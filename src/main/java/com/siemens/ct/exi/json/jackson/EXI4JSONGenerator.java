package com.siemens.ct.exi.json.jackson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.base.GeneratorBase;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.json.EXIforJSONGenerator;

public class EXI4JSONGenerator extends GeneratorBase {

	EXIforJSONGenerator e4j;
	
	OutputStream out;
	
	IOContext ctxt;
	
	private static final boolean DEBUG = false;
	
	public EXI4JSONGenerator(int features, ObjectCodec codec) throws EXIException, IOException {
		super(features, codec);
		e4j = new EXIforJSONGenerator();
		// TODO start EXI document
	}
	
	
	public EXI4JSONGenerator(int features, ObjectCodec codec, OutputStream out, IOContext ctxt) throws EXIException, IOException {
		this(features, codec);
		this.out = out;
		this.ctxt = ctxt;
		
		e4j.setOutputStream(out);
		e4j.writeStartDocument();
	}


	@Override
	protected void _releaseBuffers() {
	}

	@Override
	protected void _verifyValueWrite(String typeMsg) throws IOException {
		// TODO Not sure when this method is called (what is the intent)
		System.err.println("_verifyValueWrite: " + typeMsg);
	}

	@Override
	public void flush() throws IOException {
		if(DEBUG) {
			System.out.println("flush()");
		}
		try {
			e4j.writeEndDocument();
		} catch (EXIException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeBinary(Base64Variant bv, byte[] data, int offset, int len) throws IOException {
		// TODO not sure what binary in this context is
		throw new IOException("No support for Binary yet");
	}

	@Override
	public void writeBoolean(boolean state) throws IOException {
		if(DEBUG) {
			System.out.println("writeBoolean(" + state + ")");
		}
		try {
			e4j.writeBoolean(state);
		} catch (EXIException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeEndArray() throws IOException {
		if(DEBUG) {
			System.out.println("writeEndArray()");
		}
		try {
			e4j.writeEndArray();
		} catch (EXIException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeEndObject() throws IOException {
		if(DEBUG) {
			System.out.println("writeEndObject()");
		}
		try {
			e4j.writeEndObject();
		} catch (EXIException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeFieldName(String name) throws IOException {
		if(DEBUG) {
			System.out.println("writeFieldName(" + name + ")");
		}
		try {
			e4j.writeKeyName(name);
		} catch (EXIException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeNull() throws IOException {
		if(DEBUG) {
			System.out.println("writeNull()");
		}
		try {
			e4j.writeNull();
		} catch (EXIException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeNumber(int v) throws IOException {
		if(DEBUG) {
			System.out.println("writeNumber(" + v + ")");
		}
		try {
			e4j.writeNumber(v);
		} catch (EXIException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeNumber(long v) throws IOException {
		if(DEBUG) {
			System.out.println("writeNumber(" + v + ")");
		}
		try {
			e4j.writeNumber(v);
		} catch (EXIException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeNumber(BigInteger v) throws IOException {
		if(DEBUG) {
			System.out.println("writeNumber(" + v + ")");
		}
		try {
			e4j.writeNumber(v);
		} catch (EXIException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeNumber(double v) throws IOException {
		if(DEBUG) {
			System.out.println("writeNumber(" + v + ")");
		}
		try {
			e4j.writeNumber(v);
		} catch (EXIException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeNumber(float v) throws IOException {
		if(DEBUG) {
			System.out.println("writeNumber(" + v + ")");
		}
		try {
			e4j.writeNumber(v);
		} catch (EXIException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeNumber(BigDecimal v) throws IOException {
		if(DEBUG) {
			System.out.println("writeNumber(" + v + ")");
		}
		try {
			e4j.writeNumber(v);
		} catch (EXIException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeNumber(String encodedValue) throws IOException {
		if(DEBUG) {
			System.out.println("writeNumber(" + encodedValue + ")");
		}
		try {
			e4j.writeNumber(encodedValue);
		} catch (EXIException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeRaw(String text) throws IOException {
		writeString(text);
	}

	@Override
	public void writeRaw(char c) throws IOException {
		writeString(c + "");
	}

	@Override
	public void writeRaw(String text, int offset, int len) throws IOException {
		String s = text.substring(offset, offset+len);
		writeString(s);
	}

	@Override
	public void writeRaw(char[] text, int offset, int len) throws IOException {
		String s = new String(text, offset, len);
		writeString(s);
	}

	@Override
	public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException {
		String s = new String(text, offset, length, StandardCharsets.UTF_8);
		writeString(s);
	}

	@Override
	public void writeStartArray() throws IOException {
		if(DEBUG) {
			System.out.println("writeStartArray()");
		}
		try {
			e4j.writeStartArray();
		} catch (EXIException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeStartObject() throws IOException {
		if(DEBUG) {
			System.out.println("writeStartObject()");
		}
		try {
			e4j.writeStartObject();
		} catch (EXIException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeString(String text) throws IOException {
		if(DEBUG) {
			System.out.println("writeString(" + text + ")");
		}
		try {
			e4j.writeString(text);
		} catch (EXIException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void writeString(char[] text, int offset, int len) throws IOException {
		String s = new String(text, offset, len);
		writeString(s);
	}

	@Override
	public void writeUTF8String(byte[] text, int offset, int length) throws IOException {
		String s = new String(text, offset, length, StandardCharsets.UTF_8);
		writeString(s);
	}
	
	
	
	public static void main(String[] args) {
		EXI4JSONFactory fEXI = new EXI4JSONFactory();
		ObjectMapper mapperEXI = new ObjectMapper(fEXI);
		ObjectMapper mapperJSON = new ObjectMapper();
		
		String carJson =
			    "{ \"brand\" : \"Mercedes\", \"doors\" : 5 }";
		
		try {
			// TODO currently JSON plain-text and not EXI4JSON
		    JsonNode car = mapperJSON.readTree(carJson); // , Car.class);
		    System.out.println("car = " + car);
		    
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    mapperEXI.writeTree(fEXI.createGenerator(baos), car);
		    
		    System.out.println("# EXI4JSON Bytes: " + baos.size());
		    
		    // read bytes again 
		    EXI4JSONParser eparser = fEXI.createParser(baos.toByteArray());
		    JsonNode car2 = mapperJSON.readTree(eparser);
		    System.out.println("car2 = " + car2);
		    
//		    System.out.println("car.brand = " + car.brand);
//		    System.out.println("car.doors = " + car.doors);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}

}
