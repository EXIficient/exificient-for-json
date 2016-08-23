package com.siemens.ct.exi.json;

import java.io.ByteArrayInputStream;
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
import org.skyscreamer.jsonassert.JSONAssert;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

import junit.framework.TestCase;

public class JSONDataTestsV1 extends TestCase {

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

	@Test
	public void testCar() throws EXIException, IOException, JSONException {
		URL url = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/car.jsonld");
		_test(readURL(url));
		_test(readURL(url), sharedStrings);
	}

	@Test
	public void testOutlet() throws EXIException, IOException, JSONException {
		URL url = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/outlet.jsonld");
		_test(readURL(url));
	}

	@Test
	public void testFancyLed() throws EXIException, IOException, JSONException {
		URL url = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/fancy_led.jsonld");
		_test(readURL(url));
		_test(readURL(url), sharedStrings);
	}
	
	
	@Test
	public void testJSIssue1() throws EXIException, IOException, JSONException {
		String jsonTest = "{\n\"type\": \"FeatureCollection\",\n\"totalFeatures\": 2,\n\"features\": [\n{\n\"type\": \"Feature\",\n\"id\": \"poi.1\",\n\"geometry\": {\n\"type\": \"Point\",\n\"coordinates\": [\n40.707587626256554,\n-74.01046109936333\n]\n},\n\"geometry_name\": \"the_geom\",\n\"properties\": {\n\"NAME\": \"museam\",\n\"THUMBNAIL\": \"pics/22037827-Ti.jpg\",\n\"MAINPAGE\": \"pics/22037827-L.jpg\"\n}\n},\n{\n\"type\": \"Feature\",\n\"id\": \"poi.2\",\n\"geometry\": {\n\"type\": \"Point\",\n\"coordinates\": [\n40.70754683896324,\n-74.0108375113659\n]\n},\n\"geometry_name\": \"the_geom\",\n\"properties\": {\n\"NAME\": \"stock\",\n\"THUMBNAIL\": \"pics/22037829-Ti.jpg\",\n\"MAINPAGE\": \"pics/22037829-L.jpg\"\n}\n}\n],\n\"crs\": {\n\"type\": \"EPSG\",\n\"properties\": {\n\"code\": \"4326\"\n}\n}\n}";
		_test(jsonTest);
	}
	

	protected int _test(String expected) throws EXIException, IOException, JSONException {
		return _test(expected, null);
	}
	
	protected int _test(String expected, List<String> sharedStrings) throws EXIException, IOException, JSONException {
		EXIforJSONGenerator e4jGenerator;
		EXIforJSONParser e4jParser;
		
		if(sharedStrings == null) {
			e4jGenerator = new EXIforJSONGenerator(EXI4JSONConstants.XML_SCHEMA_FOR_JSON);
			e4jParser = new EXIforJSONParser(EXI4JSONConstants.XML_SCHEMA_FOR_JSON);
		} else {
			EXIFactory ef = DefaultEXIFactory.newInstance();
			ef.setSharedStrings(sharedStrings);
			
			e4jGenerator = new EXIforJSONGenerator(ef, EXI4JSONConstants.XML_SCHEMA_FOR_JSON);
			e4jParser = new EXIforJSONParser(ef, EXI4JSONConstants.XML_SCHEMA_FOR_JSON);
		}
		
		// generate exi-for-json
		InputStream is = new ByteArrayInputStream(expected.getBytes()); //
		ByteArrayOutputStream baosEXI = new ByteArrayOutputStream();
		e4jGenerator.generate(is, baosEXI);

		// parse exi-for-json again

		ByteArrayOutputStream baosJSON = new ByteArrayOutputStream();
		e4jParser.parse(new ByteArrayInputStream(baosEXI.toByteArray()), baosJSON);
		String actual = new String(baosJSON.toByteArray());
		// System.out.println(actual);

		// compare the 2 JSON documents
		// String expected = readFile(path);
		// actual = actual.replace("1.0", "1.001"); // test
		JSONAssert.assertEquals(expected, actual, true);
		
		return baosEXI.size();
	}

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
