package com.enteras.ci;


import java.util.Map;

import com.enteras.Helper;

public class DBServerCI extends GeneralAppCI {

	public String opPerSec;
	public String bytePerSec;
	public String version;
	public String typeDetails;
	public String clusterId;
	public int availability;
	
	public static Map<String, String> ciAttMap ;
	
	static {
		
		logger.debug("Loading class DBServerCI ...");

		String className = DBServerCI.class.getSimpleName();	
		classFieldMap.put(className, Helper.loadProperties(DBServerCI.class));
		
		logger.debug("Field property mapping");
		logger.debug("Key:" + className + " value: " + classFieldMap.get(className));
	}
	
	
	public DBServerCI() { 
		//type = "DBServerCI";
		typeDetails = "MysqlService";
		clusterId = "1";
		availability = 1;
		
	}

}
