package com.siemens.ct.exi.json.schema;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

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
	
	private void _test(String resourceJson, String resourceJsonSchema) throws Exception {
		
		// generate EXI4JSON with built-in schema
		ByteArrayOutputStream os1 = new ByteArrayOutputStream();
		EXIforJSONGenerator e4jGenerator1 = new EXIforJSONGenerator();
		e4jGenerator1.generate(HelperJSON2XML.class.getResourceAsStream(resourceJson), os1);
		
		// generate EXI4JSON with generated schema from JsonSchema
		ByteArrayOutputStream osXSD = new ByteArrayOutputStream();
		HelperJSONSchema2XSD.jsonSchema2Xsd(HelperJSON2XML.class.getResourceAsStream(resourceJsonSchema), osXSD);
		ByteArrayOutputStream os2 = new ByteArrayOutputStream();
		EXIFactory ef = DefaultEXIFactory.newInstance();
		// set to strict
		ef.getFidelityOptions().setFidelity(FidelityOptions.FEATURE_STRICT, true);
		// set grammar
		ef.setGrammars(GrammarFactory.newInstance().createGrammars(new ByteArrayInputStream(osXSD.toByteArray())));
		// 
		EXIforJSONGenerator e4jGenerator2 = new EXIforJSONGenerator(ef);
		e4jGenerator2.generate( HelperJSON2XML.class.getResourceAsStream(resourceJson), os2);
		
		// compare
		System.out.println("EXI4JSON = " + os1.size() + "Bytes. with dedicated schema: " + os2.size() + "Bytes.");
	}

}
