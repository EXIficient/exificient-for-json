package com.siemens.ct.exi.json;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

public class JSONDataTestsV2 extends AbstractJSONDataTests {

	protected int _test(String expected) throws EXIException, IOException, JSONException {
		return _test(expected, null);
	}

	
	protected int _test(String expected, List<String> sharedStrings) throws EXIException, IOException, JSONException {
		EXIforJSONGenerator e4jGenerator;
		EXIforJSONParser e4jParser;

		if (sharedStrings == null) {
			e4jGenerator = new EXIforJSONGenerator();
			e4jParser = new EXIforJSONParser();
		} else {
			EXIFactory ef = DefaultEXIFactory.newInstance();
			ef.setSharedStrings(sharedStrings);

			e4jGenerator = new EXIforJSONGenerator(ef);
			e4jParser = new EXIforJSONParser(ef);
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

	@Test
	public void testEscapeKey0() throws EXIException, IOException, JSONException {
		String key = "normalKey";
		EXIforJSONGenerator e4jGenerator = new EXIforJSONGenerator();
		String ekey = e4jGenerator.escapeKey(key);

		assertTrue(key.equals(ekey));

		EXIforJSONParser e4jParser = new EXIforJSONParser();
		String ukey = e4jParser.unescapeKey(ekey);
		assertTrue(ukey.equals(key));
	}

	@Test
	public void testEscapeKey1() throws EXIException, IOException, JSONException {
		String key = EXI4JSONConstants.LOCALNAME_NUMBER; // "number"
		EXIforJSONGenerator e4jGenerator = new EXIforJSONGenerator();
		String ekey = e4jGenerator.escapeKey(key);

		assertFalse(key.equals(ekey));
		assertTrue((String.valueOf(EXI4JSONConstants.ESCAPE_START_CHARACTER)
				+ String.valueOf(EXI4JSONConstants.ESCAPE_END_CHARACTER) + EXI4JSONConstants.LOCALNAME_NUMBER)
						.equals(ekey));

		EXIforJSONParser e4jParser = new EXIforJSONParser();
		String ukey = e4jParser.unescapeKey(ekey);
		assertTrue(ukey.equals(key));
	}

	@Test
	public void testEscapeKey2() throws EXIException, IOException, JSONException {
		String key = "a number";
		EXIforJSONGenerator e4jGenerator = new EXIforJSONGenerator();
		String ekey = e4jGenerator.escapeKey(key);

		assertFalse(key.equals(ekey));
		assertTrue("a_32.number".equals(ekey));

		EXIforJSONParser e4jParser = new EXIforJSONParser();
		String ukey = e4jParser.unescapeKey(ekey);
		assertTrue(ukey.equals(key));
	}

	@Test
	public void testEscapeKey3() throws EXIException, IOException, JSONException {
		String key = "_foo";
		EXIforJSONGenerator e4jGenerator = new EXIforJSONGenerator();
		String ekey = e4jGenerator.escapeKey(key);

		assertFalse(key.equals(ekey));
		assertTrue("_95.foo".equals(ekey));

		EXIforJSONParser e4jParser = new EXIforJSONParser();
		String ukey = e4jParser.unescapeKey(ekey);
		assertTrue(ukey.equals(key));
	}

	@Test
	public void testEscapeKey4() throws EXIException, IOException, JSONException {
		String key = "foo_.A";
		EXIforJSONGenerator e4jGenerator = new EXIforJSONGenerator();
		String ekey = e4jGenerator.escapeKey(key);

		assertFalse(key.equals(ekey));
		assertTrue("foo_95..A".equals(ekey));

		EXIforJSONParser e4jParser = new EXIforJSONParser();
		String ukey = e4jParser.unescapeKey(ekey);
		assertTrue(ukey.equals(key));
	}

	@Test
	public void testEscapeKey5() throws EXIException, IOException, JSONException {
		// High surrogate pair
		byte[] data = { 0, 0x41, // A
				(byte) 0xD8, 1, // High surrogate
				(byte) 0xDC, 2, // Low surrogate
				0, 0x42, // B
		};

		String key = new String(data, "UTF-16");

		EXIforJSONGenerator e4jGenerator = new EXIforJSONGenerator();
		String ekey = e4jGenerator.escapeKey(key);

		assertFalse(key.equals(ekey));
		assertTrue("A_66562.B".equals(ekey));

		EXIforJSONParser e4jParser = new EXIforJSONParser();
		String ukey = e4jParser.unescapeKey(ekey);
		assertTrue(ukey.equals(key));
	}
	
	public static void main(String[] args) throws EXIException, IOException {
		String json = "{\"keyNumber\": 123}";
		EXIFactory ef = DefaultEXIFactory.newInstance();
		ef.setCodingMode(CodingMode.BYTE_PACKED);
		EXIforJSONGenerator e4jGenerator = new EXIforJSONGenerator(ef);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		e4jGenerator.generate(new ByteArrayInputStream(json.getBytes()), baos);
		System.out.println(baos.size());
		
	}

}
