package com.enteras;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;



public class Helper {

	static final Logger logger = LoggerFactory.getLogger(Helper.class);

	private static Properties properties ;

	static {
		properties = loadProperties(EnterasConstants.ZABBIX_MISC_PROPERTIES_PATH);
	}



	@SuppressWarnings("rawtypes")
	public static Map<String, String> loadProperties(Class classType) {

		List<String> superClasses = getListParentClass(classType);
		Map<String, String> ciAttMap = new HashMap<>();

		Set<String> valuesUpdate = new HashSet<>();

		for(String className : superClasses) { 

			String propetyFileName = "/config/Zabbix/Zabbix_" + className + ".properties";
			logger.debug(" propetyFileName: " + propetyFileName);

			Properties properties = loadProperties(propetyFileName);
			Set<Object> keys = properties.keySet();

			for(Object k:keys){
				String key = (String) k;
				String value = properties.getProperty(key);

				if(valuesUpdate.contains(value)) continue;

				else valuesUpdate.add(value);

				logger.debug(" adding property key:" + key + " value:" + value);

				ciAttMap.put(key, value);

			}

		}

		return ciAttMap;

	}


	public static Properties loadProperties(String path) {

		Properties properties = new Properties();

		try { 
			
			
			properties.load( Helper.class.getResourceAsStream(path) );

		} catch( Exception exp ){
			logger.debug("Exception while reading resource: " + path);
			exp.printStackTrace();
		}

		return properties;


	}


	@SuppressWarnings("rawtypes")
	private static List<String> getListParentClass(Class classType) {

		List<String> ltSuperClasses = new ArrayList<>();

		while (classType != Object.class) {
			ltSuperClasses.add(classType.getSimpleName());
			classType = classType.getSuperclass();
		}

		return ltSuperClasses;


	}

	public static String readFile(String path) {

		StringBuilder response = new StringBuilder();

		logger.debug("Path:" + path);
		BufferedReader br = null;
		try {

			String sCurrentLine;

			br = new BufferedReader( 
					new InputStreamReader(
							Helper.class.getResourceAsStream(path)));

			while ((sCurrentLine = br.readLine()) != null) {
				response.append(sCurrentLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {

			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
				throw new RuntimeException(ex);
			}

		}

		return response.toString();

	}


	public static String getJsonString(Object obj) {

		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			logger.error("Eception while converting Object to String " + e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}


	public static ArrayNode getArrayNode(JsonNode jsonNode) {

		if(jsonNode.isArray())
			return (ArrayNode) jsonNode;

		throw new RuntimeException("Cannot convert JsonNode: " + jsonNode + " to ArrayNode");

	}


	public static void jsonPrettyPrint(Object obj) {

		ObjectMapper mapper = new ObjectMapper();
		try {

			logger.debug( mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj) );

		} catch (JsonProcessingException e) {
			logger.error("Exception while converting Object to String " + e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}


	public static JsonNode convStrToJsonNode(String input) {

		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readTree(input);
		} catch (JsonProcessingException e) {
			logger.error("Exception while converting String to JsonNode " + e);
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IOException e) {
			logger.error("Exception while converting String to JsonNode " + e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}
	
	public static JsonNode convObjToJsonNode(Object obj) {
		return convStrToJsonNode(getJsonString(obj));
		
	}
	
	
	public static ObjectNode createObjectNode() {

		ObjectMapper mapper = new ObjectMapper();
		return  mapper.createObjectNode();
	
	}
	
	

	public static String getConfigPathForZabbix() {

		return "config/Zabbix";
	}


	public static String getMiscProperties( String key) {
		return properties.getProperty(key);
	}
	
	public static String getCurrentDate(){
		
		DateTime dt = new DateTime().withTime(0, 0, 0, 0);
		String time = String.valueOf(dt.getMillis()/1000);
				
		return time;
		
	}

	
	public static JsonNode createIdForTemplate(JsonNode jsonNode, String assetId) {
		
		String templateId = jsonNode.get("templateid").asText();		
		if(jsonNode.isObject()) 
			((ObjectNode) jsonNode).put("uniqueid", templateId + "_" + assetId);
		
		return jsonNode;
	}
	

}
