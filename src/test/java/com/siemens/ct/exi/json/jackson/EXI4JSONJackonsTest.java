package com.siemens.ct.exi.json.jackson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.json.JSONDataTestsV2;

public class EXI4JSONJackonsTest extends JSONDataTestsV2 {

	
	@Override
	@Test
	public void testJSIssue1() throws EXIException, IOException, JSONException {
		// Jackson seems to round data somewhere (floats simplified)
		String jsonTest = "{\n\"type\": \"FeatureCollection\",\n\"totalFeatures\": 2,\n\"features\": [\n{\n\"type\": \"Feature\",\n\"id\": \"poi.1\",\n\"geometry\": {\n\"type\": \"Point\",\n\"coordinates\": [\n40.25,\n-74.25\n]\n},\n\"geometry_name\": \"the_geom\",\n\"properties\": {\n\"NAME\": \"museam\",\n\"THUMBNAIL\": \"pics/22037827-Ti.jpg\",\n\"MAINPAGE\": \"pics/22037827-L.jpg\"\n}\n},\n{\n\"type\": \"Feature\",\n\"id\": \"poi.2\",\n\"geometry\": {\n\"type\": \"Point\",\n\"coordinates\": [\n40.25,\n-74.25\n]\n},\n\"geometry_name\": \"the_geom\",\n\"properties\": {\n\"NAME\": \"stock\",\n\"THUMBNAIL\": \"pics/22037829-Ti.jpg\",\n\"MAINPAGE\": \"pics/22037829-L.jpg\"\n}\n}\n],\n\"crs\": {\n\"type\": \"EPSG\",\n\"properties\": {\n\"code\": \"4326\"\n}\n}\n}";
		_test(jsonTest);
	}
	
	@Override
	protected int _test(String expected, List<String> sharedStrings) throws EXIException, IOException, JSONException {
		// TODO sharedStrings 
		
		
		EXI4JSONFactory fEXI = new EXI4JSONFactory();
		ObjectMapper mapperEXI = new ObjectMapper(fEXI);
		ObjectMapper mapperJSON = new ObjectMapper();

			// read JSON to node
		    JsonNode jn = mapperJSON.readTree(expected);
		    
		    // write
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    mapperEXI.writeTree(fEXI.createGenerator(baos), jn);
		    
		    
		    // read bytes again 
		    EXI4JSONParser eparser = fEXI.createParser(baos.toByteArray());
		    JsonNode actualNode = mapperJSON.readTree(eparser);
		    
		    String actualString = mapperJSON.writeValueAsString(actualNode);
		    
			JSONAssert.assertEquals(expected, actualString, true);
		
		
		    return baos.size();
	}
	
}
