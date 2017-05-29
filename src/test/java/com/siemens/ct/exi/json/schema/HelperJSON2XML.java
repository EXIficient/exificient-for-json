package com.siemens.ct.exi.json.schema;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;

import com.siemens.ct.exi.api.sax.EXISource;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.json.EXIforJSONGenerator;

//helper to convert JSON into "internal" XML structures
public class HelperJSON2XML {
	
	public static void json2Xml(InputStream isJSON, OutputStream osXML) throws TransformerException, EXIException, IOException {
		ByteArrayOutputStream osEXI4JSON = new ByteArrayOutputStream();
		
		// encode as EXI4JSON
		EXIforJSONGenerator e4jGenerator = new EXIforJSONGenerator();
		e4jGenerator.generate(isJSON, osEXI4JSON);
		
		// decode as XML
		Result result = new StreamResult(osXML);
		InputSource is = new InputSource(new ByteArrayInputStream(osEXI4JSON.toByteArray()));
		SAXSource exiSource = new EXISource(e4jGenerator.getEXIFactory());
		exiSource.setInputSource(is);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.transform(exiSource, result);	
	}

	
	public static void main(String[] args) throws Exception {
		// InputStream isJSON = HelperJSON2XML.class.getResourceAsStream("/json-schema/fstab1.json");
		
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
		InputStream isJSON = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
		
		ByteArrayOutputStream osXML = new ByteArrayOutputStream();
		json2Xml(isJSON, osXML);
		System.out.println(new String(osXML.toByteArray()));
		
	}

}
