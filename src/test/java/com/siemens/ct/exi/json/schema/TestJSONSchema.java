package com.siemens.ct.exi.json.schema;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.everit.json.schema.ArraySchema;
import org.everit.json.schema.BooleanSchema;
import org.everit.json.schema.CombinedSchema;
import org.everit.json.schema.CombinedSchema.ValidationCriterion;
import org.everit.json.schema.EnumSchema;
import org.everit.json.schema.NumberSchema;
import org.everit.json.schema.ObjectSchema;
import org.everit.json.schema.Schema;
import org.everit.json.schema.StringSchema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.siemens.ct.exi.json.EXIforJSONGenerator;

public class TestJSONSchema {

	private final static Logger LOGGER = Logger.getLogger(TestJSONSchema.class.getName());

	public TestJSONSchema(InputStream inputStream) throws JSONException, ParserConfigurationException, TransformerException {
		// InputStream inputStream = getClass().getResourceAsStream("/json-schema/basic.schema.json");
		Reader inputReader = new InputStreamReader(inputStream);

		JSONObject rawSchema = new JSONObject(new JSONTokener(inputReader));
		Schema schema = SchemaLoader.load(rawSchema);

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "schema");
		doc.appendChild(doc.createComment("Root element"));
		doc.appendChild(rootElement);

		// processSchema(doc, rootElement, schema);
		processSchemaMap(doc, rootElement, (ObjectSchema) schema);

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		StreamResult result = new StreamResult(baos); // new
														// File("C:\\file.xml"));

		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);

		transformer.transform(source, result);

		System.out.println("------");
		System.out.println(new String(baos.toByteArray()));
		System.out.println("------");

//		// valid instance
//		try {
//			JSONObject jsonInstanceSchema = new JSONObject(
//					new JSONTokener(new InputStreamReader(getClass().getResourceAsStream("/json-schema/basic1.json"))));
//			schema.validate(jsonInstanceSchema);
//		} catch (ValidationException e) {
//			// prints #/rectangle/a: -5.0 is not higher or equal to 0
//			System.out.println(e.getMessage());
//		}
//
//		// invalid instance
//		try {
//			schema.validate(new JSONObject("{\"firstName\" : \"Sepp\"}"));
//		} catch (ValidationException e) {
//			// prints #: required key [lastName] not found
//			System.out.println(e.getMessage());
//		}
	}
	
	void processSchemaMap(Document doc, Element element, ObjectSchema os) {
		Element elMap = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "element");
		elMap.setAttribute("name", "map");
		element.appendChild(elMap);

		Element elComplexType2 = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "complexType");
		elMap.appendChild(elComplexType2);

		Element elAll = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "all");
		elComplexType2.appendChild(elAll);

		List<String> requiredProps = os.getRequiredProperties();
		LOGGER.info("requiredProps: " + requiredProps);

		Map<String, Schema> propSchema = os.getPropertySchemas();
		Set<String> keys = propSchema.keySet();
		for (String key : keys) {
			Schema s = propSchema.get(key);
			LOGGER.info("\t" + key + " --> " + s);

			Element el = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "element");
			el.setAttribute("name", EXIforJSONGenerator.escapeKey(key));
			if (requiredProps.contains(key)) {
				// required --> default is 1
			} else {
				el.setAttribute("minOccurs", "0");
			}
			elAll.appendChild(el);

			//
			processSchema(doc, el, s);
		}
	}

	void processSchema(Document doc, Element element, Schema schema) {
		// show internals
		// ArraySchema
		// BooleanSchema
		// CombinedSchema
		// EmptySchema
		// EnumSchema
		// NotSchema
		// NullSchema
		// NumberSchema
		// ObjectSchema
		// ReferenceSchema
		// StringSchama
		if (schema instanceof ObjectSchema) {
			LOGGER.info("ObjectSchema");
			ObjectSchema os = (ObjectSchema) schema;
			
//			Element elMap = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "element");
			
			Element elComplexType1 = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "complexType");
			element.appendChild(elComplexType1);
			
			Element elSequence = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "sequence");
			elComplexType1.appendChild(elSequence);

			processSchemaMap(doc, elSequence, os);

		} else if (schema instanceof ArraySchema) {
			LOGGER.info("ArraySchema");
			ArraySchema as = (ArraySchema) schema;

			Element elComplexType1 = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "complexType");
			element.appendChild(elComplexType1);
			
			Element elSequence1 = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "sequence");
			elComplexType1.appendChild(elSequence1);
			
			Element elArray = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "element");
			elArray.setAttribute("name", "array");
			elSequence1.appendChild(elArray);
			
			
			// TODO check result!!!
			processSchema(doc, elSequence1, as.getAllItemSchema());
			

//			Element elComplexType2 = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "complexType");
//			elArray.appendChild(elComplexType2);
//
//			Element elSequence2 = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "sequence");
//			elComplexType2.appendChild(elSequence2);
//			
//			// TODO check result!!!
//			processSchema(doc, elSequence2, as.getAllItemSchema());
			
//			for(Schema s : as.getItemSchemas()) {
//				processSchema(doc, elSequence, s);
//			}
			
		} else if (schema instanceof CombinedSchema) {
			CombinedSchema cs = (CombinedSchema) schema;
			
//			The keywords used to combine schemas are:
//
//			    allOf: Must be valid against all of the subschemas
//			    anyOf: Must be valid against any of the subschemas
//			    oneOf: Must be valid against exactly one of the subschemas

			Element elComplexType = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "complexType");
			element.appendChild(elComplexType);

			// TODO check which one (allOf, anyOf, oneOf)!!
			ValidationCriterion vc = cs.getCriterion();
			
			Element elChoice = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "choice");
			elComplexType.appendChild(elChoice);

			
			// Collection<Schema> collSchemas = cs.getSubschemas();
			for(Schema s : cs.getSubschemas()) {
				processSchema(doc, elChoice, s);
			}
			
		} else if (schema instanceof EnumSchema) {
			EnumSchema es = (EnumSchema) schema;
			
			element.appendChild(doc.createComment("add enum: " + es.toString()));
			
		} else if (schema instanceof StringSchema) {
			StringSchema ss = (StringSchema) schema;

			if (ss.getDescription() != null) {
				element.appendChild(doc.createComment(ss.getDescription()));
			}
			if (ss.getFormatValidator() != null) {
				element.appendChild(doc.createComment(ss.getFormatValidator().formatName()));
			}

			Element elComplexType = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "complexType");
			element.appendChild(elComplexType);

			Element elSequence = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "sequence");
			elComplexType.appendChild(elSequence);

			Element el = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "element");
			el.setAttribute("name", "string");
			String typeName = el.getPrefix() == null ? "string" : el.getPrefix() + "" + "string";
			el.setAttribute("type", typeName);
			elSequence.appendChild(el);

		} else if (schema instanceof NumberSchema) {
			NumberSchema ns = (NumberSchema) schema;

			if (ns.getDescription() != null) {
				element.appendChild(doc.createComment(ns.getDescription()));
			}
			if (ns.requiresInteger()) {
				element.appendChild(doc.createComment("type: " + "integer"));
			}
			if (ns.getMinimum() != null) {
				element.appendChild(doc.createComment("minimum: " + ns.getMinimum()));
			}
			if (ns.getMaximum() != null) {
				element.appendChild(doc.createComment("maximum: " + ns.getMaximum()));
			}

			Element elComplexType = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "complexType");
			element.appendChild(elComplexType);

			Element elSequence = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "sequence");
			elComplexType.appendChild(elSequence);

			Element el = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "element");
			el.setAttribute("name", "number");
			// TODO integer
			String typeName = el.getPrefix() == null ? "double" : el.getPrefix() + "" + "double";
			el.setAttribute("type", typeName);
			elSequence.appendChild(el);

		} else if (schema instanceof BooleanSchema) {
			BooleanSchema bs = (BooleanSchema) schema;

			if (bs.getDescription() != null) {
				element.appendChild(doc.createComment(bs.getDescription()));
			}

			Element elComplexType = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "complexType");
			element.appendChild(elComplexType);

			Element elSequence = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "sequence");
			elComplexType.appendChild(elSequence);

			Element el = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "element");
			el.setAttribute("name", "boolean");
			String typeName = el.getPrefix() == null ? "boolean" : el.getPrefix() + "" + "boolean";
			el.setAttribute("type", typeName);
			elSequence.appendChild(el);

			
		} else {
			LOGGER.info("Unknown Schema " + schema);
			throw new RuntimeException("Unknown Schema " + schema);
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println("Start TestJSONSchema");
		
		System.out.println("Basic JSON Schema");
		InputStream inputStreamBasic = TestJSONSchema.class.getResourceAsStream("/json-schema/basic.schema.json");
		new TestJSONSchema(inputStreamBasic);
		
		System.out.println("Fstab JSON Schema");
		InputStream inputStreamFstab = TestJSONSchema.class.getResourceAsStream("/json-schema/fstab.schema.json");
		new TestJSONSchema(inputStreamFstab);

	}
}
