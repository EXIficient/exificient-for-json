package com.siemens.ct.exi.json.jackson;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import com.fasterxml.jackson.core.io.IOContext;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.json.EXIforJSONParser;
import com.siemens.ct.exi.json.EXIforJSONParser.EXI4JSONEvent;

public class EXI4JSONParser extends ParserMinimalBase {
	
	private static final boolean DEBUG = false;

	EXIforJSONParser e4j;

	InputStream in;
	
	boolean isClosed = false;

	/**
	 * Total number of bytes/characters read before start of current token. For
	 * big (gigabyte-sized) sizes are possible, needs to be long, unlike
	 * pointers and sizes related to in-memory buffers.
	 */
	protected long _tokenInputTotal = 0;
	
	  /**
     * Codec used for data binding when (if) requested.
     */
	protected ObjectCodec _objectCodec;
	

	/**
	 * I/O context for this reader. It handles buffer allocation for the reader.
	 */
	final protected IOContext _ioContext;

	public EXI4JSONParser(InputStream in, IOContext ctxt) throws EXIException, IOException {
		this.in = in;
		this._ioContext = ctxt;

		e4j = new EXIforJSONParser();
		e4j.setInputStream(in);
		e4j.readStartDocument();
	}

	public EXI4JSONParser(byte[] data, int offset, int len, IOContext ctxt) throws EXIException, IOException {
		this(new ByteArrayInputStream(data, offset, len), ctxt);
	}

	@Override
	protected void _handleEOF() throws JsonParseException {
		if(DEBUG) {
			System.out.println("_handleEOF()");
		}
	}

	@Override
	public void close() throws IOException {
		try {
			e4j.readEndDocument();
		} catch (EXIException e) {
			throw new IOException(e);
		}
	}

	@Override
	public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
		if(DEBUG) {
			System.out.println("getBinaryValue()");
		}
		// TODO not sure about the intent of this method
		throw new IOException("No support for getBinaryValue yet");
	}

	@Override
	public String getCurrentName() throws IOException {
		String cname = e4j.getKeyName();
		if(DEBUG) {
			System.out.println("getCurrentName() " + cname);
		}
		return cname;
	}

	@Override
	public JsonStreamContext getParsingContext() {
		if(DEBUG) {
			System.out.println("getParsingContext()");
		}
		return null;
	}

	@Override
	public String getText() throws IOException {
		String s = e4j.getValueString();
		if(DEBUG) {
			System.out.println("getText() " + s);
		}
		return s;
	}

	@Override
	public char[] getTextCharacters() throws IOException {
		String s = getText();
		if(DEBUG) {
			System.out.println("getTextCharacters() " + s);
		}
		return s.toCharArray();
	}

	@Override
	public int getTextLength() throws IOException {
		String s = getText();
		int len = s.length();
		if(DEBUG) {
			System.out.println("getTextLength() " + len);
		}
		return len;
	}

	@Override
	public int getTextOffset() throws IOException {
		if(DEBUG) {
			System.out.println("getTextOffset() " + 0);
		}
		return 0;
	}

	@Override
	public boolean hasTextCharacters() {
		if(DEBUG) {
			System.out.println("hasTextCharacters()");
		}
		return false;
	}

	@Override
	public boolean isClosed() {
		if(DEBUG) {
			System.out.println("isClosed() " + isClosed);
		}
		return isClosed;
	}

	@Override
	public JsonToken nextToken() throws IOException {
		try {
			EXI4JSONEvent e4je = e4j.readNextEvent();
			JsonToken jt;
			if (e4je == null) {
				isClosed = true;
				jt = JsonToken.NOT_AVAILABLE;
			} else {
				switch (e4je) {
				case KEY_NAME:
					jt = JsonToken.FIELD_NAME;
					break;
				case START_OBJECT:
					jt = JsonToken.START_OBJECT;
					break;
				case END_OBJECT:
					jt = JsonToken.END_OBJECT;
					break;
				case START_ARRAY:
					jt = JsonToken.START_ARRAY;
					break;
				case END_ARRAY:
					jt = JsonToken.END_ARRAY;
					break;
				case VALUE_STRING:
					jt = JsonToken.VALUE_STRING;
					break;
				case VALUE_NUMBER:
					double d = e4j.getValueNumberDouble();
					if (d == (long) d) {
						// integral
						jt = JsonToken.VALUE_NUMBER_INT;
					} else {
						jt = JsonToken.VALUE_NUMBER_FLOAT;
					}
					break;
				case VALUE_BOOLEAN:
					if (e4j.getValueBoolean()) {
						jt = JsonToken.VALUE_TRUE;
					} else {
						jt = JsonToken.VALUE_FALSE;
					}
					break;
				case VALUE_NULL:
					jt = JsonToken.VALUE_NULL;
					break;
				default:
					jt = JsonToken.NOT_AVAILABLE;
				}
			}

			if(DEBUG) {
				System.out.println("nextToken() " + jt);
			}

			this._currToken = jt;

			return jt;
		} catch (EXIException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void overrideCurrentName(String name) {
		if(DEBUG) {
			System.err.println("overrideCurrentName(" + name + "");
		}
	}


	@Override
	public ObjectCodec getCodec() {
		if(DEBUG) {
			System.out.println("getCodec() " + _objectCodec);
		}
		return _objectCodec;
	}
	
    @Override
    public void setCodec(ObjectCodec c) {
		if(DEBUG) {
			System.err.println("setCodec(" + c + "");
		}
        _objectCodec = c;
}
	

	@Override
	public JsonLocation getTokenLocation() {
		if(DEBUG) {
			System.out.println("getTokenLocation()");
		}
		return new JsonLocation(_ioContext.getSourceReference(), _tokenInputTotal, // bytes
				-1, -1, (int) _tokenInputTotal); // char offset, line, column

	}

	@Override
	public JsonLocation getCurrentLocation() {
		if(DEBUG) {
			System.out.println("getCurrentLocation()");
		}
		final long offset = 0; // _currInputProcessed + _inputPtr;
		return new JsonLocation(_ioContext.getSourceReference(), offset, // bytes
				-1, -1, (int) offset); // char offset, line, column
	}


	@Override
	public BigInteger getBigIntegerValue() throws IOException {
		double d = e4j.getValueNumberDouble();
		BigInteger bi = new BigInteger(d + "");
		if(DEBUG) {
			System.out.println("getBigIntegerValue() " + bi);
		}
		return bi;
	}
	
	@Override
	public BigDecimal getDecimalValue() throws IOException {
		double d = e4j.getValueNumberDouble();
		BigDecimal bd = BigDecimal.valueOf(d);
		if(DEBUG) {
			System.out.println("getDecimalValue() " + bd);
		}
		return bd;
	}

	@Override
	public double getDoubleValue() throws IOException {
		double d = e4j.getValueNumberDouble();
		if(DEBUG) {
			System.out.println("getDoubleValue() " + d);
		}
		return d;
	}

	@Override
	public float getFloatValue() throws IOException {
		double d = e4j.getValueNumberDouble();
		float f = (float) d;
		if(DEBUG) {
			System.out.println("getFloatValue() " + f);
		}
		return f;
	}

	@Override
	public int getIntValue() throws IOException {
		double d = e4j.getValueNumberDouble();
		int i = (int) d;
		if(DEBUG) {
			System.out.println("getIntValue() " + i);
		}
		return i;
	}

	@Override
	public long getLongValue() throws IOException {
		double d = e4j.getValueNumberDouble();
		long l = (long) d;
		if(DEBUG) {
			System.out.println("getLongValue() " + l);
		}
		
		return l;
	}

	@Override
	public NumberType getNumberType() throws IOException {
		NumberType nt;
		// INT, LONG, BIG_INTEGER, FLOAT, DOUBLE, BIG_DECIMAL
		double d = e4j.getValueNumberDouble();
		if (d == (long) d) {
			// integral
			if (d == (int) d) {
				nt = NumberType.INT;
			} else {
				// long
				nt = NumberType.LONG;
			}
		} else {
			nt = NumberType.DOUBLE;
		}

		if(DEBUG) {
			System.out.println("getNumberType() " + nt);
		}
		
		return nt;
	}

	@Override
	public Number getNumberValue() throws IOException {
		double d = e4j.getValueNumberDouble();
		if(DEBUG) {
			System.out.println("getNumberValue() " + d);
		}
		
		return d;
	}

	@Override
	public Version version() {
		if(DEBUG) {
			System.out.println("version() " + Version.unknownVersion());
		}
		
		return Version.unknownVersion();
	}

}
