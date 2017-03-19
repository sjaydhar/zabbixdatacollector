package com.enteras.ci;

import java.util.Map;

import com.enteras.Helper;

public class SoftwareCI extends AssetCI {
	
	public static Map<String, String> ciAttMap ;
	
	static {
		logger.debug("Loading class SoftwareCI ...");

		String className = SoftwareCI.class.getSimpleName();		
		classFieldMap.put(className, Helper.loadProperties(SoftwareCI.class));
		logger.debug("Field property mapping");
		logger.debug("Key:" + className + " value: " + classFieldMap.get(className));
	}
	
	public SoftwareCI() { 
		//type = "SoftwareCI";

	}

}
