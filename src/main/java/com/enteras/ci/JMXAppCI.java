package com.enteras.ci;

import com.enteras.Helper;

public class JMXAppCI extends GeneralAppCI {
	
	public String heapMeomoryUsage;
	public String openedFDCount;
	public String jvmVersion;
	public String typeDetails;
	public String clusterId;
	public int availability;
	
	
	static {
		
		logger.debug("Loading class JMXAppCI ...");

		String className = JMXAppCI.class.getSimpleName();		
		classFieldMap.put(className, Helper.loadProperties(JMXAppCI.class));
		logger.debug("Field property mapping");
		logger.debug("Key:" + className + " value: " + classFieldMap.get(className));
		
		/*logger.info("Property file JMXAppCI");
		for(String key : classFieldMap.get(className).keySet()) {
			logger.debug("Key: " + key + " value: " + classFieldMap.get(className).get(key));
		}*/
		
	}
	
	public JMXAppCI() {
		// TODO Auto-generated constructor stub
		typeDetails = "JMXService";
		clusterId = "2";
		availability = 1;
	}
	
}
