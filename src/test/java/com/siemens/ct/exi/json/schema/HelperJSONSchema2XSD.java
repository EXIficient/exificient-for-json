package com.siemens.ct.exi.json.schema;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
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
import org.everit.json.schema.EmptySchema;
import org.everit.json.schema.EnumSchema;
import org.everit.json.schema.NullSchema;
import org.everit.json.schema.NumberSchema;
import org.everit.json.schema.ObjectSchema;
import org.everit.json.schema.ReferenceSchema;
import org.everit.json.schema.Schema;
import org.everit.json.schema.StringSchema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.siemens.ct.exi.json.EXI4JSONConstants;
import com.siemens.ct.exi.json.EXIforJSONGenerator;

//helper to convert JSON Schema into "EXI4JSON" XML schema
public class HelperJSONSchema2XSD {

	private final static Logger LOGGER = Logger.getLogger(HelperJSONSchema2XSD.class.getName());

	public static void jsonSchema2Xsd(InputStream isJsonSchema, OutputStream osXSD) throws JSONException, ParserConfigurationException, TransformerException {
		Reader inputReader = new InputStreamReader(isJsonSchema);

		JSONObject rawSchema = new JSONObject(new JSONTokener(inputReader));
		Schema schema = SchemaLoader.load(rawSchema);

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "schema");
		doc.appendChild(doc.createComment("Root element"));
		rootElement.setAttribute("targetNamespace", EXI4JSONConstants.NAMESPACE_EXI4JSON);
		rootElement.setAttribute("elementFormDefault", "qualified");
		doc.appendChild(rootElement);

		processSchema(doc, rootElement, schema);

		// write the XSD content into file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);

		// ByteArrayOutputStream osXSD = new ByteArrayOutputStream();
		StreamResult result = new StreamResult(osXSD); // new
														// File("C:\\file.xml"));

		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);

		transformer.transform(source, result);
	}
	
	static void processSchema(Document doc, Element element, Schema schema) {
		// internals:
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

			Element elMap = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "element");
			elMap.setAttribute("name", "map");
			element.appendChild(elMap);

			Element elComplexType2 = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "complexType");
			elMap.appendChild(elComplexType2);
			
			if(false) {
				// all
				
				Element elAll = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "all");
				elComplexType2.appendChild(elAll);

				List<String> requiredProps = os.getRequiredProperties();
				LOGGER.info("requiredProps: " + requiredProps);

				Map<String, Schema> propSchema = os.getPropertySchemas();
				Set<String> keys = propSchema.keySet();
				for (String key : keys) {
					Schema s = propSchema.get(key);
					LOGGER.info("\t" + key + " --> " + s.getId());

					Element el = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "element");
					el.setAttribute("name", EXIforJSONGenerator.escapeKey(key));
					if (requiredProps.contains(key)) {
						// required --> default is 1
					} else {
						el.setAttribute("minOccurs", "0");
					}
					elAll.appendChild(el);
					
					
					Element elComplexType1 = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "complexType");
					el.appendChild(elComplexType1);
					
					Element elSequence = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "sequence");
					elComplexType1.appendChild(elSequence);

					//
					processSchema(doc, elSequence, s);
				}
			} else {
				// choice (causes UPA in Schema1.0 in case of "additionalProperties" flag, Schema1.1 just fine)
				
				Element elChoice = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "choice");
				elChoice.setAttribute("minOccurs", "0");
				elChoice.setAttribute("maxOccurs", "unbounded");
				elComplexType2.appendChild(elChoice);

				List<String> requiredProps = os.getRequiredProperties();
				LOGGER.info("requiredProps: " + requiredProps);

				Map<String, Schema> propSchema = os.getPropertySchemas();
				Set<String> keys = propSchema.keySet();
				for (String key : keys) {
					Schema s = propSchema.get(key);
					LOGGER.info("\t" + key + " --> " + s.getId());

					Element el = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "element");
					el.setAttribute("name", EXIforJSONGenerator.escapeKey(key));
//					if (requiredProps.contains(key)) {
//						// required --> default is 1
//					} else {
//						el.setAttribute("minOccurs", "0");
//					}
					elChoice.appendChild(el);
					
					
					Element elComplexType1 = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "complexType");
					el.appendChild(elComplexType1);
					
					Element elSequence = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "sequence");
					elComplexType1.appendChild(elSequence);

					//
					processSchema(doc, elSequence, s);
				}
				
				
				if(os.permitsAdditionalProperties()) {
					Element elAny = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "any");
					elAny.setAttribute("namespace", "##targetNamespace");
					elAny.setAttribute("processContents", "lax");
					elChoice.appendChild(elAny);
				}
				
			}



		} else if (schema instanceof ArraySchema) {
			LOGGER.info("ArraySchema");
			ArraySchema as = (ArraySchema) schema;
			
			Element elArray = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "element");
			elArray.setAttribute("name", "array");
			element.appendChild(elArray);
			
			Element elComplexType1 = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "complexType");
			elArray.appendChild(elComplexType1);
			
			Element elSequence1 = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "sequence");
			elSequence1.setAttribute("minOccurs", "0");
			elSequence1.setAttribute("maxOccurs", "unbounded");
			elComplexType1.appendChild(elSequence1);
			
			// TODO check result!!!
			processSchema(doc, elSequence1, as.getAllItemSchema());
			
		} else if (schema instanceof CombinedSchema) {
			CombinedSchema cs = (CombinedSchema) schema;
			
//			The keywords used to combine schemas are:
//
//			    allOf: Must be valid against all of the subschemas
//			    anyOf: Must be valid against any of the subschemas
//			    oneOf: Must be valid against exactly one of the subschemas

			// throw new RuntimeException("TODO CombinedSchema");
			System.err.println("TODO CombinedSchema");

//			Collection<Schema> ss = cs.getSubschemas();
//			for(Schema s : ss) {
//				processSchema(doc, element, s);	
//			}
			
//			// TODO check which one (allOf, anyOf, oneOf)!!
//			ValidationCriterion vc = cs.getCriterion();
//			
//			if("oneOf".equals(vc.toString())) {
//				for(Schema s : cs.getSubschemas()) {
//					// processSchema(doc, elChoice, s);
//					processSchema(doc, element, s);
//				}
//			} else {
//				Collection<Schema> ss = cs.getSubschemas();
//				if(ss.size() != 2) {
//					throw new RuntimeException("Expected size 2");
//				} else {
//					Iterator<Schema> iter = ss.iterator();
//					Schema s1= iter.next();
//					Schema s2= iter.next();
//					processSchema(doc, element, s2);
//				}
//			}

		} else if (schema instanceof EnumSchema) {
			EnumSchema es = (EnumSchema) schema;
			
			// element.appendChild(doc.createComment(es.getPossibleValues().toString()));
			
			Element el = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "element");
			el.setAttribute("name", "string");
			element.appendChild(el);
			
			Element simpleType = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "simpleType");
			el.appendChild(simpleType);
			
			Element restriction  = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "restriction");
			restriction.setAttribute("base", "string");
			simpleType.appendChild(restriction);
			
			Set<Object> pv = es.getPossibleValues();
			for(Object o : pv) {
				// String
				Element enumeration  = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "enumeration");
				enumeration.setAttribute("value", o.toString());
				restriction.appendChild(enumeration);
			}
			
		} else if (schema instanceof StringSchema) {
			StringSchema ss = (StringSchema) schema;

			if (ss.getDescription() != null) {
				element.appendChild(doc.createComment(ss.getDescription()));
			}
			if (ss.getFormatValidator() != null) {
				element.appendChild(doc.createComment(ss.getFormatValidator().formatName()));
			}

			Element el = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "element");
			el.setAttribute("name", "string");
			String typeName = el.getPrefix() == null ? "string" : el.getPrefix() + "" + "string";
			el.setAttribute("type", typeName);
			element.appendChild(el);

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

			Element el = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "element");
			el.setAttribute("name", "number");
			// TODO integers
			String typeName = el.getPrefix() == null ? "double" : el.getPrefix() + "" + "double";
			el.setAttribute("type", typeName);
			element.appendChild(el);

		} else if (schema instanceof BooleanSchema) {
			BooleanSchema bs = (BooleanSchema) schema;

			if (bs.getDescription() != null) {
				element.appendChild(doc.createComment(bs.getDescription()));
			}

			Element el = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "element");
			el.setAttribute("name", "boolean");
			String typeName = el.getPrefix() == null ? "boolean" : el.getPrefix() + "" + "boolean";
			el.setAttribute("type", typeName);
			// elSequence.appendChild(el);
			element.appendChild(el);
		} else if (schema instanceof NullSchema) {
			NullSchema ns = (NullSchema) schema;

			Element el = doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "element");
			el.setAttribute("name", "null");
			el.appendChild(doc.createElementNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "complexType"));
			// elSequence.appendChild(el);
			element.appendChild(el);
		} else if (schema instanceof ReferenceSchema) {
			ReferenceSchema rs = (ReferenceSchema) schema;
			Schema rrs = rs.getReferredSchema();
			System.out.println("RS: " + rrs.getTitle());
			processSchema(doc, element, rrs);
		} else if (schema instanceof EmptySchema) {
			// TODO what to do with empty schema
		} else {
			LOGGER.info("Unknown Schema " + schema);
			throw new RuntimeException("Unknown Schema " + schema);
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println("Basic JSON Schema");
		InputStream inputStreamBasic = HelperJSONSchema2XSD.class.getResourceAsStream("/json-schema/basic.schema.json");
		ByteArrayOutputStream osXSD1 = new ByteArrayOutputStream();
		jsonSchema2Xsd(inputStreamBasic, osXSD1);
		System.out.println(new String(osXSD1.toByteArray()));
		
		System.out.println("Fstab JSON Schema");
		InputStream inputStreamFstab = HelperJSONSchema2XSD.class.getResourceAsStream("/json-schema/fstab.schema.json");
		ByteArrayOutputStream osXSD2 = new ByteArrayOutputStream();
		jsonSchema2Xsd(inputStreamFstab, osXSD2);
		System.out.println(new String(osXSD2.toByteArray()));
		
		// TEST
		String jsonSchema = "{\n" + 
				"    \"title\": \"Person\",\n" + 
				"    \"type\": \"object\",\n" + 
				"    \"properties\": {\n" + 
				"        \"firstName\": {\n" + 
				"            \"type\": \"string\"\n" + 
				"        },\n" + 
				"        \"lastName\": {\n" + 
				"            \"type\": \"string\"\n" + 
				"        },\n" + 
				"        \"age\": {\n" + 
				"            \"description\": \"Age in years\",\n" + 
				"            \"type\": \"integer\",\n" + 
				"            \"minimum\": 0\n" + 
				"        }\n" + 
				"    },\n" + 
				"    \"required\": [\"firstName\", \"lastName\"]\n" + 
				"}";
		ByteArrayOutputStream osXSD3 = new ByteArrayOutputStream();
		jsonSchema2Xsd(new ByteArrayInputStream(jsonSchema.getBytes()), osXSD3);
		System.out.println(new String(osXSD3.toByteArray()));

	}
}
