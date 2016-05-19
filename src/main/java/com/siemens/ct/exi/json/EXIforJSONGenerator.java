/*
 * Copyright (c) 2007-2016 Siemens AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package com.siemens.ct.exi.json;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

import com.siemens.ct.exi.EXIBodyEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.EXIStreamEncoder;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.Grammars;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.values.StringValue;

public class EXIforJSONGenerator extends AbstractEXIforJSON {
	
	public EXIforJSONGenerator() throws EXIException, IOException {
		super();
	}
	
	public EXIforJSONGenerator(EXIFactory ef) throws EXIException, IOException {
		super(ef);
	}
	
	static PrintStream DEBUG = null;
	// static final boolean DEBUG = false;

	
	static void printDebug(String s) {
		if(DEBUG != null) {
			DEBUG.println(s);	
		}
	}
	
	static void printDebugInd(int ind, String s) {
		if(DEBUG != null) {
			for(int i=0; i<ind; i++) {
				DEBUG.print("\t");
			}
			DEBUG.println(s);	
		}
	}
	
	public void generate(InputStream is, OutputStream os) throws EXIException, IOException {
		EXIStreamEncoder streamEncoder = ef.createEXIStreamEncoder();
		
		EXIBodyEncoder bodyEncoder = streamEncoder.encodeHeader(os);
		
		JsonParser parser = Json.createParser(is);
		
		int ind = 0;
		String key = null;
		
		bodyEncoder.encodeStartDocument();
		
		while (parser.hasNext()) {
			Event e = parser.next();
			switch(e) {
			case KEY_NAME:
				key = parser.getString();
				break;
			case START_OBJECT:
				bodyEncoder.encodeStartElement(NAMESPACE_EXI4JSON, LOCALNAME_MAP, null);
				if(key == null) {
					printDebugInd(ind, "<map>");
				} else {
					printDebugInd(ind, "<map key=\"" + key + "\">");
					bodyEncoder.encodeAttribute("", LOCALNAME_KEY, null, new StringValue(key));
					key = null;
				}
				ind++;
				break;
			case END_OBJECT:
				ind--;
				printDebugInd(ind, "</map>");
				bodyEncoder.encodeEndElement();
				break;
			case START_ARRAY:
				bodyEncoder.encodeStartElement(NAMESPACE_EXI4JSON, LOCALNAME_ARRAY, null);
				if(key == null) {
					printDebugInd(ind, "<array>");
				} else {
					printDebugInd(ind, "<array key=\"" + key + "\">");
					bodyEncoder.encodeAttribute("", LOCALNAME_KEY, null, new StringValue(key));
					key = null;
				}
				ind++;
				break;
			case END_ARRAY:
				ind--;
				printDebugInd(ind, "</array>");
				bodyEncoder.encodeEndElement();
				break;
			case VALUE_STRING:
				bodyEncoder.encodeStartElement(NAMESPACE_EXI4JSON, LOCALNAME_STRING, null);
				bodyEncoder.encodeCharacters(new StringValue(parser.getString()));
				if(key == null) {
					printDebugInd(ind, "<string>" + parser.getString() + "</string>");
				} else {
					printDebugInd(ind, "<string key=\"" + key + "\">" + parser.getString() + "</string>");
					bodyEncoder.encodeAttribute("", LOCALNAME_KEY, null, new StringValue(key));
					key = null;
				}
				bodyEncoder.encodeEndElement();
				break;
			case VALUE_NUMBER:
				bodyEncoder.encodeStartElement(NAMESPACE_EXI4JSON, LOCALNAME_NUMBER, null);
				bodyEncoder.encodeCharacters(new StringValue(parser.getString()));
				if(key == null) {
					printDebugInd(ind, "<number>" + parser.getString() + "</number>");
				} else {
					printDebugInd(ind, "<number key=\"" + key + "\">" + parser.getString() + "</number>");
					bodyEncoder.encodeAttribute("", LOCALNAME_KEY, null, new StringValue(key));
					key = null;
				}
				bodyEncoder.encodeEndElement();
				break;
			case VALUE_FALSE:
			case VALUE_TRUE:
				bodyEncoder.encodeStartElement(NAMESPACE_EXI4JSON, LOCALNAME_BOOLEAN, null);
				bodyEncoder.encodeCharacters(new StringValue((e == Event.VALUE_FALSE ? "false" : "true")));
				if(key == null) {
					printDebugInd(ind, "<boolean>" + parser.getString() + "</boolean>");
				} else {
					printDebugInd(ind, "<boolean key=\"" + key + "\">" + (e == Event.VALUE_FALSE ? "false" : "true") + "</boolean>");
					bodyEncoder.encodeAttribute("", LOCALNAME_KEY, null, new StringValue(key));
					key = null;
				}
				bodyEncoder.encodeEndElement();
				break;
			case VALUE_NULL:
				bodyEncoder.encodeStartElement(NAMESPACE_EXI4JSON, LOCALNAME_NULL, null);
				if(key == null) {
					printDebugInd(ind, "<null />" );
				} else {
					printDebugInd(ind, "<null key=\"" + key + "\" />");
					bodyEncoder.encodeAttribute("", LOCALNAME_KEY, null, new StringValue(key));
					key = null;
				}
				bodyEncoder.encodeEndElement();
				break;
			default:
				throw new RuntimeException("Not supported JSON event: " + e);
			}
		}
		
		bodyEncoder.encodeEndDocument();
		bodyEncoder.flush();
	}
	
	
	public static void main(String[] args) throws FileNotFoundException, EXIException, IOException {
		// TEST V2
		// String json = "./../../../W3C/EXI/docs/json/V2/personnel_one.json";
		// String json = "./../../../W3C/EXI/docs/json/V2/personnel_three.json";
		
		
		
		
		List<String> jsons = new ArrayList<String>();
		// Taki
		jsons.add("./../../../W3C/EXI/docs/json/V2/personnel_one.json");
		jsons.add("./../../../W3C/EXI/docs/json/V2/personnel_two.json");
		jsons.add("./../../../W3C/EXI/docs/json/V2/personnel_three.json");
		
		// Some other samples
		jsons.add("./../../../W3C/EXI/docs/json/V2/test/bower.json");
		jsons.add("./../../../W3C/EXI/docs/json/V2/test/door.jsonld");
		jsons.add("./../../../W3C/EXI/docs/json/V2/test/package.json");
		jsons.add("./../../../W3C/EXI/docs/json/V2/test/ui.resizable.jquery.json");
		
		// "old" JSON tests
		// GPX 
		jsons.add("./../../../W3C/Group/EXI/TTFMS/data/JSON/gpx/sample-set-1/gpx-1-1pts.json");
		jsons.add("./../../../W3C/Group/EXI/TTFMS/data/JSON/gpx/sample-set-1/gpx-1-100pts.json");
		jsons.add("./../../../W3C/Group/EXI/TTFMS/data/JSON/gpx/sample-set-1/gpx-1-200pts.json");
		jsons.add("./../../../W3C/Group/EXI/TTFMS/data/JSON/gpx/sample-set-1/gpx-1-500pts.json");
		// JSON Generator
		jsons.add("./../../../W3C/Group/EXI/TTFMS/data/JSON/json-generator.com/2015-01-06/01.json");
		jsons.add("./../../../W3C/Group/EXI/TTFMS/data/JSON/json-generator.com/2015-01-06/03.json");
		jsons.add("./../../../W3C/Group/EXI/TTFMS/data/JSON/json-generator.com/2015-01-06/06.json");
		jsons.add("./../../../W3C/Group/EXI/TTFMS/data/JSON/json-generator.com/2015-01-06/10.json");
		// OpenWheather
		jsons.add("./../../../W3C/Group/EXI/TTFMS/data/JSON/openweathermap.org/sample-set-1/owm-1-1cities.json");
		jsons.add("./../../../W3C/Group/EXI/TTFMS/data/JSON/openweathermap.org/sample-set-1/owm-1-100cities.json");
		jsons.add("./../../../W3C/Group/EXI/TTFMS/data/JSON/openweathermap.org/sample-set-1/owm-1-200cities.json");
		jsons.add("./../../../W3C/Group/EXI/TTFMS/data/JSON/openweathermap.org/sample-set-1/owm-1-300cities.json");
		jsons.add("./../../../W3C/Group/EXI/TTFMS/data/JSON/openweathermap.org/sample-set-1/owm-1-400cities.json");
		jsons.add("./../../../W3C/Group/EXI/TTFMS/data/JSON/openweathermap.org/sample-set-1/owm-1-500cities.json");
		jsons.add("./../../../W3C/Group/EXI/TTFMS/data/JSON/openweathermap.org/sample-set-1/owm-1-1000cities.json");
		
		
		System.out.println("Name; JSON; V1; V2; V2_EFG");
		for(String json: jsons) {
			test(json);
		}
		

	}
	
	private static void test(String json) throws FileNotFoundException, EXIException, IOException {
		ByteArrayOutputStream baosV1 = new ByteArrayOutputStream();
		{
			EXIforJSONGenerator e4jGenerator = new EXIforJSONGenerator();
			e4jGenerator.generate(new FileInputStream(json), baosV1);
			// System.out.println("Size V1: " + baosV1.size());
		}
		
		ByteArrayOutputStream baosV2 = new ByteArrayOutputStream();
		{
			generateV2(new FileInputStream(json), baosV2, getEXIFactoryV2());
			// System.out.println("Size V2: " + baosV2.size());
		}
		
		ByteArrayOutputStream baosV2_EFG = new ByteArrayOutputStream();
		{
			generateV2(new FileInputStream(json), baosV2_EFG, getEXIFactoryV2_EFG());
			// System.out.println("Size V2_EFG: " + baosV2_EFG.size());
		}
		
		System.out.println(json + "; " + (new File(json)).length() + "; " + baosV1.size() + "; " + baosV2.size() + "; " + baosV2_EFG.size());
	}
	
	
	private static EXIFactory getEXIFactoryV2() throws EXIException, IOException {
		EXIFactory efV2 = DefaultEXIFactory.newInstance();
		// setup EXI schema
		// URL urlXSD = new URL("http://www.w3.org/XML/EXI/docs/json/schema-for-json.xsd");
		// URL urlXSD  = this.getClass().getResource("/schema-for-json.xsd");
		// InputStream isXSD = urlXSD.openStream();
		InputStream isXSD = new FileInputStream("./../../../W3C/EXI/docs/json/V2/schema-for-json-V2b.xsd");
		Grammars g = GrammarFactory.newInstance().createGrammars(isXSD);
		isXSD.close();
		efV2.setGrammars(g);
		
		// set to strict
		efV2.getFidelityOptions().setFidelity(FidelityOptions.FEATURE_STRICT, true);
		
		return efV2;
	}
	
	// (i.e. Element Fragment Grammar)
	private static EXIFactory getEXIFactoryV2_EFG() throws EXIException, IOException {
		EXIFactory efV2_EFG =  getEXIFactoryV2();
		efV2_EFG.setUsingNonEvolvingGrammars(true);
		
		return efV2_EFG;
	}
	
	
	private static void generateV2(InputStream is, OutputStream os, EXIFactory efV2) throws EXIException, IOException {
//		DEBUG = System.out;
		
		EXIStreamEncoder streamEncoder = efV2.createEXIStreamEncoder();
		
		EXIBodyEncoder bodyEncoder = streamEncoder.encodeHeader(os);
		
		JsonParser parser = Json.createParser(is);
		
		bodyEncoder.encodeStartDocument();
		
		List<Event> events = new ArrayList<Event>();
		List<String> keys = new ArrayList<String>();
		
		while (parser.hasNext()) {
			Event e = parser.next();
			
			switch(e) {
			case KEY_NAME:
				events.add(e);
				String key = parser.getString();
				keys.add(key);
				bodyEncoder.encodeStartElement("", key, null); // NAMESPACE_EXI4JSON
				printDebug("<" + key +">");
				break;
			case START_OBJECT:
				events.add(e);
				bodyEncoder.encodeStartElement(NAMESPACE_EXI4JSON, LOCALNAME_MAP, null);
				printDebug("<map>");
				break;
			case END_OBJECT:
				printDebug("</map>");
				Event eo = events.remove(events.size()-1);
				assert(eo == Event.START_OBJECT);
				bodyEncoder.encodeEndElement();
				checkKeyEnd(events, keys, bodyEncoder);
				break;
			case START_ARRAY:
				events.add(e);
				bodyEncoder.encodeStartElement(NAMESPACE_EXI4JSON, LOCALNAME_ARRAY, null);
				printDebug("<array>");
				break;
			case END_ARRAY:
				printDebug("</array>");
				Event ea = events.remove(events.size()-1);
				assert(ea == Event.START_ARRAY);
				bodyEncoder.encodeEndElement();
				checkKeyEnd(events, keys, bodyEncoder);
				break;
			case VALUE_STRING:
				bodyEncoder.encodeStartElement(NAMESPACE_EXI4JSON, LOCALNAME_STRING, null);
				bodyEncoder.encodeCharacters(new StringValue(parser.getString()));
				printDebug("<string>" + parser.getString() + "</string>");
				bodyEncoder.encodeEndElement();
				checkKeyEnd(events, keys, bodyEncoder);
				break;
			case VALUE_NUMBER:
				// TODO use /other/integer if it is an integer value
				bodyEncoder.encodeStartElement(NAMESPACE_EXI4JSON, LOCALNAME_NUMBER, null);
				bodyEncoder.encodeCharacters(new StringValue(parser.getString()));
				printDebug("<number>" + parser.getString() + "</number>");
				bodyEncoder.encodeEndElement();
				checkKeyEnd(events, keys, bodyEncoder);
				break;
			case VALUE_FALSE:
			case VALUE_TRUE:
				bodyEncoder.encodeStartElement(NAMESPACE_EXI4JSON, LOCALNAME_BOOLEAN, null);
				String sb = e == Event.VALUE_FALSE ? "false" : "true";
				bodyEncoder.encodeCharacters(new StringValue(sb));
				printDebug("<boolean>" + sb + "</boolean>");
				bodyEncoder.encodeEndElement();
				checkKeyEnd(events, keys, bodyEncoder);
				break;
			case VALUE_NULL:
				bodyEncoder.encodeStartElement(NAMESPACE_EXI4JSON, LOCALNAME_NULL, null);
				printDebug("<null />" );
				bodyEncoder.encodeEndElement();
				checkKeyEnd(events, keys, bodyEncoder);
				break;
			default:
				throw new RuntimeException("Not supported JSON event: " + e);
			}
		}
		
		bodyEncoder.encodeEndDocument();
		bodyEncoder.flush();
	}
	
	private static void checkKeyEnd(List<Event> events, List<String> keys, EXIBodyEncoder bodyEncoder) throws EXIException, IOException {
		if(events.size()>0 && events.get(events.size()-1) == Event.KEY_NAME) {
			bodyEncoder.encodeEndElement(); // end of key element
			printDebug("</" + keys.remove(keys.size()-1) +">");
			events.remove(events.size()-1);
		}
		
	}

	
}
