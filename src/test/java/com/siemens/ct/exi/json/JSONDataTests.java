package com.siemens.ct.exi.json;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.siemens.ct.exi.exceptions.EXIException;

import junit.framework.TestCase;

public class JSONDataTests extends TestCase {

//	@Test
//	public void testBrightnessProximitySensor() throws EXIException, IOException, JSONException {
//		String path = "D:/Projects/WoT/wot/TF-TD/TD Samples/brightnessProximitySensor.jsonld";
//		_test(readFile(path));
//	}
	
	@Test
	public void testCar() throws EXIException, IOException, JSONException {
		URL url = new URL("https://raw.githubusercontent.com/w3c/wot/master/TF-TD/TD%20Samples/car.jsonld");
		_test(readURL(url));
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
	}

	protected void _test(String expected) throws EXIException, IOException, JSONException {
		// generate exi-for-json
		InputStream is = new ByteArrayInputStream(expected.getBytes()); // 
		EXIforJSONGenerator e4jGenerator = new EXIforJSONGenerator();
		ByteArrayOutputStream baosEXI = new ByteArrayOutputStream();
		e4jGenerator.generate(is, baosEXI);

		// parse exi-for-json again
		EXIforJSONParser e4jParser = new EXIforJSONParser();
		ByteArrayOutputStream baosJSON = new ByteArrayOutputStream();
		e4jParser.parse(new ByteArrayInputStream(baosEXI.toByteArray()), baosJSON);

		String actual = new String(baosJSON.toByteArray());
		// System.out.println(actual);

		// compare the 2 JSON documents
		// String expected = readFile(path);
		// actual = actual.replace("1.0", "1.001"); // test
		JSONAssert.assertEquals(expected, actual, true);
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
