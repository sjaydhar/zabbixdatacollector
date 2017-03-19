package com.enteras.ci;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enteras.EnterasConstants;
import com.enteras.Helper;
import com.fasterxml.jackson.databind.JsonNode;

public class AssetCIs {

	static final Logger logger = LoggerFactory.getLogger(AssetCIs.class);

	//Cannot instantiate class
	AssetCIs() { }

	//static Map<String, String> tempAssetMap = new HashMap<>();
	static Map<Set<String>, String> appAssetMap = new HashMap<>();
	static Map<String, Collection<String>> kpiAssetMap = new HashMap<>();


	static {

		//Loading App to Asset Mapping 
		/*Properties properties = Helper.loadProperties(EnterasConstants.ZABBIX_CI_TEMP_MAP);
		for(String key : properties.stringPropertyNames())  
			for(String app : properties.getProperty(key).split(",")) 
				tempAssetMap.put(app.trim(), key);
		logger.debug("tempAssetMap loaded value:" + tempAssetMap);*/


		//Loading App to Asset Mapping 
		Properties properties = Helper.loadProperties(EnterasConstants.ZABBIX_CI_APP_MAP);
		for(String key : properties.stringPropertyNames()) {
				Set<String> appSet = new HashSet<>();
				appSet.addAll(Arrays.asList( key.split(","))); 
				appAssetMap.put(appSet, properties.getProperty(key));
		}
		logger.debug("appAssetMap loaded value:" + appAssetMap);
		
		kpiAssetMap = new HashMap<>();
		properties = Helper.loadProperties(EnterasConstants.ZABBIX_CI_KPI_MAP);
		for(Entry<Object, Object> entry : properties.entrySet()){
			kpiAssetMap.put(entry.getKey().toString(),
					Arrays.asList(entry.getValue().toString().split(",")));
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public static Collection<String> getKPIListForAssetCI(String assetCI) {
		
		if(kpiAssetMap.containsKey(assetCI))
			return kpiAssetMap.get(assetCI);
		return Collections.EMPTY_LIST;
		
	}


	public static AssetCI createAssetCIBasedOnProtocol(JsonNode jsonNode) {

		if(jsonNode.get("available").asInt() == 1)
			return new ServerCI();
		else if(jsonNode.get("snmp_available").asInt() == 1 )
			return new NetworkDeviceCI();
		else { 
			logger.error("Device type not yet handled");
			return null;
		}

	}

	public static boolean isServerAsset(JsonNode jsonNode) {

		if(jsonNode.get("available").asInt() == 1)
			return true;
		return false;

	}

	public static boolean isNetworkDeviceAsset(JsonNode jsonNode) {

		if(jsonNode.get("snmp_available").asInt() == 1)
			return true;
		return false;

	}





	/*public static AssetCI getAssetCIForTemp(Map<String, AssetCI> assetCIMap, String templateName) {

		logger.debug("Checking Asset for Template : " + templateName);

		//Not Handled cases
		if(!tempAssetMap.containsKey(templateName)) {
			logger.warn("Applciation type not yet Handled");
			return null;
		}

		AssetCI assetCI = assetCIMap.get(templateName);
		if(assetCI != null) return assetCI;


		String className = tempAssetMap.get(templateName);		
		//Creating New Asset
		try {

			assetCI = (AssetCI) Class.forName(className).newInstance();
			assetCIMap.put(templateName, assetCI);
			logger.debug("Creating Asset for template : " + templateName);

		}catch(Exception exp) {
			exp.printStackTrace();
			logger.error("Exception while creating Object Instance:" + exp);
		}

		return assetCI;

	}*/


	public static AssetCI getAssetCIForApp(Map<String, AssetCI> assetCIMap, String appName) {

		Set<String> appSet = null;
		logger.debug("Checking Asset for Application : " + appName);
		for(Set<String> tempAppSet : appAssetMap.keySet()){
			if(tempAppSet.contains(appName))
				appSet = tempAppSet;
		}
		
		//Not Handled cases
		if(appSet == null) {
			logger.warn("Applciation type not yet Handled");
			return null;
		}

		AssetCI assetCI = assetCIMap.get(appName);
		if(assetCI != null) return assetCI;


		String className = appAssetMap.get(appSet);		
		//Creating New Asset
		try {

			assetCI = (AssetCI) Class.forName(className).newInstance();
			for(String tempAppName : appSet)
				assetCIMap.put(tempAppName, assetCI);
			logger.debug("Creating Asset for app : " + appName);

		}catch(Exception exp) {
			exp.printStackTrace();
			logger.error("Exception while creating Object Instance:" + exp);
		}

		return assetCI;

	}



	public static AssetCI getServerCI(Collection<AssetCI> ltAssetCI) {

		for(AssetCI assetCI : ltAssetCI){
			if(assetCI.getClass() == ServerCI.class)
				return assetCI;
		}

		return null;


	}



}
