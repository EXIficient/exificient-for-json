package com.siemens.ct.exi.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonParser.Event;

import com.siemens.ct.exi.EXIBodyDecoder;
import com.siemens.ct.exi.EXIStreamDecoder;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.values.BooleanValue;
import com.siemens.ct.exi.values.FloatValue;
import com.siemens.ct.exi.values.Value;
import com.siemens.ct.exi.values.ValueType;

public class EXIforJSONParser extends AbstractEXIforJSON {
	
	Event jsonEvent = null;
	String key = null;
	Value value = null;
	
	public EXIforJSONParser() throws EXIException, IOException {
		super();
	}
	
	
	void checkPendingEvent(JsonGenerator generator) {
		if(jsonEvent != null) {
			if(key == null) {
				switch(jsonEvent) {
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
					if(value.getValueType() == ValueType.FLOAT) {
						FloatValue fv = (FloatValue) value;
						generator.write(fv.toDouble());
					} else {
						throw new RuntimeException("Not supported number value: " + value);
					}
					break;
				case VALUE_FALSE:
				case VALUE_TRUE:
					if(value.getValueType() == ValueType.BOOLEAN) {
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
				switch(jsonEvent) {
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
					if(value.getValueType() == ValueType.FLOAT) {
						FloatValue fv = (FloatValue) value;
						generator.write(key, fv.toDouble());
					} else {
						throw new RuntimeException("Not supported number value: " + value);
					}
					break;
				case VALUE_FALSE:
				case VALUE_TRUE:
					if(value.getValueType() == ValueType.BOOLEAN) {
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
	
	
	
	public void parse(InputStream is, OutputStream os) throws EXIException, IOException {
		EXIStreamDecoder streamDecoder = ef.createEXIStreamDecoder();
		
		EXIBodyDecoder bodyDecoder = streamDecoder.decodeHeader(is);
		
		JsonGenerator generator = Json.createGenerator(new OutputStreamWriter(os));
		
		EventType next;
	
		while((next = bodyDecoder.next()) != null) {
			switch(next) {
			case START_DOCUMENT:
				bodyDecoder.decodeStartDocument();
				break;
			case END_DOCUMENT:
				bodyDecoder.decodeEndDocument();
				break;
			case ATTRIBUTE:
				QNameContext qncAT = bodyDecoder.decodeAttribute();
				if(!LOCALNAME_KEY.equals(qncAT.getLocalName())) {
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
				if(LOCALNAME_MAP.equals(qncSE.getLocalName())) {
					// wait for possible key
					jsonEvent = Event.START_OBJECT;
				} else if(LOCALNAME_ARRAY.equals(qncSE.getLocalName())) {
					// wait for possible key
					jsonEvent = Event.START_ARRAY;
				} else if(LOCALNAME_STRING.equals(qncSE.getLocalName())) {
					// wait for possible key
					jsonEvent = Event.VALUE_STRING;
				} else if(LOCALNAME_NUMBER.equals(qncSE.getLocalName())) {
					// wait for possible key
					jsonEvent = Event.VALUE_NUMBER;
				} else if(LOCALNAME_BOOLEAN.equals(qncSE.getLocalName())) {
					// wait for possible key
					jsonEvent = Event.VALUE_FALSE;
				} else if(LOCALNAME_NULL.equals(qncSE.getLocalName())) {
					// wait for possible key
					jsonEvent = Event.VALUE_NULL;
				} else {
					throw new RuntimeException("Not supported EXI element: " + qncSE);
				}
				break;
			case END_ELEMENT:
				QNameContext qncEE = bodyDecoder.decodeEndElement();
				checkPendingEvent(generator);
				if(LOCALNAME_MAP.equals(qncEE.getLocalName())) {
					generator.writeEnd();
				} else if(LOCALNAME_ARRAY.equals(qncEE.getLocalName())) {
					generator.writeEnd();
				}
				break;
			default:
				throw new RuntimeException("Not supported EXI event: " + next);
			}
		}
		
		
		generator.flush();
	}
}