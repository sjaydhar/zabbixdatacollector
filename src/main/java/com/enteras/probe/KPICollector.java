package com.enteras.probe;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enteras.EnterasConstants;
import com.enteras.Helper;
import com.enteras.clientapi.DefaultZabbixApi;
import com.enteras.clientapi.ZabbixApi;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class KPICollector {

	static final Logger logger = LoggerFactory.getLogger(KPICollector.class);

	public static JsonNode getKPIMetrics(String url, String userName, String password, String hostId, String collectorId, String metricType) {

		logger.debug("Inside getKPIMetrics");
		logger.debug("Input hostId:" + hostId + " metricType:" + metricType);

		Properties properties = Helper.loadProperties(EnterasConstants.ZABBIX_KPI_MAPPING_PATH +  metricType + ".properties");
		logger.debug("property file is "+properties.toString());

		ZabbixApi zabbixApi = DefaultZabbixApi.getInstance(url, userName, password);
		JsonNode items;
		if(hostId.indexOf("_")>0)
			items = zabbixApi.getAllItemForHost(hostId.substring(0, hostId.indexOf("_")));
		else
			items = zabbixApi.getAllItemForHost(hostId);

		ObjectNode resObjectNode = mapping(items, properties);
		resObjectNode = dynamicMapping(items, properties, resObjectNode);
		resObjectNode = mappingOnNames(items, properties, resObjectNode);

		if(hostId.indexOf("_")>0)
			resObjectNode.put("assetId", hostId.substring(hostId.indexOf("_")+1, hostId.length())+"_"+hostId.substring(0, hostId.indexOf("_")));
		else{
			resObjectNode.put("assetId", hostId);
		}
		resObjectNode.put("kpiType", metricType + "_KPI");
		resObjectNode.put("timestamp", String.valueOf(System.currentTimeMillis()));


		logger.debug("Response Json Object is\n  "+resObjectNode);
		Helper.jsonPrettyPrint(resObjectNode);

		return resObjectNode;
	}

	public static ObjectNode mapping(JsonNode items, Properties properties) {


		ObjectNode resObjectNode = Helper.createObjectNode();
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

	public static ObjectNode mappingOnNames(JsonNode items, Properties properties, ObjectNode resObjectNode) {


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


	public static ObjectNode dynamicMapping(JsonNode items, Properties properties, ObjectNode resObjectNode) {


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



	public static void main(String[] args) {
		getKPIMetrics("http://192.168.204.152/zabbix/api_jsonrpc.php", "Admin", "zabbix", "10158","",  "Hardware");
		KPICollectorProbe kpiProbe = new KPICollectorProbe("http://192.168.204.152/zabbix/api_jsonrpc.php", "Admin", "zabbix", "10");
		kpiProbe.getMetricsData_New();

	}

}
