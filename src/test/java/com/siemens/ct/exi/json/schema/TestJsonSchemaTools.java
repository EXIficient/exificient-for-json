package com.siemens.ct.exi.json.schema;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Test;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.json.EXIforJSONGenerator;

public class TestJsonSchemaTools {

	@Test
	public void testBasic() throws Exception {
		String resourceJson = "/json-schema/basic1.json";
		String resourceJsonSchema = "/json-schema/basic.schema.json";
		_test(resourceJson, resourceJsonSchema);
	}
	
	@Test
	public void testFstab() throws Exception {
		String resourceJson = "/json-schema/fstab1.json";
		String resourceJsonSchema = "/json-schema/fstab.schema.json";
		_test(resourceJson, resourceJsonSchema);
	}
	
	@Test
	public void testSample1() throws Exception {
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
		// System.out.println(json);
		
		JSONObject jsonSchema = HelperJSON2JSONSchema.json2JsonSchema(json);
		System.out.println(jsonSchema);
		
		// built-in schema
		byte[] ba = _testA(new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8)));
		
		// JSON schema
		InputStream isJsonSchema = new ByteArrayInputStream(jsonSchema.toString().getBytes(StandardCharsets.UTF_8));
		byte[] bb = _testB(new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8)), isJsonSchema);
		
		_compare(ba, bb);
	}
	
	@Test
	public void testWoT1() throws Exception {
		String resourceJson = "/json-schema/wot1.jsonld";
		JSONObject json = new JSONObject(new JSONTokener(new InputStreamReader(HelperJSON2XML.class.getResourceAsStream(resourceJson))));
		// System.out.println(json);
		
		JSONObject jsonSchema = HelperJSON2JSONSchema.json2JsonSchema(json);
		System.out.println(jsonSchema);
		
		// built-in schema
		byte[] ba = _testA(HelperJSON2XML.class.getResourceAsStream(resourceJson));
		
		// JSON schema
		InputStream isJsonSchema = new ByteArrayInputStream(jsonSchema.toString().getBytes(StandardCharsets.UTF_8));
		byte[] bb = _testB(HelperJSON2XML.class.getResourceAsStream(resourceJson), isJsonSchema);
		
		_compare(ba, bb);
	}
	
	
	private void _test(String resourceJson, String resourceJsonSchema) throws Exception {
		// built-in schema
		byte[] ba = _testA(HelperJSON2XML.class.getResourceAsStream(resourceJson));
		
		// JSON schema
		InputStream isJsonSchema = HelperJSON2XML.class.getResourceAsStream(resourceJsonSchema);
		byte[] bb = _testB(HelperJSON2XML.class.getResourceAsStream(resourceJson), isJsonSchema);

		_compare(ba, bb);
	}
	
	private void _compare(byte[] ba, byte[] bb) {
		// compare
		System.out.println("EXI4JSON = " + ba.length + "Bytes. with dedicated schema: " + bb.length + "Bytes.");
	}
	
	private byte[] _testA(InputStream isJson) throws Exception {
		// generate EXI4JSON with built-in schema
		ByteArrayOutputStream os1 = new ByteArrayOutputStream();
		EXIforJSONGenerator e4jGenerator1 = new EXIforJSONGenerator();
		e4jGenerator1.generate(isJson, os1);
		
		return os1.toByteArray();
	}
	
	private byte[] _testB(InputStream isJson, InputStream isJsonSchema) throws Exception {
		// generate EXI4JSON with generated schema from JsonSchema
		ByteArrayOutputStream osXSD = new ByteArrayOutputStream();
		HelperJSONSchema2XSD.jsonSchema2Xsd(isJsonSchema, osXSD);
		ByteArrayOutputStream os2 = new ByteArrayOutputStream();
		EXIFactory ef = DefaultEXIFactory.newInstance();
		// set to strict
		ef.getFidelityOptions().setFidelity(FidelityOptions.FEATURE_STRICT, true);
		// set grammar
		ef.setGrammars(GrammarFactory.newInstance().createGrammars(new ByteArrayInputStream(osXSD.toByteArray())));
		// 
		EXIforJSONGenerator e4jGenerator2 = new EXIforJSONGenerator(ef);
		e4jGenerator2.generate(isJson, os2);
		
		return os2.toByteArray();
		
	}

}
