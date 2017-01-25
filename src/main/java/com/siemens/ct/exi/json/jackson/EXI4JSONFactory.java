package com.siemens.ct.exi.json.jackson;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.io.IOContext;
import com.siemens.ct.exi.exceptions.EXIException;

public class EXI4JSONFactory extends JsonFactory {

	private static final long serialVersionUID = 1L;

	/**
	 * Name used to identify format. (and returned by
	 * {@link #getFormatName()}
	 */
	public final static String FORMAT_NAME_EXI4JSON = "EXI4JSON";

	/*
	 * Configuration
	 */
	protected int _formatParserFeatures;
	protected int _formatGeneratorFeatures;

	public EXI4JSONFactory() {
		this(null);
	}

	public EXI4JSONFactory(ObjectCodec oc) {
		super(oc);
		_formatParserFeatures = 0; // DEFAULT_EXI4JSON_PARSER_FEATURE_FLAGS;
		_formatGeneratorFeatures = 0; // DEFAULT_EXI4JSON_GENERATOR_FEATURE_FLAGS;
	}

	/*
	 * Format detection functionality
	 */

	@Override
	public String getFormatName() {
		return FORMAT_NAME_EXI4JSON;
	}

	/*
	 * Capability introspection
	 */

	@Override
	public boolean canHandleBinaryNatively() {
		return true;
	}

	/*
	 * Overridden parser factory methods, new (2.1)
	 */

	@Override
	public EXI4JSONParser createParser(File f) throws IOException {
		return _createParser(new FileInputStream(f), _createContext(f, true));
	}

	@Override
	public EXI4JSONParser createParser(URL url) throws IOException {
		return _createParser(_optimizedStreamFromURL(url), _createContext(url, true));
	}

	@Override
	public EXI4JSONParser createParser(InputStream in) throws IOException {
		return _createParser(in, _createContext(in, false));
	}

	@Override
	public EXI4JSONParser createParser(byte[] data) throws IOException {
		return _createParser(data, 0, data.length, _createContext(data, true));
	}

	@Override
	public EXI4JSONParser createParser(byte[] data, int offset, int len) throws IOException {
		return _createParser(data, offset, len, _createContext(data, true));
	}

	/** 
	 * Overridden internal factory methods
	 */
	@Override
	protected IOContext _createContext(Object srcRef, boolean resourceManaged) {
		return super._createContext(srcRef, resourceManaged);
	}

	/**
	 * Overridden factory method that actually instantiates desired parser.
	 */
	@Override
	protected EXI4JSONParser _createParser(InputStream in, IOContext ctxt) throws IOException {
		return new EXI4JSONParser(in, ctxt);
	}

	/**
	 * Overridden factory method that actually instantiates desired parser.
	 */
	@Override
	protected EXI4JSONParser _createParser(Reader r, IOContext ctxt) throws IOException {
		return _nonByteSource();
	}

	@Override
	protected EXI4JSONParser _createParser(char[] data, int offset, int len, IOContext ctxt, boolean recyclable)
			throws IOException {
		return _nonByteSource();
	}

	/**
	 * Overridden factory method that actually instantiates desired parser.
	 */
	@Override
	protected EXI4JSONParser _createParser(byte[] data, int offset, int len, IOContext ctxt) throws IOException {
		return new EXI4JSONParser(data, offset, len, ctxt);
	}

	@Override
	protected EXI4JSONGenerator _createGenerator(Writer out, IOContext ctxt) throws IOException {
		return _nonByteTarget();
	}

	@Override
	protected EXI4JSONGenerator _createUTF8Generator(OutputStream out, IOContext ctxt) throws IOException {
		try {
			return new EXI4JSONGenerator(_formatGeneratorFeatures, _objectCodec, out, ctxt);
		} catch (EXIException e) {
			throw new IOException(e);
		}
	}

	@Override
	protected Writer _createWriter(OutputStream out, JsonEncoding enc, IOContext ctxt) throws IOException {
		return _nonByteTarget();
	}

	protected <T> T _nonByteTarget() {
		throw new UnsupportedOperationException("Can not create generator for non-byte-based target");
	}

	protected <T> T _nonByteSource() {
		throw new UnsupportedOperationException("Can not create generator for non-byte-based source");
	}

}
