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
				"	\"@context\": [\r\n" + 
				"		\"http://w3c.github.io/wot/w3c-wot-td-context.jsonld\",\r\n" + 
				"		{ \"actuator\": \"http://example.org/actuator#\" }\r\n" + 
				"	],\r\n" + 
				"	\"@type\": [\"Thing\"],\r\n" + 
				"	\"name\": \"MyLEDThing\",\r\n" + 
				"	\"base\": \"coap://myled.example.com:5683/\",\r\n" + 
				"	\"security\": {\r\n" + 
				"			\"cat\": \"token:jwt\",\r\n" + 
				"			\"alg\": \"HS256\",\r\n" + 
				"			\"as\": \"https://authority-issuing.example.org\"\r\n" + 
				"		},\r\n" + 
				"	\"interaction\": [\r\n" + 
				"		{\r\n" + 
				"			\"@type\": [\"Property\",\"actuator:onOffStatus\"],\r\n" + 
				"			\"name\": \"status\",\r\n" + 
				"			\"outputData\": { \"type\": \"boolean\" },\r\n" + 
				"			\"writable\": true,\r\n" + 
				"			\"link\": [{\r\n" + 
				"				\"href\" : \"pwr\", \r\n" + 
				"				\"mediaType\": \"application/exi\" \r\n" + 
				"			},\r\n" + 
				"			{\r\n" + 
				"				\"href\" : \"http://mytemp.example.com:8080/status\",\r\n" + 
				"				\"mediaType\": \"application/json\"\r\n" + 
				"			}]\r\n" + 
				"		},\r\n" + 
				"		{\r\n" + 
				"			\"@type\": [\"Action\",\"actuator:fadeIn\"],\r\n" + 
				"			\"name\": \"fadeIn\",\r\n" + 
				"			\"inputData\": { \"type\": \"integer\" },\r\n" + 
				"			\"link\": [{\r\n" + 
				"				\"href\" : \"in\", \r\n" + 
				"				\"mediaType\": \"application/exi\" \r\n" + 
				"			},\r\n" + 
				"			{\r\n" + 
				"				\"href\" : \"http://mytemp.example.com:8080/in\",\r\n" + 
				"				\"mediaType\": \"application/json\"\r\n" + 
				"			}]									\r\n" + 
				"		},\r\n" + 
				"		{\r\n" + 
				"			\"@type\": [\"Action\",\"actuator:fadeOut\"],\r\n" + 
				"			\"name\": \"fadeOut\",\r\n" + 
				"			\"inputData\": { \"type\": \"integer\" },\r\n" + 
				"			\"link\": [{\r\n" + 
				"				\"href\" : \"out\", \r\n" + 
				"				\"mediaType\": \"application/exi\" \r\n" + 
				"			},\r\n" + 
				"			{\r\n" + 
				"				\"href\" : \"http://mytemp.example.com:8080/out\",\r\n" + 
				"				\"mediaType\": \"application/json\"\r\n" + 
				"			}]									\r\n" + 
				"		},\r\n" + 
				"		{\r\n" + 
				"			\"@type\": [\"Event\",\"actuator:alert\"],\r\n" + 
				"			\"name\": \"criticalCondition\",\r\n" + 
				"			\"outputData\": { \"type\": \"string\" },\r\n" + 
				"			\"link\": [{\r\n" + 
				"              \"href\" : \"ev\",\r\n" + 
				"              \"mediaType\": \"application/exi\"\r\n" + 
				"            }]	\r\n" + 
				"		}\r\n" + 
				"	]\r\n" + 
				"}";
		InputStream isJSON = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
		
		ByteArrayOutputStream osXML = new ByteArrayOutputStream();
		json2Xml(isJSON, osXML);
		System.out.println(new String(osXML.toByteArray()));
		
	}

}
