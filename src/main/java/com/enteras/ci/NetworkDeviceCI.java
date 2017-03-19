package com.enteras.ci;



import com.enteras.Helper;
import com.fasterxml.jackson.databind.JsonNode;

public class NetworkDeviceCI extends HardwareCI {
	
	public String deviceType;
	public String vendor;
	
	public String sysContact;
	public String sysDescr;
	public String sysLocation;
	public String sysName;
	public String sysUpTime;
	
	static {
		
		logger.debug("Loading class NetworkDeviceCI ...");

		String className = NetworkDeviceCI.class.getSimpleName();		
		classFieldMap.put(className, Helper.loadProperties(NetworkDeviceCI.class));
		
		logger.debug("Field property mapping");
		logger.debug("Key:" + className + " value: " + classFieldMap.get(className));
	}
	
	public NetworkDeviceCI(){

	}
	
	public static NetworkDeviceCI createNDInstance(JsonNode jsonNode) {
		return new SwitchCI();
	}

}
