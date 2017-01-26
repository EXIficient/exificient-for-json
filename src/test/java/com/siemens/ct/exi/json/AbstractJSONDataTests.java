package com.siemens.ct.exi.json;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.junit.Test;

import com.siemens.ct.exi.exceptions.EXIException;

import junit.framework.TestCase;

public abstract class AbstractJSONDataTests extends TestCase {

	List<String> sharedStrings = Arrays.asList(new String[] { "@context", "@id", "@type", "@value", "Brightness", "Car",
			"CoAP", "DecreaseColor", "Distance", "Door", "EXI", "EXI4JSON", "Fan", "HTTP", "IncreaseColor", "JSON",
			"Lamp", "Lighting", "Off", "On", "OnOff", "PowerConsumption", "RGBColor", "RGBColorBlue", "RGBColorGreen",
			"RGBColorRed", "Speed", "Start", "Stop", "Switch", "Temperature", "Thing", "Toggle", "TrafficLight", "WS",
			"actions", "associations", "celsius", "dogont", "encodings", "events", "hrefs",
			"http://w3c.github.io/wot/w3c-wot-td-context.jsonld",
			"https://w3c.github.io/wot/w3c-wot-common-context.jsonld", "inch", "inputData", "interactions", "joule",
			"kelvin", "kmh", "kwh", "lgdo", "m", "max", "mile", "min", "mm", "mph", "name", "outputData", "properties",
			"protocols", "qu", "reference", "schema", "security", "unit", "uris", "valueType", "writable",
			"xsd:boolean", "xsd:byte", "xsd:float", "xsd:int", "xsd:short", "xsd:string", "xsd:unsignedByte",
			"xsd:unsignedInt", "xsd:unsignedShort" });

	// @Test
	// public void testBrightnessProximitySensor() throws EXIException,
	// IOException, JSONException {
	// String path = "D:/Projects/WoT/wot/TF-TD/TD
	// Samples/brightnessProximitySensor.jsonld";
	// _test(readFile(path));
	// }
	
	// URL url = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/car.jsonld");
	String sCar = "{\r\n" + 
			"    \"@context\": \"http://w3c.github.io/wot/w3c-wot-td-context.jsonld\",\r\n" + 
			"    \"metadata\": {\r\n" + 
			"        \"name\": \"MyCar\",\r\n" + 
			"        \"protocols\" : {\r\n" + 
			"            \"HTTP\" : {\r\n" + 
			"                \"uri\" : \"http://100.64.24.55:3000/api/\",\r\n" + 
			"                \"priority\" : 1\r\n" + 
			"            }\r\n" + 
			"        },\r\n" + 
			"        \"encodings\": [\r\n" + 
			"            \"JSON\"\r\n" + 
			"        ]\r\n" + 
			"    },\r\n" + 
			"    \"interactions\": [\r\n" + 
			"        {\r\n" + 
			"            \"@type\": \"Property\",\r\n" + 
			"            \"name\": \"vehicleSpeed\",\r\n" + 
			"            \"outputData\": \"xsd:unsignedShort\",\r\n" + 
			"            \"writable\": false\r\n" + 
			"        }\r\n" + 
			"    ]\r\n" + 
			"}";

	@Test
	public void testCar() throws EXIException, IOException, JSONException {
		_test(sCar);
		_test(sCar, sharedStrings);
	}

	
	// URL url = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/outlet.jsonld");
	String sOutlet = "{\r\n" + 
			"  \"@context\": \"http://w3c.github.io/wot/w3c-wot-td-context.jsonld\",\r\n" + 
			"  \"metadata\": {\r\n" + 
			"    \"name\": \"MyOutlet\",\r\n" + 
			"    \"protocols\" : {\r\n" + 
			"      \"WS\" : {\r\n" + 
			"        \"uri\" : \"ws://www.example.com:80/outlet\",\r\n" + 
			"        \"priority\" : 1\r\n" + 
			"      },\r\n" + 
			"      \"HTTP\" : {\r\n" + 
			"        \"uri\" : \"http://www.example.com:80/outlet\",\r\n" + 
			"        \"priority\" : 2\r\n" + 
			"      }\r\n" + 
			"    },\r\n" + 
			"    \"encodings\": [\r\n" + 
			"      \"JSON\"\r\n" + 
			"    ]\r\n" + 
			"  },\r\n" + 
			"  \"interactions\": [\r\n" + 
			"    {\r\n" + 
			"      \"@type\": \"Property\",\r\n" + 
			"      \"name\": \"powerStateOn\",\r\n" + 
			"      \"outputData\": \"xsd:boolean\",\r\n" + 
			"      \"writable\": true\r\n" + 
			"    },{\r\n" + 
			"      \"@type\": \"Property\",\r\n" + 
			"      \"name\": \"outletInUse\",\r\n" + 
			"      \"outputData\": \"xsd:boolean\",\r\n" + 
			"      \"writable\": false\r\n" + 
			"    },{\r\n" + 
			"      \"@type\": \"Property\",\r\n" + 
			"      \"name\": \"powerConsumption\",\r\n" + 
			"      \"outputData\": \"xsd:float\",\r\n" + 
			"      \"writable\": false\r\n" + 
			"    },{\r\n" + 
			"      \"@type\": \"Event\",\r\n" + 
			"      \"outputData\": \"xsd:boolean\",\r\n" + 
			"      \"name\": \"powerStateChanged\"\r\n" + 
			"    },{\r\n" + 
			"      \"@type\": \"Event\",\r\n" + 
			"      \"outputData\": \"xsd:boolean\",\r\n" + 
			"      \"name\": \"outletUsageChanged\"\r\n" + 
			"    },{\r\n" + 
			"      \"@type\": \"Event\",\r\n" + 
			"      \"outputData\": \"xsd:float\",\r\n" + 
			"      \"name\": \"powerConsumptionChanged\"\r\n" + 
			"    }\r\n" + 
			"  ]\r\n" + 
			"}";
	
	@Test
	public void testOutlet() throws EXIException, IOException, JSONException {
		_test(sOutlet);
	}

	// URL url = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/fancy_led.jsonld");
	String sFancyLed = "{\r\n" + 
			"  \"@context\": \"http://w3c.github.io/wot/w3c-wot-td-context.jsonld\",\r\n" + 
			"  \"metadata\": {\r\n" + 
			"    \"name\": \"MyLED\",\r\n" + 
			"    \"protocols\" : {\r\n" + 
			"      \"CoAP\" : {\r\n" + 
			"        \"uri\" : \"coap://192.168.1.123:5683/things/MyLED\",\r\n" + 
			"        \"priority\" : 1\r\n" + 
			"		  },\r\n" + 
			"      \"HTTP\" : {\r\n" + 
			"        \"uri\" : \"http://192.168.1.123:8080/things/MyLED\",\r\n" + 
			"        \"priority\" : 2\r\n" + 
			"      }\r\n" + 
			"	  },\r\n" + 
			"    \"encodings\": [\r\n" + 
			"      \"JSON\"\r\n" + 
			"    ]\r\n" + 
			"  },\r\n" + 
			"  \"interactions\": [\r\n" + 
			"     {\r\n" + 
			"      \"@type\": \"Property\",\r\n" + 
			"      \"name\": \"brightness\",\r\n" + 
			"      \"outputData\": \"xsd:unsignedByte\",\r\n" + 
			"      \"writable\": true\r\n" + 
			"     }, {\r\n" + 
			"      \"@type\": \"Property\",\r\n" + 
			"      \"name\": \"colorTemperature\",\r\n" + 
			"      \"outputData\": \"xsd:unsignedShort\",\r\n" + 
			"      \"writable\": true\r\n" + 
			"    }, {\r\n" + 
			"      \"@type\": \"Property\",\r\n" + 
			"      \"name\": \"rgbValueRed\",\r\n" + 
			"      \"outputData\": \"xsd:unsignedByte\",\r\n" + 
			"      \"writable\": true\r\n" + 
			"    }, {\r\n" + 
			"      \"@type\": \"Property\",\r\n" + 
			"      \"name\": \"rgbValueGreen\",\r\n" + 
			"      \"outputData\": \"xsd:unsignedByte\",\r\n" + 
			"      \"writable\": true\r\n" + 
			"    }, {\r\n" + 
			"      \"@type\": \"Property\",\r\n" + 
			"      \"name\": \"rgbValueBlue\",\r\n" + 
			"      \"outputData\": \"xsd:unsignedByte\",\r\n" + 
			"      \"writable\": true\r\n" + 
			"    }, {\r\n" + 
			"      \"@type\": \"Action\",\r\n" + 
			"      \"name\": \"ledOnOff\",\r\n" + 
			"      \"inputData\": \"xsd:boolean\",\r\n" + 
			"      \"outputData\": \"\"\r\n" + 
			"    }, {\r\n" + 
			"      \"@type\": \"Action\",\r\n" + 
			"      \"name\": \"fadeIn\",\r\n" + 
			"      \"inputData\": \"xsd:unsignedByte\",\r\n" + 
			"      \"outputData\": \"\"\r\n" + 
			"    },  {\r\n" + 
			"      \"@type\": \"Action\",\r\n" + 
			"      \"name\": \"fadeOut\",\r\n" + 
			"      \"inputData\": \"xsd:unsignedByte\",\r\n" + 
			"      \"outputData\": \"\"\r\n" + 
			"    },  {\r\n" + 
			"      \"@type\": \"Action\",\r\n" + 
			"      \"name\": \"trafficLight\",\r\n" + 
			"      \"inputData\": \"xsd:boolean\",\r\n" + 
			"      \"outputData\": \"\"\r\n" + 
			"    }, {\r\n" + 
			"      \"@type\": \"Event\",\r\n" + 
			"      \"outputData\": \"xsd:unsignedShort\",\r\n" + 
			"      \"name\": \"colorTemperatureChanged\"\r\n" + 
			"    }\r\n" + 
			"  ]\r\n" + 
			"}";
	
	@Test
	public void testFancyLed() throws EXIException, IOException, JSONException {
		_test(sFancyLed);
		_test(sFancyLed, sharedStrings);
	}

	@Test
	public void testJSIssue1() throws EXIException, IOException, JSONException {
		String jsonTest = "{\n\"type\": \"FeatureCollection\",\n\"totalFeatures\": 2,\n\"features\": [\n{\n\"type\": \"Feature\",\n\"id\": \"poi.1\",\n\"geometry\": {\n\"type\": \"Point\",\n\"coordinates\": [\n40.707587626256554,\n-74.01046109936333\n]\n},\n\"geometry_name\": \"the_geom\",\n\"properties\": {\n\"NAME\": \"museam\",\n\"THUMBNAIL\": \"pics/22037827-Ti.jpg\",\n\"MAINPAGE\": \"pics/22037827-L.jpg\"\n}\n},\n{\n\"type\": \"Feature\",\n\"id\": \"poi.2\",\n\"geometry\": {\n\"type\": \"Point\",\n\"coordinates\": [\n40.70754683896324,\n-74.0108375113659\n]\n},\n\"geometry_name\": \"the_geom\",\n\"properties\": {\n\"NAME\": \"stock\",\n\"THUMBNAIL\": \"pics/22037829-Ti.jpg\",\n\"MAINPAGE\": \"pics/22037829-L.jpg\"\n}\n}\n],\n\"crs\": {\n\"type\": \"EPSG\",\n\"properties\": {\n\"code\": \"4326\"\n}\n}\n}";
		_test(jsonTest);
	}

	// Key-name Escaping: Conflict with existing EXI4JSON global schema element
	// name "number"
	// {"a number": 1}
	@Test
	public void testKeynameEscapingConflictEXI4JSONName1() throws EXIException, IOException, JSONException {
		String jsonTest = "{\r\n" + "	\"number\": 1\r\n" + "}";
		_test(jsonTest);
	}
	
	// Key-name Escaping: Conflict with NCName character(s) "a number"
	// {"number": 1}
	@Test
	public void testKeynameEscapingConflictNCName1() throws EXIException, IOException, JSONException {
		String jsonTest = "{\r\n" + "	\"a number\": 2\r\n" + "}";
		_test(jsonTest);
	}

	/**
	 * write JSON to EXI4JSON and deserialize it again and compare (without sharedStrings)
	 * 
	 * @param expected
	 * @return
	 * @throws EXIException
	 * @throws IOException
	 * @throws JSONException
	 */
	abstract protected int _test(String expected) throws EXIException, IOException, JSONException;

	/**
	 * write JSON to EXI4JSON and deserialize it again and compare (with sharedStrings)
	 * 
	 * @param expected
	 * @param sharedStrings
	 * @return
	 * @throws EXIException
	 * @throws IOException
	 * @throws JSONException
	 */
	abstract protected int _test(String expected, List<String> sharedStrings)
			throws EXIException, IOException, JSONException;

	static String readFile(String path) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded);
	}

	static String readURL(URL url) throws IOException {
		InputStream in = url.openStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[8192];
		int len = 0;
		while ((len = in.read(buf)) != -1) {
			baos.write(buf, 0, len);
		}
		return new String(baos.toByteArray());
	}

}
