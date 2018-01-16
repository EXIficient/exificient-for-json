package com.siemens.ct.exi.json.schema;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;
import org.json.JSONTokener;

public class HelperJSON2JSONSchema {

	public static JSONObject json2JsonSchema(InputStream isJson) throws JSONException  {
		JSONObject jo = new JSONObject(new JSONTokener(new InputStreamReader(isJson)));
		return json2JsonSchema(jo);
	}
	
	public static JSONObject json2JsonSchema(JSONObject jo) throws JSONException  {
		JSONObject jsonSchema = new JSONObject();
		jsonSchema.put("$schema", "http://json-schema.org/draft-04/schema#");
		jsonObject2JsonSchema(jo, jsonSchema);
		return jsonSchema;
	}
	
	
	public static void jsonObject2JsonSchema(JSONObject jo, JSONObject jsonSchema) throws JSONException  {
		jsonSchema.put("type", "object");
		JSONObject jsonSchemaProperties = new JSONObject();
		jsonSchema.put("properties", jsonSchemaProperties);
		Iterator<?> iter = jo.keys();
		while(iter.hasNext()) {
			String key = (String) iter.next();
			JSONObject jsonSub = new JSONObject();
			jsonSchemaProperties.put(key, jsonSub);
			Object value = jo.get(key);
			if(value instanceof JSONObject) {
				jsonObject2JsonSchema((JSONObject) value, jsonSub);
			} else if(value instanceof JSONArray) {
				jsonArray2JsonSchema((JSONArray) value, jsonSub);
			} else if(value instanceof JSONString || value instanceof String) {
				jsonString2JsonSchema(jsonSub);
			} else if(value instanceof Number) {
				jsonNumber2JsonSchema((Number) value, jsonSub);
			} else if(value instanceof Boolean) {
				jsonBoolean2JsonSchema(jsonSub);
			} else {
				throw new RuntimeException("Unsupported JSON type : " + value);
			}
		}
	}
	
	public static void jsonArray2JsonSchema(JSONArray ja, JSONObject jsonSchema) throws JSONException  {
		jsonSchema.put("type", "array");
		JSONObject jsonSchemaItems = new JSONObject();
		jsonSchema.put("items", jsonSchemaItems);
		
		for(int i=0; i<ja.length(); i++) {
			Object o = ja.get(i);
			// TODO check all array items
			if(o instanceof JSONObject) {
				jsonSchemaItems.put("type", "object");
				jsonObject2JsonSchema((JSONObject) o, jsonSchemaItems);
			} else if(o instanceof JSONArray) {
				jsonSchemaItems.put("type", "array");
				jsonArray2JsonSchema((JSONArray) o, jsonSchemaItems);
			} else if(o instanceof JSONString || o instanceof String) {
				jsonSchemaItems.put("type", "string");
			} else if(o instanceof Number) {
				jsonSchemaItems.put("type", "number");
			} else if(o instanceof Boolean) {
				jsonSchemaItems.put("type", "boolean");
			} else {
				throw new RuntimeException("Unsupported JSON type : " + o);
			}
		}
		
	}
	
	public static void jsonString2JsonSchema(JSONObject jsonSchema) throws JSONException  {
		jsonSchema.put("type", "string");
	}
	
	public static void jsonBoolean2JsonSchema(JSONObject jsonSchema) throws JSONException  {
		jsonSchema.put("type", "boolean");
	}
	
	public static void jsonNumber2JsonSchema(Number n, JSONObject jsonSchema) throws JSONException  {
		jsonSchema.put("type", "number");
	}
	
	public static void main(String[] args) throws JSONException {
		String s = "{\r\n" + 
				"  \"id\": 1,\r\n" + 
				"  \"name\": \"A green door\",\r\n" + 
				"  \"price\": 12.5,\r\n" + 
				"  \"checked\": false,\r\n" + 
				"  \"tags\": [\r\n" + 
				"    \"home\",\r\n" + 
				"    \"green\"\r\n" + 
				"  ]\r\n" + 
				"}";
		
		JSONObject json = new JSONObject(new JSONTokener(new StringReader(s)));
		System.out.println(json);
		
		JSONObject jsonSchema = json2JsonSchema(json);
		System.out.println(jsonSchema);
		
		
//		InputStream inputStream = TestJsonSchemaTools.class.getResourceAsStream("/path/to/your/schema.json");
//		JSONObject rawSchema = new JSONObject(new JSONTokener(new InputStreamReader(inputStream)));
		
//		JSONObject jo = new JSONObject();
//		jo.put("id", "bla");
//		JSONObject jo2 = new JSONObject();
//		jo2.put("sub", true);
//		jo.put("subobject", jo2);
//		
//		System.out.println(jo);
		
		// SchemaMapper schemaMapper = new SchemaMapper();
	}


}
