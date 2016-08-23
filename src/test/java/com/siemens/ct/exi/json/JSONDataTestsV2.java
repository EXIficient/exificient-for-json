package com.siemens.ct.exi.json;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;

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
		
		if(sharedStrings == null) {
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

}
