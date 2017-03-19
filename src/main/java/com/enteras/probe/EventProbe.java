package com.enteras.probe;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enteras.EnterasConstants;
import com.enteras.Helper;
/*import com.enteras.ci.AssetCI;
import com.enteras.ci.AssetCIs;
import com.enteras.ci.DBServiceGroupCI;
import com.enteras.ci.JMXServiceGroupCI;
import com.enteras.ci.NetworkDeviceCI;*/
import com.enteras.clientapi.DefaultZabbixApi;
import com.enteras.clientapi.ZabbixApi;
import com.enteras.kafka.ProbeKafkaProducer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class EventProbe implements Runnable  {

	final static Logger logger = LoggerFactory.getLogger(EventProbe.class);

	static Map<String, Thread> probeThreadMap = new HashMap<>();
	static String startTime = "";

	ZabbixApi zabbixApi ;
	int duration ;

	ProbeKafkaProducer producer ;
	String eventTopic;
		

	EventProbe(String url, String userName, String password, int duration) {

		zabbixApi = DefaultZabbixApi.getInstance(url, userName, password);
		
		startTime = Helper.getCurrentDate();
		
		this.duration = duration;
		producer = new ProbeKafkaProducer();
		eventTopic = Helper.getMiscProperties(EnterasConstants.KAFKA_EVENT_TOPIC);

	}
	


	public ArrayNode execute() throws Exception {
		
		JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
		ArrayNode responseNode = nodeFactory.arrayNode();
		String[] serviceGroupArray = {"DBServiceGroup,DBService","WebAppService,WebAppService","JMXServiceGroup,JMXService","HTTPService,HTTPService"};
		Random rand = new Random();
		
		logger.debug(" Fetching List of hosts monitored hosts");
		JsonNode hostObjects = zabbixApi.getAllHost();
		ArrayNode arrayNode = Helper.getArrayNode(hostObjects);
		
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String endTime = String.valueOf(System.currentTimeMillis()/1000);

		for(JsonNode jsonNode : arrayNode ) {

			String hostId = jsonNode.get("hostid").asText();
			String hostname = jsonNode.get("host").asText();
			ArrayNode eventNode = Helper.getArrayNode(zabbixApi.getEventsForHost(hostId, startTime, endTime));
			logger.debug(" Fetching List of events on monitored hosts");
			for (JsonNode eachEvent : eventNode) {
				
				int index = rand.nextInt(serviceGroupArray.length);
				String serviceName = serviceGroupArray[index].split(",")[1];
				String nanoSeconds = eachEvent.get("clock").asText();
				String priority = eachEvent.get("object").asText().equalsIgnoreCase("0")?"High":"Low";
				String serviceGroup = serviceGroupArray[index].split(",")[0];
				String relatedCI = hostname;
				
				String dateFormat = df.format(new Date(Long.parseLong(nanoSeconds)*1000));
				ObjectNode objNode = Helper.createObjectNode();
				objNode.put("serviceName", serviceName);
				objNode.put("priority", priority);
				objNode.put("serviceGroup", serviceGroup);
				objNode.put("relatedCI", relatedCI);
				objNode.put("timestamp", dateFormat);
				objNode.put("type", "Event");
				objNode.put("assetId", hostId);
				responseNode.add(objNode);
			}

		}
		startTime = endTime;
		return responseNode;

	}




	public  void postToKafka( ArrayNode result) {
		
		int count = 0;
		for (JsonNode jsonNode : result) {
			logger.debug(("Events posting to kafka - \n"+Helper.getJsonString(jsonNode)+"\n\n"));
			count++;
			producer.sendMessage(eventTopic, String.valueOf(System.currentTimeMillis()), Helper.getJsonString(jsonNode));
		}
		logger.debug("Number of messages are "+count);
	}



	@Override
	public void run() {

		try {
			while(true) {

				ArrayNode result = execute();
				postToKafka(result);
				Thread.sleep(duration * 1000);
			}	
		}catch(InterruptedException ie ){
			logger.error(" exception : " + ie);
		}catch(Exception exp) {
			logger.error(" exception : " + exp);
			exp.printStackTrace();
			//writeFile( Arrays.asList( (Object)exp ) );
		}


	}


	public static String createProbe(String url, String userName, String password, String duration) {

		if( probeThreadMap.containsKey(url) )
			return "Event Probe is already monitoring this server: " + url;

		try {
			Thread thread = new Thread(new EventProbe( url, userName, password, Integer.parseInt(duration)) );
			probeThreadMap.put(url, thread);
			thread.start();
			return "Event Probe started for monitoring Zabbix server: " + url ;

		} catch(Exception exp) {
			exp.printStackTrace();
			return exp.toString();
		}


	}


	public static String stopProbe(String url) {

		if( !probeThreadMap.containsKey(url) )
			return "No Event Probe is monitoring this zabbix server: " + url;

		try {

			Thread thread = probeThreadMap.remove(url);
			thread.interrupt();
			thread.join();

			return "Stopped Event Probe from monitoring Zabbix server: " + url ;

		} catch(Exception exp) {
			exp.printStackTrace();
			return exp.toString();
		}


	}



	public static void main(String[] args) throws Exception {

		String url = "http://192.168.204.152/zabbix/api_jsonrpc.php";
		String userName = "Admin";
		String password = "zabbix";

		logger.debug( createProbe(url, userName, password, "600") );
		Thread.sleep(5000*12);
		logger.debug( createProbe(url, userName, password, "600") );
		Thread.sleep(5000);
		logger.debug( stopProbe("abc") );
		Thread.sleep(5000);
		logger.debug( stopProbe(url) );
		Thread.sleep(5000);
		logger.debug( stopProbe(url) );

	}

}
