package com.enteras.ci;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.enteras.Helper;

public class GeneralAppCI extends SoftwareCI {

	static Set<String> serverAppSet = new HashSet<>();
	static Map<String, String> appMap = new HashMap<>();

	public static Map<String, String> ciAttMap ;

	static {

		logger.debug("Loading class GeneralAppCI ...");


		serverAppSet.add("CPU");
		serverAppSet.add("Filesystems");
		serverAppSet.add("General");
		serverAppSet.add("Memory");
		serverAppSet.add("Network interfaces");
		serverAppSet.add("OS");
		serverAppSet.add("Performance");
		serverAppSet.add("Processes");
		serverAppSet.add("Security");
		serverAppSet.add("Filesystems");

		appMap.put("MySQL", "com.enteras.ci.DBServerCI");

		appMap.put("FTP service", "com.enteras.ci.ServiceCI");
		appMap.put("HTTP service", "com.enteras.ci.ServiceCI");
		appMap.put("HTTPS service", "com.enteras.ci.ServiceCI");
		appMap.put("IMAP service", "com.enteras.ci.ServiceCI");
		appMap.put("LDAP service", "com.enteras.ci.ServiceCI");
		appMap.put("NNTP service", "com.enteras.ci.ServiceCI");
		appMap.put("NTP service", "com.enteras.ci.ServiceCI");
		appMap.put("POP service", "com.enteras.ci.ServiceCI");
		appMap.put("SMTP service", "com.enteras.ci.ServiceCI");
		appMap.put("SSH service", "com.enteras.ci.ServiceCI");
		appMap.put("Telnet service", "com.enteras.ci.ServiceCI");



		String className = GeneralAppCI.class.getSimpleName();		
		classFieldMap.put(className, Helper.loadProperties(GeneralAppCI.class));
		logger.debug("Field property mapping");
		logger.debug("Key:" + className + " value: " + classFieldMap.get(className));

	}


	public static AssetCI creatApp(String appName) throws Exception {

		String className = appMap.get(appName);
		if(className == null) {
			logger.error("mapping for appName is missing: " + appName);
			return null;
		}

		return (AssetCI) Class.forName(className).newInstance();

	}

	public static boolean isServerApp(String appName) {
		return serverAppSet.contains(appName);
	}




	public String hostAssetId;

	public GeneralAppCI() { 
		//type = "GeneralAppCI";

	}


	public void populateLinksFields(AssetCI[] arrObj) {

		super.populateLinksFields(arrObj);
		for(AssetCI obj : arrObj) {
			if(ServerCI.class.isInstance(obj)) {
				hostAssetId = obj.assetId;
				
				connectedTo.add(obj.assetId);
			}
		}

	}



}
