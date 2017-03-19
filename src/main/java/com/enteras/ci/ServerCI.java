package com.enteras.ci;

import java.util.Map;

import com.enteras.Helper;

public class ServerCI extends HardwareCI {
	
	public static Map<String, String> ciAttMap ;

	public String numOfProcess ;
	public String cpuSwitches;
	public String hostName;
	public String cpuDetails;
	public String model;
	public String serialNumber;
	public String osVersion;
	public String osName;


	static {
		logger.debug("Loading class ServerCI ...");
		
		String className = ServerCI.class.getSimpleName();		
		classFieldMap.put(className, Helper.loadProperties(ServerCI.class));
		logger.debug("Field property mapping");
		logger.debug("Key:" + className + " value: " + classFieldMap.get(className));
	}
	
	
	public ServerCI() {

		
	}

}
