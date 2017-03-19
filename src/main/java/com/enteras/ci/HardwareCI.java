package com.enteras.ci;

import java.util.Map;

import com.enteras.Helper;
import com.fasterxml.jackson.databind.JsonNode;

public class HardwareCI extends AssetCI {
	
	public static Map<String, String> ciAttMap ;
	
	public String ip ;
	public String subNet;
	public String available;

	static {
		
		logger.debug("Loading class HardwareCI ...");
		
		String className = GeneralAppCI.class.getSimpleName();		
		classFieldMap.put(HardwareCI.class.getSimpleName(), Helper.loadProperties(HardwareCI.class));
		logger.debug("Field property mapping");
		logger.debug("Key:" + className + " value: " + classFieldMap.get(className));
	}
	
	
	public HardwareCI() { 
		
		//type = "HardwareCI";
		
	}
	
	
	public void populateField(JsonNode input)  {
		
		super.populateField(input);
		if(ip != null &&  ip.lastIndexOf(".") != -1 ) {
			subNet = ip.substring(0, ip.lastIndexOf("."));
		}
		
	}
	
	
	
}


