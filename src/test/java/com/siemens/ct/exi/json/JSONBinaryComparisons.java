package com.siemens.ct.exi.json;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.siemens.ct.exi.exceptions.EXIException;

public class JSONBinaryComparisons {

	public static byte[] encodeEXI4JSON(InputStream json) throws EXIException, IOException {
		EXIforJSONGenerator e4jGenerator = new EXIforJSONGenerator(); // ef);
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

	public static List<File> getFilesForFolder(final File folder, String suffix, boolean recursive) {
		List<File> files = new ArrayList<File>();
		addFilesForFolder(files, folder, suffix, recursive);
		return files;
	}

	public static void test(List<File> files, PrintStream ps) throws EXIException, IOException {
		ps.println("TestCase; JSON; CBOR; EXI4JSON");

		for (File fj : files) {
			test(fj, ps);
		}
	}

	public static void test(File f, PrintStream ps) throws EXIException, IOException {
		int sizeCBOR;
		{
			InputStream isJSON = new FileInputStream(f);
			sizeCBOR = encodeCBOR(isJSON).length;
			isJSON.close();
		}
		int sizeEXI4JSON;
		{
			InputStream isJSON = new FileInputStream(f);
			sizeEXI4JSON = encodeEXI4JSON(isJSON).length;
			isJSON.close();
		}

		// remove local path
		File fl = new File(AbstractEXIforJSON.class.getResource("/").getFile());
		String name = f.getAbsolutePath();
		if (name.startsWith(fl.getAbsolutePath())) {
			name = name.substring(fl.getAbsolutePath().length() + 1);
		}

		ps.println(name + "; " + f.length() + "; " + sizeCBOR + "; " + sizeEXI4JSON);
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

	public static void main(String[] args) throws EXIException, IOException {
		// URL urlJSON =
		// AbstractEXIforJSON.class.getResource("/gpx/sample-set-1/gpx-1-1pts.json");
		// URL urlJSON = AbstractEXIforJSON.class.getResource("/glossary.json");

		File f = new File(AbstractEXIforJSON.class.getResource("/").getFile());
		System.out.println(f);
		List<File> files = getFilesForFolder(f, ".json", true);

		test(files, System.out);

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
