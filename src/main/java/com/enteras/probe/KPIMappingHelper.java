package com.enteras.probe;

import java.util.Properties;

import com.enteras.Helper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class KPIMappingHelper {

	static ObjectNode dynamicMapping(JsonNode items, Properties properties, ObjectNode resObjectNode) {


		ArrayNode itemNodes = Helper.getArrayNode(items);

		String key_ = null;
		for(JsonNode jsonNode : itemNodes){

			key_ = jsonNode.get("key_").asText();
			if(key_.indexOf("[")!=-1){
				String subKey = key_.substring(0, key_.indexOf("["));
				if(properties.containsKey(subKey)) {
					resObjectNode.put(properties.getProperty(subKey)+"_"+key_.substring(key_.indexOf("[")+1, key_.length()-1), jsonNode.get("lastvalue").asText());
				}
			}

		}
		return resObjectNode;

	}

	static ObjectNode mappingOnNames(JsonNode items, Properties properties, ObjectNode resObjectNode) {


		ArrayNode itemNodes = Helper.getArrayNode(items);

		String key_ = null;
		for(JsonNode jsonNode : itemNodes){

			key_ = jsonNode.get("name").asText();
			if(properties.containsKey(key_)) {
				resObjectNode.put(properties.getProperty(key_), jsonNode.get("lastvalue").asText());
			}

		}

		return resObjectNode;

	}


	static ObjectNode mappingOnKey(JsonNode items, Properties properties, ObjectNode resObjectNode) {

		ArrayNode itemNodes = Helper.getArrayNode(items);

		String key_ = null;
		for(JsonNode jsonNode : itemNodes){

			key_ = jsonNode.get("key_").asText();

			if(properties.containsKey(key_)) {
				resObjectNode.put(properties.getProperty(key_), jsonNode.get("lastvalue").asText());
			}

		}

		return resObjectNode;

	}


	public static void mapping( JsonNode items, Properties properties, ObjectNode resObjectNode ){

		mappingOnKey(items, properties, resObjectNode);
		dynamicMapping(items, properties, resObjectNode);
		mappingOnNames(items, properties, resObjectNode);

	}


}
