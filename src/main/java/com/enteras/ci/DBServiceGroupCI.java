package com.enteras.ci;

import java.util.List;
import java.util.Map;


public class DBServiceGroupCI extends GeneralAppCI {

	public String opPerSec;
	public String bytePerSec;
	public String version;
	public String typeDetails;
	public String clusterId;
	public int availability;
	
	public static Map<String, String> ciAttMap ;
	
	static {
		
		logger.debug("Loading class DBServiceGroupCI ...");

		/*String className = DBServiceGroupCI.class.getSimpleName();	
		classFieldMap.put(className, Helper.loadProperties(DBServerCI.class));
		
		logger.debug("Field property mapping");
		logger.debug("Key:" + className + " value: " + classFieldMap.get(className));*/
	}
	
	
	public DBServiceGroupCI() { 
		
		typeDetails = "MysqlService";
		clusterId = "1";
	}


	public static AssetCI createCluster(List<AssetCI> list) {
		// TODO Auto-generated method stub
		
		DBServiceGroupCI obj = new DBServiceGroupCI();
		obj.type = "DBServiceGroup";
		obj.typeDetails = "MySQL DB Service Group";
		
		int totalAvailability = 0;
		for (AssetCI assetCI : list) {
			DBServerCI dbService = (DBServerCI) assetCI;
			obj.connectedTo.add(dbService.assetId);
			totalAvailability += dbService.availability;
			if(obj.assetId == null ) {
				obj.assetId = dbService.assetId;
			}else {
				obj.assetId = obj.assetId + "_" + dbService.assetId;
			}
		}
		obj.availability = totalAvailability/list.size();
		
		return obj;
	}

}
