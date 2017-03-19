package com.enteras.ci;

import com.enteras.Helper;

public class SwitchCI extends NetworkDeviceCI {
	
	static {
		logger.debug("Loading class SwitchCI ...");
		
		String className = SwitchCI.class.getSimpleName();		
		classFieldMap.put(className, Helper.loadProperties(SwitchCI.class));
		logger.debug("Field property mapping");
		logger.debug("Key:" + className + " value: " + classFieldMap.get(className));
	}
	
}
