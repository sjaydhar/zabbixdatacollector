package com.enteras.ci;

import java.util.List;


public class JMXServiceGroupCI extends GeneralAppCI {
	
	public String heapMeomoryUsage;
	public String openedFDCount;
	public String jvmVersion;
	public String typeDetails;
	public String clusterId;
	public int availability;
	
	
	static {
		
		logger.debug("Loading class JMXServiceGroupCI ...");

		
	}
	
	public JMXServiceGroupCI() {
		// TODO Auto-generated constructor stub
		typeDetails = "JMXServiceGroup";
		clusterId = "2";
	}
	
	public static AssetCI createCluster(List<AssetCI> list) {
		// TODO Auto-generated method stub
		
		JMXServiceGroupCI obj = new JMXServiceGroupCI();
		obj.type = "JMXServiceGroupCI";
		obj.typeDetails = "Java application Service Group";
		
		int totalAvailability = 0;
		for (AssetCI assetCI : list) {
			JMXAppCI jmxService = (JMXAppCI) assetCI;
			obj.connectedTo.add(jmxService.assetId);
			totalAvailability += jmxService.availability;
			if(obj.assetId == null ) {
				obj.assetId = jmxService.assetId;
			}else {
				obj.assetId = obj.assetId + "_" + jmxService.assetId;
			}
		}
		obj.availability = totalAvailability/list.size();
		
		return obj;
	}
	
}
