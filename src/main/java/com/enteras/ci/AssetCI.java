package com.enteras.ci;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enteras.Helper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;


public class AssetCI {

	static final Logger logger = LoggerFactory.getLogger(AssetCI.class);
	
	public static class Property { 

		Property(String name, String value){

			this.name = name;
			this.value = value;
			
		}

		public String name;
		public String value;

	}

	public String assetId;
	public String serviceId;
	public String timeStamp ;
	public String type ;
	public String typeDetails;
	
	public Collection<String> connectedTo = new HashSet<>();
	public List<String> kpiMetricsList = new ArrayList<String>();


	//public List<Property> ltProperties = new ArrayList<>();

	public static Map<String, Map<String, String>> classFieldMap = new HashMap<>() ;

	static {

		logger.debug("Loading class AssetCI ...");
		
		String className = AssetCI.class.getSimpleName();
		classFieldMap.put( className, Helper.loadProperties(AssetCI.class));
		
		logger.debug("Field property mapping");
		logger.debug("Key:" + className + " value: " + classFieldMap.get(className));
		
	}

	public AssetCI() {	
		init();
	}

	/*public String getJsonString() {
		return Helper.getJsonString(this);
	}*/

	protected void init() {	

		setTimeStamp();
		type = this.getClass().getSimpleName();
		
		kpiMetricsList.addAll( AssetCIs.getKPIListForAssetCI( this.getClass().getSimpleName() ) );

	}

	protected void setTimeStamp() {

		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date today = Calendar.getInstance().getTime(); 
		timeStamp = df.format(today);

	}


	public void populateFieldBasedOnItems(JsonNode input)  {

		
		if( !classFieldMap.containsKey(this.getClass().getSimpleName()) )
			return;
		
		Map<String, String> fieldpropertyMap = classFieldMap.get(this.getClass().getSimpleName());
		
		ArrayNode arrayNode = Helper.getArrayNode(input);

		for(JsonNode jsonNode :  arrayNode) { 

			if( fieldpropertyMap.containsKey(jsonNode.get("key_").asText()) ) {
					populateField( fieldpropertyMap.get(jsonNode.get("key_").asText()), 
							jsonNode.get("lastvalue").asText() );
			}

			//ltProperties.add( new Property( jsonObj.get("key_").getAsString(), jsonObj.get("lastvalue").getAsString()) );		
		}

	}

	
	

	public void populateField(JsonNode input)  {

		if(!classFieldMap.containsKey(this.getClass().getSimpleName()))
			return;
		
		Map<String, String> fieldpropertyMap = classFieldMap.get(this.getClass().getSimpleName());
		for(Entry<String, String> entry : fieldpropertyMap.entrySet()) {

			if( input.has(entry.getKey()) ) {	
				populateField( entry.getValue(), 
						input.get( entry.getKey() ).asText() );
			}

		}
		
	}


	public  void populateField( String fieldName, String value) {

		logger.debug(" ClassName: " + this.getClass() );
		try {
			
			this.getClass().getField(fieldName).set(this, value);
			
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			
			logger.warn("Exception while populating field: " + e + " field Name: " + fieldName );
			e.printStackTrace();
			
		}

	}

	public void populateLinksFields(AssetCI[] arrObj) {
		
	}



	/*protected void loadProperties() {

		List<String> superClasses = getListParentClass();
		ciAttMap.clear();

		Set<String> valuesUpdate = new HashSet<>();

		for(String className : superClasses) { 

			String propetyFileName = "/config/Zabbix/Zabbix_" + className + ".properties";
			logger.debug(" propetyFileName: " + propetyFileName);

			Properties properties = new Properties();

			try { 

				properties.load( this.getClass().getResourceAsStream(propetyFileName) );

			} catch( Exception exp ){
				logger.debug("Exception while reading resource: " + propetyFileName);
				exp.printStackTrace();
				return ;
			}

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

	}*/


	/*protected void loadProperties() {



		ltSuperClasses

			String propetyFileName = "/config/Zabbix/Zabbix_" + className + ".properties";
			Properties properties = new Properties();

			try { 

				properties.load( this.getClass().getResourceAsStream(propetyFileName) );

			} catch( Exception exp ){
				logger.debug("Exception while reading resource: " + propetyFileName);
				exp.printStackTrace();
				return ;
			}

			Set<Object> keys = properties.keySet();


			for(Object k:keys){
				String key = (String) k;
				String value = properties.getProperty(key);

				if(valuesUpdate.contains(value)) continue; 
				else valuesUpdate.add(value);

				ciAttMap.put(key, value);

			}

	}*/


	/*private List<String> getListParentClass() {

		List<String> ltSuperClasses = new ArrayList<>();

		logger.debug("This Class:" + getClass().getSimpleName());

		Class C = getClass();
		while (C != Object.class) {
			ltSuperClasses.add(C.getSimpleName());
			C = C.getSuperclass();
		}

		return ltSuperClasses;


	}*/

}
