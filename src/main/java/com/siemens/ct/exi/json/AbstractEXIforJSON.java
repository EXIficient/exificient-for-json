package com.siemens.ct.exi.json;

import java.io.IOException;
import java.net.URL;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.Grammars;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

public abstract class AbstractEXIforJSON {

	static final String NAMESPACE_EXI4JSON = "http://www.w3.org/2015/EXI/json";
	
	static final String LOCALNAME_MAP = "map";
	static final String LOCALNAME_ARRAY = "array";
	static final String LOCALNAME_STRING = "string";
	static final String LOCALNAME_NUMBER = "number";
	static final String LOCALNAME_BOOLEAN = "boolean";
	static final String LOCALNAME_NULL = "null";
	static final String LOCALNAME_KEY = "key";
	
	
	final EXIFactory ef;
	
	public AbstractEXIforJSON() throws EXIException, IOException {
		this.ef = DefaultEXIFactory.newInstance();;	
		
		// setup EXI
		URL urlXSD = new URL("http://www.w3.org/XML/EXI/docs/json/schema-for-json.xsd");
		Grammars g = GrammarFactory.newInstance().createGrammars(urlXSD.openStream());
		ef.setGrammars(g);
		ef.setFidelityOptions(FidelityOptions.createStrict());
	}
	
}
