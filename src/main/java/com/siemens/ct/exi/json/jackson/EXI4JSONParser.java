package com.siemens.ct.exi.json.jackson;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.base.ParserMinimalBase;

public class EXI4JSONParser extends ParserMinimalBase  {

	@Override
	protected void _handleEOF() throws JsonParseException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte[] getBinaryValue(Base64Variant arg0) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCurrentName() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonStreamContext getParsingContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getText() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char[] getTextCharacters() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTextLength() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTextOffset() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasTextCharacters() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isClosed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public JsonToken nextToken() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void overrideCurrentName(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BigInteger getBigIntegerValue() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObjectCodec getCodec() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonLocation getCurrentLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal getDecimalValue() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getDoubleValue() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getFloatValue() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getIntValue() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getLongValue() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public NumberType getNumberType() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Number getNumberValue() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonLocation getTokenLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCodec(ObjectCodec arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Version version() {
		// TODO Auto-generated method stub
		return null;
	}

}
