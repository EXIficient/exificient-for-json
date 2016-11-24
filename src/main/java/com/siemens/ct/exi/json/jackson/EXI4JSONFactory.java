package com.siemens.ct.exi.json.jackson;

import com.fasterxml.jackson.core.JsonFactory;

public class EXI4JSONFactory extends JsonFactory {

	private static final long serialVersionUID = 1L;

	/**
	 * Name used to identify Smile format. (and returned by
	 * {@link #getFormatName()}
	 */
	public final static String FORMAT_NAME_EXI4JSON = "EXI4JSON";

	/*
	 * /********************************************************** /* Format
	 * detection functionality
	 * /**********************************************************
	 */

	@Override
	public String getFormatName() {
		return FORMAT_NAME_EXI4JSON;
	}
}
