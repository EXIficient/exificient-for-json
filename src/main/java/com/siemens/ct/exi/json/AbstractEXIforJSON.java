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
import java.net.URL;

import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.FidelityOptions;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.core.grammars.Grammars;
import com.siemens.ct.exi.core.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.grammars.GrammarFactory;

public abstract class AbstractEXIforJSON {
	
	/**
	 * EXI Grammars for XML schema from https://www.w3.org/TR/2016/WD-exi-for-json-20160128/
	 * 
	 * @see XML_SCHEMA_FOR_JSON
	 */
	static Grammars g1;
	
	/**
	 * EXI Grammars for XML schema from https://www.w3.org/TR/2016/WD-exi-for-json-20160823/
	 * 
	 * @see XML_SCHEMA_FOR_EXI4JSON
	 */
	static Grammars g2;
	
	final EXIFactory ef;
	final String schemaId;
	
	public AbstractEXIforJSON() throws EXIException, IOException {
		this(DefaultEXIFactory.newInstance());
	}
	
	public AbstractEXIforJSON(EXIFactory ef) throws EXIException, IOException {
		// default schema
		this(ef, EXI4JSONConstants.XML_SCHEMA_FOR_EXI4JSON);
	}
	
	public AbstractEXIforJSON(String schemaId) throws EXIException, IOException {
		// default schema EXI factory
		this(DefaultEXIFactory.newInstance(), schemaId);
	}
	
	public AbstractEXIforJSON(EXIFactory ef, String schemaId) throws EXIException, IOException {
		this.ef = ef;
		this.schemaId = schemaId;
		
		if(ef.getGrammars().isSchemaInformed()) {
			// schema-informed grammars (dedicated grammars in use)
		} else {
			// setup EXI schema
			Grammars g;
			if(EXI4JSONConstants.XML_SCHEMA_FOR_JSON.equals(schemaId)) {
				g = loadGrammars1();
			} else {
				// default
				g = loadGrammars2();
			}
			ef.setGrammars(g);
			
			// set to strict
			ef.getFidelityOptions().setFidelity(FidelityOptions.FEATURE_STRICT, true);
		}
	}
	
	public EXIFactory getEXIFactory() {
		return this.ef;
	}
	
	static Grammars loadGrammars1() throws IOException, EXIException {
		if(g1 == null) {
			URL urlXSD  =  AbstractEXIforJSON.class.getResource("/schema-for-json.xsd");
			InputStream isXSD = urlXSD.openStream();
			g1 = GrammarFactory.newInstance().createGrammars(isXSD);
			isXSD.close();			
		}
		
		return g1;
	}
	
	static Grammars loadGrammars2() throws IOException, EXIException {
		if(g2 == null) {
			URL urlXSD  =  AbstractEXIforJSON.class.getResource("/exi4json.xsd");
			InputStream isXSD = urlXSD.openStream();
			g2 = GrammarFactory.newInstance().createGrammars(isXSD);
			isXSD.close();			
		}
		
		return g2;
	}
	
}
