package com.enteras.clientapi;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.enteras.EnterasConstants;
import com.enteras.Helper;

public enum ProtocolType {
	
	SNMP ("11"){
		
		@Override
		public Map<String, String> getDCheckMap() {
			
			Map<String, String> dcheckMap = new HashMap<String, String>();
			dcheckMap.put("type", "11");
			dcheckMap.put("key_", "SNMPv2-MIB::sysName.0");
			dcheckMap.put("snmp_community", "public");
			dcheckMap.put("ports", "161");
			
			return dcheckMap;
		}
		
	},
	
	ZABBIX_AGENT("9") {
		
		@Override
		public Map<String, String> getDCheckMap() {
			
			Map<String, String> dcheckMap = new HashMap<String, String>();
			dcheckMap.put("type", "9");
			dcheckMap.put("key_", "system.uname");
			dcheckMap.put("uniq", "0");
			dcheckMap.put("ports", "10050");
			
			
			return dcheckMap;
		}
		
	},
	;
	
	
	public List<String> templates; 
	public String serviceType;

	public abstract Map<String, String> getDCheckMap(); 
	
	
	ProtocolType(String serviceType) {
		
		this.serviceType = serviceType;
		
		Properties properties = Helper.loadProperties(EnterasConstants.ZABBIX_MISC_PROPERTIES_PATH);
		
		if( properties.containsKey(this.name() + "_Templateids")) {
			templates = Arrays.asList(properties.getProperty(this.name() + "_Templateids").split(","));
		}
		
	}
	
	
}
