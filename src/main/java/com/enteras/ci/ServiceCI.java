package com.enteras.ci;

import java.util.Map;
import java.util.Properties;

import com.enteras.Helper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;


public class ServiceCI extends GeneralAppCI {

	//public String typeDetails;
	public String status;

	public static Map<String, String> ciAttMap ;


	static {
		logger.debug("Loading class ServiceCI ...");
		
		String className = ServiceCI.class.getSimpleName();		
		classFieldMap.put(className, Helper.loadProperties(ServiceCI.class));
		logger.debug("Field property mapping");
		logger.debug("Key:" + className + " value: " + classFieldMap.get(className));
	}


	public void populateFieldBasedOnItems(JsonNode input) {
		
		
		Properties properties = Helper.loadProperties("/config/Zabbix/Zabbix_ServiceCI.properties");
		ArrayNode arrayNode = Helper.getArrayNode(input);
		for(JsonNode jsonNode :  arrayNode) { 
			String keyText = jsonNode.get("key_").asText();
			typeDetails = properties.getProperty(keyText);
			status = jsonNode.get("lastvalue").asText();
		}

	}


}
