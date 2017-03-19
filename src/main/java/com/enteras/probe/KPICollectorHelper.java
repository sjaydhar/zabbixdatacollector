package com.enteras.probe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enteras.EnterasConstants;
import com.enteras.Helper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class KPICollectorHelper {

	final static Logger logger = LoggerFactory.getLogger(KPICollectorProbe.class);
	
	static Map<String, List<String>> appKPIMap = null;
	static Map<String, Properties> kpiProperties = null;
	static Set<String> serverRelKPISet = null;
	
	final static List<String> serverRelCISet = Arrays.asList(new String[]{"ServerCI","NetworkDeviceCI","SwitchCI"}); 

	static {

		//
		loadAllMaps();
	}


	static void loadAllMaps() {

		//Load Server related KPI
		serverRelKPISet = new HashSet<>();
		Properties properties = Helper.loadProperties(EnterasConstants.ZABBIX_CI_KPI_MAP);
		for(String serverRelCI : serverRelCISet) {
			serverRelKPISet.addAll( Arrays.asList(properties.getProperty(serverRelCI).split(",")) );
		}

		// Loading app to kpi mapping
		properties = Helper.loadProperties(EnterasConstants.ZABBIX_KPI_APP_MAP);
		appKPIMap = new HashMap<>();
		Set<String> kpiTypeSet = new HashSet<>();

		for(Entry<Object, Object> entry : properties.entrySet()){

			List<String> kpiList = Arrays.asList(((String)entry.getValue()).split(","));
			kpiTypeSet.addAll(kpiList);

			List<String> appList = Arrays.asList(((String)entry.getKey()).split(","));
			for(String app: appList) 
				appKPIMap.put(app, kpiList);

		}

		//Loading kpi to items mapping
		kpiProperties = new HashMap<>();
		for(String kpiType : kpiTypeSet){
			kpiProperties.put(kpiType, Helper.loadProperties(EnterasConstants.ZABBIX_KPI_MAPPING_PATH +  kpiType + ".properties"));
		}


	}


	public static List<JsonNode> getKPIForApp(JsonNode jsonAppNode, 
			Map<String, JsonNode> kpiJsonNodeMap, JsonNode hostJsonNode) {

		List<JsonNode> kpiNodeLt = new ArrayList<>();
		String appName = jsonAppNode.get("name").asText();
		List<String> kpiList = appKPIMap.get(appName);
		
		if(kpiList == null || kpiList.isEmpty()) {
			logger.info("application is not yet mapped to any KPI: " + appName);
			return kpiNodeLt;
		}

		for(String kpiStr : kpiList){

			if(kpiJsonNodeMap.containsKey(kpiStr)) {
				
				kpiNodeLt.add(kpiJsonNodeMap.get(kpiStr));
				
			}else{ 
				JsonNode kpiJsonNode = createKPIJsonNode(jsonAppNode, kpiStr, hostJsonNode);
				kpiNodeLt.add(kpiJsonNode);
				kpiJsonNodeMap.put(kpiStr, kpiJsonNode);
			}

		}

		return kpiNodeLt;
	}


	static JsonNode createKPIJsonNode(JsonNode appJsonNode, String kpiType, JsonNode hostJsonNode) {

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode objNode = mapper.createObjectNode();

		
		if(serverRelKPISet.contains(kpiType))
			objNode.put("assetId", hostJsonNode.get("hostid").asText());
		else
			objNode.put("assetId", appJsonNode.get("applicationid").asText());
		
		objNode.put("kpiType", kpiType);
		objNode.put("timestamp", String.valueOf(System.currentTimeMillis()));


		return objNode;

	}



	public static void populateKPI(JsonNode itemJsonNode, List<JsonNode> kpiLt) {

		String kpiType = null;

		for(JsonNode kpiJsonNode : kpiLt) {

			kpiType = kpiJsonNode.get("kpiType").asText();
			KPIMappingHelper.mapping(itemJsonNode, 
					kpiProperties.get(kpiType), 
					(ObjectNode)kpiJsonNode);

		}

	}

	public static void main(String[] args) {

		logger.info(" appKPIMap: " + appKPIMap );
		logger.info(" appKPIMap: " + kpiProperties );
		logger.info(" appKPIMap: " + serverRelKPISet );



	}

}
