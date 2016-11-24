package com.siemens.ct.exi.json.jackson;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.base.GeneratorBase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EXI4JSONGenerator extends GeneratorBase {

	protected EXI4JSONGenerator(int features, ObjectCodec codec) {
		super(features, codec);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void _releaseBuffers() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void _verifyValueWrite(String arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flush() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeBinary(Base64Variant arg0, byte[] arg1, int arg2, int arg3) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeBoolean(boolean arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeEndArray() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeEndObject() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeFieldName(String arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeNull() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeNumber(int arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeNumber(long arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeNumber(BigInteger arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeNumber(double arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeNumber(float arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeNumber(BigDecimal arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeNumber(String arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeRaw(String arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeRaw(char arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeRaw(String arg0, int arg1, int arg2) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeRaw(char[] arg0, int arg1, int arg2) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeRawUTF8String(byte[] arg0, int arg1, int arg2) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeStartArray() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeStartObject() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeString(String arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeString(char[] arg0, int arg1, int arg2) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeUTF8String(byte[] arg0, int arg1, int arg2) throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	
	
	public static void main(String[] args) {
		EXI4JSONFactory f = new EXI4JSONFactory();
		ObjectMapper mapper = new ObjectMapper(f);
		
		String carJson =
			    "{ \"brand\" : \"Mercedes\", \"doors\" : 5 }";
		
		try {
			// TODO currently JSON plain-text and not EXI4JSON
		    JsonNode car = mapper.readTree(carJson); // , Car.class);
		    System.out.println("car = " + car);
		    
//		    System.out.println("car.brand = " + car.brand);
//		    System.out.println("car.doors = " + car.doors);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}

}
