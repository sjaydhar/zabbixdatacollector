package com.enteras.probe;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*import java.text.DateFormat;
import java.text.SimpleDateFormat;*/




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enteras.EnterasConstants;
import com.enteras.Helper;
//import com.enteras.ci.AssetCI;
import com.enteras.ci.AssetCIs;
import com.enteras.clientapi.DefaultZabbixApi;
import com.enteras.clientapi.ZabbixApi;
import com.enteras.kafka.ProbeKafkaProducer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class KPICollectorProbe implements Runnable {

	final static Logger logger = LoggerFactory.getLogger(KPICollectorProbe.class);
	static Map<String, Thread> probeThreadMap = new HashMap<>();
	static LinkedList<List<JsonNode>> resKPIList = new LinkedList<List<JsonNode>>();
	
	ZabbixApi zabbixApi = null;

	static ProbeKafkaProducer producer ;
	static String kpiTopic;
	static String duration;

	public KPICollectorProbe(String url, String userName, String password, String interval) {
		// TODO Auto-generated constructor stub

		producer = new ProbeKafkaProducer();
		zabbixApi = DefaultZabbixApi.getInstance(url, userName, password);
		
		KPICollectorProbe.duration = interval;
		kpiTopic = Helper.getMiscProperties(EnterasConstants.KAFKA_KPI_TOPIC);

	}

	public static String createKpiProbe(String url, String userName, String password, String duration) {

		if( probeThreadMap.containsKey(url) )
			return "KPI Probe is alreading monitoring this server: " + url;

		try {
			Thread thread = new Thread(new KPICollectorProbe(url, userName, password, duration));
			probeThreadMap.put(url, thread);
			thread.start();
			return "KPI Probe started for monitoring Zabbix server: " + url ;

		} catch(Exception exp) {
			exp.printStackTrace();
			return exp.toString();
		}

	}

	public static String stopKPIProbe(String url) {

		if( !probeThreadMap.containsKey(url) )
			return "No KPI Probe is monitoring this zabbix server: " + url;

		try {

			Thread thread = probeThreadMap.remove(url);
			thread.interrupt();
			thread.join();

			return "Stopped KPI probe from monitoring Zabbix server: " + url ;

		} catch(Exception exp) {
			exp.printStackTrace();
			return exp.toString();
		}


	}


	public void run() {
		// TODO Auto-generated method stub

		try {
			while(true) {
				
				getMetricsData_New();
				Thread.sleep(Integer.parseInt(duration)*1000);
			}	
		}catch(InterruptedException ie ){
			logger.error(" exception : " + ie);
		}catch(Exception exp) {
			logger.error(" exception : " + exp);
			exp.printStackTrace();
			//writeFile( Arrays.asList( (Object)exp ) );
		}


	}


	public void getMetricsData_New() {
		
		ArrayNode arrayNode = Helper.getArrayNode(zabbixApi.getAllHost());

		List<JsonNode> result = new ArrayList<JsonNode>();

		for (JsonNode hostNode : arrayNode) {

			if(AssetCIs.isServerAsset(hostNode))
				result.addAll( createServerRelKPI(hostNode) );
			else if( AssetCIs.isNetworkDeviceAsset(hostNode))
				result.addAll( createNetwokrDeviceKPI(hostNode) );
			else  {
				logger.warn("Host type is not yet handled");
			}

			postKPIToKafka(result);
			logger.info("List of kpi genereared: " + result);

		}

	}


	private  Collection<JsonNode> createServerRelKPI(JsonNode hostNode) {

		Map<String, JsonNode> kpiMap = new HashMap<>();
		String hostId = hostNode.get("hostid").asText();
		
		logger.debug(" Fetching Application for given host :" + hostId );
		JsonNode jsonAppNodes = zabbixApi.getAllAppsAsc(hostId);

		for( JsonNode jsonAppNode : Helper.getArrayNode(jsonAppNodes) ) {

			String appName = jsonAppNode.get("name").asText();
			String appId = jsonAppNode.get("applicationid").asText();
			//Helper.createIdForTemplate(jsonAppNode, hostId);

			List<JsonNode> kpisLt = KPICollectorHelper.getKPIForApp(jsonAppNode, kpiMap, hostNode);
			if( kpisLt == null || kpisLt.isEmpty() ) continue;

			logger.debug(" Fetching items for given application :" + appName);
			JsonNode itemNodes = zabbixApi.getAllItemForApp(appId);	

			KPICollectorHelper.populateKPI(itemNodes, kpisLt);

		}

		
		//Removing the duplicates
		Set<JsonNode> kpiSet = new HashSet<>();
		kpiSet.addAll(kpiMap.values());
		
		return kpiSet;
	}

	
	@SuppressWarnings("unchecked")
	private static Collection<JsonNode> createNetwokrDeviceKPI(JsonNode hostNode) {
		
		logger.info("Not yet implemented");
		
		return Collections.EMPTY_LIST;
	}



	private static void postKPIToKafka(List<JsonNode> resultNodeList) {
		// TODO Auto-generated method stub


		for (JsonNode jsonNode : resultNodeList) {
			logger.debug("Message posted to kafka topic: " + Helper.getJsonString(jsonNode));
			producer.sendMessage(kpiTopic,jsonNode.get("timestamp").asText(), Helper.getJsonString(jsonNode));

		}

	}

	public static void main(String[] args) throws Exception {
		
		createKpiProbe("http://192.168.204.152/zabbix/api_jsonrpc.php", "Admin", "zabbix", "2");
		Thread.sleep(10000);
		
		//stopKPIProbe("http://192.168.204.152/zabbix/api_jsonrpc.php");
		
	}

}
