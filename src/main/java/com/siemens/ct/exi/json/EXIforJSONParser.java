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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonParser.Event;

import com.siemens.ct.exi.core.EXIBodyDecoder;
import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.EXIStreamDecoder;
import com.siemens.ct.exi.core.context.QNameContext;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.core.grammars.event.EventType;
import com.siemens.ct.exi.core.values.BooleanValue;
import com.siemens.ct.exi.core.values.FloatValue;
import com.siemens.ct.exi.core.values.StringValue;
import com.siemens.ct.exi.core.values.Value;
import com.siemens.ct.exi.core.values.ValueType;

public class EXIforJSONParser extends AbstractEXIforJSON {

	InputStream isEXI4JSON;

	EXIBodyDecoder bodyDecoder;

	Event jsonEvent = null;
	String key = null;
	Value value = null;

	StringValue sValue;
	BooleanValue bValue;
	FloatValue fValue;
	// String sKey;

	public EXIforJSONParser() throws EXIException, IOException {
		super();
	}

	public EXIforJSONParser(EXIFactory ef) throws EXIException, IOException {
		super(ef);
	}

	public EXIforJSONParser(String schemaId) throws EXIException, IOException {
		super(schemaId);
	}

	public EXIforJSONParser(EXIFactory ef, String schemaId) throws EXIException, IOException {
		super(ef, schemaId);
	}

	public void setInputStream(InputStream isEXI4JSON) {
		this.isEXI4JSON = isEXI4JSON;
	}

	void checkPendingEvent(JsonGenerator generator) {
		if (jsonEvent != null) {
			if (key == null) {
				switch (jsonEvent) {
				case START_OBJECT:
					generator.writeStartObject();
					break;
				case START_ARRAY:
					generator.writeStartArray();
					break;
				case VALUE_STRING:
					generator.write(value.toString());
					break;
				case VALUE_NUMBER:
					if (value.getValueType() == ValueType.FLOAT) {
						FloatValue fv = (FloatValue) value;
						generator.write(fv.toDouble());
						// generator.write(new BigDecimal(fv.getMantissa() + "E"
						// + fv.getExponent()));
					} else {
						throw new RuntimeException("Not supported number value: " + value);
					}
					break;
				case VALUE_FALSE:
				case VALUE_TRUE:
					if (value.getValueType() == ValueType.BOOLEAN) {
						BooleanValue bv = (BooleanValue) value;
						generator.write(bv.toBoolean());
					} else {
						throw new RuntimeException("Not supported boolean value: " + value);
					}
					break;
				case VALUE_NULL:
					generator.writeNull();
					break;
				default:
					throw new RuntimeException("Not supported event in checkPendingEvent: " + jsonEvent);
				}
			} else {
				switch (jsonEvent) {
				case START_OBJECT:
					generator.writeStartObject(key);
					break;
				case START_ARRAY:
					generator.writeStartArray(key);
					break;
				case VALUE_STRING:
					generator.write(key, value.toString());
					break;
				case VALUE_NUMBER:
					if (value.getValueType() == ValueType.FLOAT) {
						FloatValue fv = (FloatValue) value;
						generator.write(key, fv.toDouble());
					} else {
						throw new RuntimeException("Not supported number value: " + value);
					}
					break;
				case VALUE_FALSE:
				case VALUE_TRUE:
					if (value.getValueType() == ValueType.BOOLEAN) {
						BooleanValue bv = (BooleanValue) value;
						generator.write(key, bv.toBoolean());
					} else {
						throw new RuntimeException("Not supported boolean value: " + value);
					}
					break;
				case VALUE_NULL:
					generator.writeNull(key);
					break;
				default:
					throw new RuntimeException("Not supported event in checkPendingEvent: " + jsonEvent);
				}
			}

			jsonEvent = null;
			key = null;
			value = null;
		}
	}

	EXI4JSONEvent checkPendingEvent2() throws EXIException {
		if (jsonEvent != null) {
			EXI4JSONEvent ev = null;
			switch (jsonEvent) {
			case START_OBJECT:
				ev = EXI4JSONEvent.START_OBJECT;
				break;
			case START_ARRAY:
				ev = EXI4JSONEvent.START_ARRAY;
				break;
			case VALUE_STRING:
				ev = EXI4JSONEvent.VALUE_STRING;
				if (value.getValueType() == ValueType.STRING) {
					sValue = (StringValue) value;
				} else {
					throw new RuntimeException("Not supported number value: " + value);
				}
				break;
			case VALUE_NUMBER:
				ev = EXI4JSONEvent.VALUE_NUMBER;
				if (value.getValueType() == ValueType.FLOAT) {
					fValue = (FloatValue) value;
				} else {
					throw new EXIException("Not supported number value: " + value);
				}
				break;
			case VALUE_FALSE:
			case VALUE_TRUE:
				ev = EXI4JSONEvent.VALUE_BOOLEAN;
				if (value.getValueType() == ValueType.BOOLEAN) {
					bValue = (BooleanValue) value;
				} else {
					throw new EXIException("Not supported boolean value:" + value);
				}
				break;
			case VALUE_NULL:
				ev = EXI4JSONEvent.VALUE_NULL;
				break;
			default:
				throw new EXIException("Not supported event in checkPendingEvent: " + jsonEvent);
			}

			jsonEvent = null;
			key = null;
			value = null;

			return ev;
		} else {
			throw new EXIException("JSON Event null");
		}
	}

	public void parse(InputStream isEXI4JSON, OutputStream osJSON) throws EXIException, IOException {

		if (EXI4JSONConstants.XML_SCHEMA_FOR_JSON.equals(schemaId)) {
			parseV1(isEXI4JSON, osJSON);
		} else {
			parseV2(isEXI4JSON, osJSON);
		}
	}

	protected void parseV1(InputStream isEXI4JSON, OutputStream osJSON) throws EXIException, IOException {
		EXIStreamDecoder streamDecoder = ef.createEXIStreamDecoder();

		EXIBodyDecoder bodyDecoder = streamDecoder.decodeHeader(isEXI4JSON);

		JsonGenerator generator = Json.createGenerator(new OutputStreamWriter(osJSON));

		EventType next;

		while ((next = bodyDecoder.next()) != null) {
			switch (next) {
			case START_DOCUMENT:
				bodyDecoder.decodeStartDocument();
				break;
			case END_DOCUMENT:
				bodyDecoder.decodeEndDocument();
				break;
			case ATTRIBUTE:
				QNameContext qncAT = bodyDecoder.decodeAttribute();
				if (!EXI4JSONConstants.LOCALNAME_KEY.equals(qncAT.getLocalName())) {
					throw new RuntimeException("Not supported EXI attribute: " + qncAT);
				}
				Value avalue = bodyDecoder.getAttributeValue();
				key = avalue.toString();
				// checkPendingEvent(generator, jsonEvent, value.toString());
				break;
			case CHARACTERS:
				value = bodyDecoder.decodeCharacters();
				checkPendingEvent(generator);
				break;
			case START_ELEMENT:
				QNameContext qncSE = bodyDecoder.decodeStartElement();
				checkPendingEvent(generator);
				if (EXI4JSONConstants.LOCALNAME_MAP.equals(qncSE.getLocalName())) {
					// wait for possible key
					jsonEvent = Event.START_OBJECT;
				} else if (EXI4JSONConstants.LOCALNAME_ARRAY.equals(qncSE.getLocalName())) {
					// wait for possible key
					jsonEvent = Event.START_ARRAY;
				} else if (EXI4JSONConstants.LOCALNAME_STRING.equals(qncSE.getLocalName())) {
					// wait for possible key
					jsonEvent = Event.VALUE_STRING;
				} else if (EXI4JSONConstants.LOCALNAME_NUMBER.equals(qncSE.getLocalName())) {
					// wait for possible key
					jsonEvent = Event.VALUE_NUMBER;
				} else if (EXI4JSONConstants.LOCALNAME_BOOLEAN.equals(qncSE.getLocalName())) {
					// wait for possible key
					jsonEvent = Event.VALUE_FALSE;
				} else if (EXI4JSONConstants.LOCALNAME_NULL.equals(qncSE.getLocalName())) {
					// wait for possible key
					jsonEvent = Event.VALUE_NULL;
				} else {
					throw new RuntimeException("Not supported EXI element: " + qncSE);
				}
				break;
			case END_ELEMENT:
				QNameContext qncEE = bodyDecoder.decodeEndElement();
				checkPendingEvent(generator);
				if (EXI4JSONConstants.LOCALNAME_MAP.equals(qncEE.getLocalName())) {
					generator.writeEnd();
				} else if (EXI4JSONConstants.LOCALNAME_ARRAY.equals(qncEE.getLocalName())) {
					generator.writeEnd();
				}
				break;
			default:
				throw new RuntimeException("Not supported EXI event: " + next);
			}
		}

		generator.flush();
	}

	public void readStartDocument() throws EXIException, IOException {
		EXIStreamDecoder streamDecoder = ef.createEXIStreamDecoder();

		bodyDecoder = streamDecoder.decodeHeader(isEXI4JSON);

		if (EventType.START_DOCUMENT == bodyDecoder.next()) {
			bodyDecoder.decodeStartDocument();
		} else {
			throw new EXIException("No start EXI StartDocument event");
		}
	}

	public void readEndDocument() throws EXIException, IOException {
		bodyDecoder.decodeEndDocument();
	}

	public String getKeyName() {
		return key;
	}

	// returns null for end
	public EXI4JSONEvent readNextEvent() throws EXIException, IOException {

		EXI4JSONEvent ev = null;

		do {

			EventType next = bodyDecoder.next();

			if (next == null) {
				return null;
			}

			switch (next) {
			case END_DOCUMENT:
				return null;
			case CHARACTERS:
				value = bodyDecoder.decodeCharacters();
				ev = checkPendingEvent2();
				break;
			case START_ELEMENT_NS:
				// key element
				key = bodyDecoder.decodeStartElement().getLocalName();
				key = unescapeKey(key);
				ev = EXI4JSONEvent.KEY_NAME;
				break;
			case START_ELEMENT:
			case START_ELEMENT_GENERIC:
			case START_ELEMENT_GENERIC_UNDECLARED:
				QNameContext qncSE = bodyDecoder.decodeStartElement();
				if (EXI4JSONConstants.LOCALNAME_MAP.equals(qncSE.getLocalName())) {
					ev = EXI4JSONEvent.START_OBJECT;
					key = null;
				} else if (EXI4JSONConstants.LOCALNAME_ARRAY.equals(qncSE.getLocalName())) {
					ev = EXI4JSONEvent.START_ARRAY;
					key = null;
				} else if (EXI4JSONConstants.LOCALNAME_STRING.equals(qncSE.getLocalName())) {
					// wait for value
					jsonEvent = Event.VALUE_STRING;
				} else if (EXI4JSONConstants.LOCALNAME_NUMBER.equals(qncSE.getLocalName())) {
					// wait for value
					jsonEvent = Event.VALUE_NUMBER;
				} else if (EXI4JSONConstants.LOCALNAME_BOOLEAN.equals(qncSE.getLocalName())) {
					// wait for value
					jsonEvent = Event.VALUE_FALSE;
				} else if (EXI4JSONConstants.LOCALNAME_NULL.equals(qncSE.getLocalName())) {
					// generator.writeNull();
					jsonEvent = Event.VALUE_NULL;
				} else if (EXI4JSONConstants.LOCALNAME_OTHER.equals(qncSE.getLocalName())) {
					// TODO other element
					throw new IOException("'other' element not yet supported!");
				} else {
					// key element --> not necessary here : MUST BE
					// START_ELEMENT_NS
					// key = bodyDecoder.decodeStartElement().getLocalName();
					throw new IOException("Unexpected element " + qncSE);
				}
				break;
			case END_ELEMENT:
				QNameContext qncEE = bodyDecoder.decodeEndElement();
				if (EXI4JSONConstants.LOCALNAME_MAP.equals(qncEE.getLocalName())) {
					ev = EXI4JSONEvent.END_OBJECT;
				} else if (EXI4JSONConstants.LOCALNAME_ARRAY.equals(qncEE.getLocalName())) {
					ev = EXI4JSONEvent.END_ARRAY;
				} else if (EXI4JSONConstants.LOCALNAME_NULL.equals(qncEE.getLocalName())) {
					ev = EXI4JSONEvent.VALUE_NULL;
				}
				break;
			default:
				throw new IOException("Not supported EXI event: " + next);
			}
		} while (ev == null);

		return ev;

	}

	public enum EXI4JSONEvent {
		/***/
		KEY_NAME,
		/***/
		START_OBJECT,
		/***/
		END_OBJECT,
		/***/
		START_ARRAY,
		/***/
		END_ARRAY,
		/***/
		VALUE_STRING,
		/***/
		VALUE_NUMBER,
		/***/
		VALUE_BOOLEAN,
		/***/
		VALUE_NULL,

	}
	
	public String getValueString() {
		return sValue.toString();
	}

	public boolean getValueBoolean() {
		return bValue.toBoolean();
	}

	public double getValueNumberDouble() {
		return fValue.toDouble();
	}

	protected void parseV2(InputStream isEXI4JSON, OutputStream osJSON) throws EXIException, IOException {
		this.setInputStream(isEXI4JSON);
		this.readStartDocument();

		JsonGenerator generator = Json.createGenerator(new OutputStreamWriter(osJSON));

		EXI4JSONEvent ev;

		String sk = null; // buffer key name

		while ((ev = this.readNextEvent()) != null) {
			switch (ev) {
			case KEY_NAME:
				sk = this.getKeyName();
				break;
			case START_OBJECT:
				if (sk == null) {
					generator.writeStartObject();
				} else {
					generator.writeStartObject(sk);
					sk = null;
				}
				break;
			case START_ARRAY:
				if (sk == null) {
					generator.writeStartArray();
				} else {
					generator.writeStartArray(sk);
					sk = null;
				}
				break;
			case END_OBJECT:
			case END_ARRAY:
				generator.writeEnd();
				break;
			case VALUE_STRING:
				if (sk == null) {
					generator.write(getValueString() );
				} else {
					generator.write(sk, getValueString() );
					sk = null;
				}
				break;
			case VALUE_NUMBER:
				if (sk == null) {
					generator.write(getValueNumberDouble());
				} else {
					generator.write(sk, getValueNumberDouble());
					sk = null;
				}
				break;
			case VALUE_BOOLEAN:
				if (sk == null) {
					generator.write(getValueBoolean());
				} else {
					generator.write(sk, getValueBoolean());
					sk = null;
				}
				break;
			case VALUE_NULL:
				if (sk == null) {
					generator.writeNull();
				} else {
					generator.writeNull(sk);
					sk = null;
				}
				break;
			default:
				throw new RuntimeException("Not supported EXI event: " + ev);
			}
		}

		generator.flush();
	}

	protected String unescapeKey(String key) {
		StringBuilder sb = null;

		// conflicting names
		if (key.length() > 2 && key.charAt(0) == EXI4JSONConstants.ESCAPE_START_CHARACTER
				&& key.charAt(1) == EXI4JSONConstants.ESCAPE_END_CHARACTER) {
			key = key.substring(2);
		} else {
			// check whether there is an escape character
			int i = 0;
			while (i < key.length()) {
				char c = key.charAt(i);
				if (c == EXI4JSONConstants.ESCAPE_START_CHARACTER) {
					int endIndex = key.indexOf(EXI4JSONConstants.ESCAPE_END_CHARACTER, i);
					if (endIndex <= 0) {
						throw new RuntimeException("Unexpected Escape Key: " + key);
					} else {
						int cp = Integer.parseInt(key.substring(i + 1, endIndex));
						if (sb == null) {
							sb = new StringBuilder();
							sb.append(key, 0, i);
						}
						sb.appendCodePoint(cp);
						i += (endIndex - i);
					}
				} else {
					// ok
					if (sb != null) {
						sb.append(c);
					}
				}
				i++;
			}
		}

		if (sb == null) {
			return key;
		} else {
			return sb.toString();
		}
	}

	// public static void main(String[] args) throws EXIException, IOException {
	// if(args.length == 1) {
	// EXIforJSONParser e4jParser = new EXIforJSONParser();
	// ByteArrayOutputStream osJSON = new ByteArrayOutputStream();
	// e4jParser.parse(new
	// FileInputStream("C:\\Users\\mchn4310\\AppData\\Local\\Temp\\exi4json4302776206135352859exi"),
	// osJSON);
	// System.out.println(new String(osJSON.toByteArray()));
	// }
	// }
}
