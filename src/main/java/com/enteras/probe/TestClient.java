package com.enteras.probe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enteras.Helper;
import com.enteras.ci.DBServerCI;
import com.enteras.ci.ServerCI;
import com.enteras.ci.ServiceCI;
import com.enteras.ci.SwitchCI;



public class TestClient {

	static final Logger logger = LoggerFactory.getLogger(TestClient.class);
	
	public static void main(String[] args) {

		    try {
		    	
		    SwitchCI switch1 = 	new SwitchCI();
		    switch1.assetId = "1";
		    switch1.deviceType = "SWITCH";
		    switch1.ip = "192.168.204.152";
		    switch1.subNet = "192.168.204";
		    switch1.serviceId = "0";
		    switch1.sysContact = "SWITCH";
		    switch1.sysDescr = "HP J9726A 2920-24G Switch, revision WB.15.11.0007, ROM WB.15.05";
		    switch1.sysLocation = "Level 1";
		    switch1.sysName = "SWITCH_1";
		    switch1.sysUpTime = "60";
		    switch1.type = "SWITCHCI";
		    switch1.vendor = "vendor1";
		    logger.error( Helper.getJsonString(switch1) );
		    
		    ServerCI server1 = new ServerCI();
		    server1.assetId = "2";
		    server1.available = "0";
		    server1.cpuSwitches = "250";
		    server1.hostName = "host1";
		    server1.ip = "192.168.204.153";
		    server1.numOfProcess = "50";
		    server1.serviceId = "s1";
		    server1.subNet = "192.168.204";
		    server1.type = "0";		        
		    logger.error( Helper.getJsonString(server1) );
		    
		    
		    ServerCI server2 = new ServerCI();
		    server2.assetId = "6";
		    server2.available = "0";
		    server2.cpuSwitches = "251";
		    server2.hostName = "host2";
		    server2.ip = "192.168.204.154";
		    server2.numOfProcess = "60";
		    server2.serviceId = "s1";
		    server2.subNet = "192.168.204";
		    server2.type = "0";		        
		    logger.error( Helper.getJsonString( server2 ) );
		    
		    
		    ServiceCI service1 = new ServiceCI();
		    service1.assetId = "3";
		    service1.hostAssetId = "2";
		    service1.serviceId = "s1";
		    service1.status = "0";
		    service1.type = "SERVICECI";
		    service1.typeDetails = "HTTPS";
		    logger.error( Helper.getJsonString( service1 ) );
		    
		    ServiceCI service2 = new ServiceCI();
		    service2.assetId = "4";
		    service2.hostAssetId = "2";
		    service2.serviceId = "s1";
		    service2.status = "0";
		    service2.type = "SERVICECI";
		    service2.typeDetails = "HTTP";  
		    logger.error( Helper.getJsonString( service2 ) );
		    
		    ServiceCI service3 = new ServiceCI();
		    service3.assetId = "5";
		    service3.hostAssetId = "6";
		    service3.serviceId = "s1";
		    service3.status = "0";
		    service3.type = "SERVICECI";
		    service3.typeDetails = "FTP";  
		    logger.error( Helper.getJsonString(service3) );
		    
		    
		    DBServerCI dbServer = new DBServerCI();
		    dbServer.assetId = "7";
		    dbServer.hostAssetId = "6";
		    dbServer.serviceId = "s1";
		    dbServer.type = "SERVICECI";
		    dbServer.bytePerSec="20";
		    dbServer.opPerSec="250";
		    dbServer.version="1.0";		   
		    logger.error( Helper.getJsonString(dbServer) );	      

		        // handle response here...
		    }catch (Exception ex) {
		        logger.debug(" Execption in main:" + ex.getMessage());
		        ex.printStackTrace();
		    } finally {
		       
		    }

	}
	
	
	
	//{   		 \"jsonrpc\": \"2.0\",		    \"method\": \"user.login\",		    \"params\": {		        \"user\": \"Admin\",		        \"password\": \"zabbix\"		    },		    \"id\": 1,		    \"auth\": null		}


	//{    \"jsonrpc\": \"2.0\",    \"method\": \"item.get\",    \"params\": {        \"output\": \"extend\",        \"filter\": {            \"host\": [                \"Template OS Linux\"             ],	\"hostids\": [                \"10105\"             ]        }    },    \"auth\": \"6ba5d5426e545c1c6b65356e2d37acde\",    \"id\": 1}
	
}
