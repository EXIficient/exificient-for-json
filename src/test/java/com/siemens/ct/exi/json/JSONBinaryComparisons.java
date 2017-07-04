package com.siemens.ct.exi.json;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.json.JSONException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.json.schema.HelperJSONSchema2XSD;

public class JSONBinaryComparisons {
	
	static final String SEPARATOR = ",";

	public static byte[] encodeEXI4JSON(InputStream json) throws EXIException, IOException {
		EXIforJSONGenerator e4jGenerator = new EXIforJSONGenerator(); // ef);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		e4jGenerator.generate(json, baos);
		// System.out.println("EXI4JSON " + baos.size());
		return baos.toByteArray();
	}
	
	public static byte[] encodeEXI4JSON(InputStream json, EXIFactory ef) throws EXIException, IOException {
		EXIforJSONGenerator e4jGenerator = new EXIforJSONGenerator(ef);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		e4jGenerator.generate(json, baos);
		// System.out.println("EXI4JSON " + baos.size());
		return baos.toByteArray();
	}

	public static byte[] encodeCBOR(InputStream json) throws EXIException, IOException {
		CBORFactory f = new CBORFactory();

		ObjectMapper mapperJson = new ObjectMapper();
		ObjectMapper mapperCbor = new ObjectMapper(f);
		JsonNode node = mapperJson.readValue(json, JsonNode.class);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		mapperCbor.writeTree(f.createGenerator(baos), node);

		// System.out.println("CBOR " + baos.size());

		@SuppressWarnings("unused")
		JsonNode node2 = mapperCbor.readTree(baos.toByteArray());

		return baos.toByteArray();
	}
	
	public static byte[] encodeSmile(InputStream json) throws EXIException, IOException {
		SmileFactory f = new SmileFactory();

		ObjectMapper mapperJson = new ObjectMapper();
		ObjectMapper mapperCbor = new ObjectMapper(f);
		JsonNode node = mapperJson.readValue(json, JsonNode.class);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		mapperCbor.writeTree(f.createGenerator(baos), node);

		// System.out.println("Smile " + baos.size());

		@SuppressWarnings("unused")
		JsonNode node2 = mapperCbor.readTree(baos.toByteArray());

		return baos.toByteArray();
	}

	public static List<File> getFilesForFolder(final File folder, String suffix, boolean recursive) {
		List<File> files = new ArrayList<File>();
		addFilesForFolder(files, folder, suffix, recursive);
		return files;
	}

	public static void test(List<File> files, String removePath, PrintStream ps, EXIFactory ef) throws EXIException, IOException {
		String header = "TestCase" + SEPARATOR + " JSON" + SEPARATOR + "CBOR" + SEPARATOR + " Smile" + SEPARATOR + " EXI4JSON";
		if(ef != null) {
			header += SEPARATOR + " EXI4JSONSchema";
		}
		ps.println(header);

		for (File fj : files) {
			test(fj, removePath, ps, ef);
		}
	}

	public static void test(File f, String removePath, PrintStream ps, EXIFactory ef) throws EXIException, IOException {
		int sizeCBOR;
		{
			InputStream isJSON = new FileInputStream(f);
			sizeCBOR = encodeCBOR(isJSON).length;
			isJSON.close();
		}
		int sizeSmile;
		{
			InputStream isJSON = new FileInputStream(f);
			sizeSmile = encodeSmile(isJSON).length;
			isJSON.close();
		}
		int sizeEXI4JSON;
		{
			InputStream isJSON = new FileInputStream(f);
			sizeEXI4JSON = encodeEXI4JSON(isJSON).length;
			isJSON.close();
		}
		int sizeEXI4JSONSchema = -1;
		if(ef != null) {
			InputStream isJSON = new FileInputStream(f);
			sizeEXI4JSONSchema = encodeEXI4JSON(isJSON, ef).length;
			isJSON.close();
		}

		// remove path
		// File fl = new File(AbstractEXIforJSON.class.getResource("/").getFile());
		String name = f.getAbsolutePath();
		// if (name.startsWith(fl.getAbsolutePath())) {
		if (name.startsWith(removePath)) {
			name = name.substring(removePath.length() + 1);
		}

		if(ef != null) {
			ps.println(name + SEPARATOR + f.length() + SEPARATOR  + sizeCBOR + SEPARATOR  + sizeSmile + SEPARATOR + sizeEXI4JSON + SEPARATOR + sizeEXI4JSONSchema);
		} else {
			ps.println(name + SEPARATOR  + f.length() + SEPARATOR  + sizeCBOR + SEPARATOR + sizeSmile + SEPARATOR + sizeEXI4JSON);
		}
		
	}

	private static void addFilesForFolder(List<File> files, final File folder, String suffix, boolean recursive) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				if (recursive) {
					addFilesForFolder(files, fileEntry, suffix, recursive);
				}
			} else {

				if (fileEntry.getName().endsWith(suffix)) {
					// System.out.println(fileEntry.getName());
					files.add(fileEntry);
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		// URL urlJSON =
		// AbstractEXIforJSON.class.getResource("/gpx/sample-set-1/gpx-1-1pts.json");
		// URL urlJSON = AbstractEXIforJSON.class.getResource("/glossary.json");

		List<File> files;
		String removePath = "";
		
//		// local test files
//		{
//			File f = new File(AbstractEXIforJSON.class.getResource("/").getFile());
//			files = getFilesForFolder(f, ".json", true);
//			removePath = f.getAbsolutePath();
//		}
		
		// WoT test-bed files
		{
			File f = new File("D:\\Projects\\WoT\\wot-thing-description-danielpeintner\\test-bed\\");
			files = getFilesForFolder(f, ".jsonld", true);
			removePath = f.getAbsolutePath();
		}
		
		EXIFactory ef = null;
		
		if(false) {
			// Tests with additional schema (factory) knowledge
			ef = DefaultEXIFactory.newInstance();
			// ef.setFidelityOptions(FidelityOptions.createStrict());
			InputStream isJsonSchema = new FileInputStream("D:\\Projects\\WoT\\thing-description-tests\\wot-td-osaka.jsonschema");
			File fXSD = File.createTempFile("json", ".xsd");
			OutputStream osXSD = new FileOutputStream(fXSD);
			HelperJSONSchema2XSD.jsonSchema2Xsd(isJsonSchema, osXSD);
			ef.setGrammars(GrammarFactory.newInstance().createGrammars(fXSD.getAbsolutePath()));
		}

		test(files, removePath, System.out, ef);
		
//		for (File f : files) {
//			byte[] b = encodeEXI4JSON(new FileInputStream(f), ef);
//			System.out.println(f + "; " + b.length);
//		}

		// {
		// InputStream isJSON = urlJSON.openStream();
		// encodeEXI4JSON(isJSON);
		// isJSON.close();
		// }
		// {
		// InputStream isJSON = urlJSON.openStream();
		// encodeCBOR(isJSON);
		// isJSON.close();
		// }

	}

}
