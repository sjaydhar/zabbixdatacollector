package com.enteras.probe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enteras.EnterasConstants;
import com.enteras.Helper;
import com.enteras.ci.AssetCI;
import com.enteras.ci.AssetCIs;
import com.enteras.ci.DBServiceGroupCI;
import com.enteras.ci.JMXServiceGroupCI;
import com.enteras.ci.NetworkDeviceCI;
import com.enteras.clientapi.DefaultZabbixApi;
import com.enteras.clientapi.ZabbixApi;
import com.enteras.kafka.ProbeKafkaProducer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class Probev1 implements Runnable  {

	final static Logger logger = LoggerFactory.getLogger(Probev1.class);

	static String filePath = "output/resouce.json";
	static Map<String, Thread> probeThreadMap = new HashMap<>();


	private static final AtomicInteger nextId = new AtomicInteger(1);


	ZabbixApi zabbixApi ;
	int duration ;

	ProbeKafkaProducer producer ;
	String ciTopic;

	Properties properties = Helper.loadProperties("/config/Zabbix/Zabbix_Cluster_App.properties");
	
	Map<String, String> clusterIdMap = new HashMap<String, String>();
		

	Probev1(String url, String userName, String password, int duration) {


		zabbixApi = DefaultZabbixApi.getInstance(url, userName, password);

		this.duration = duration;
		producer = new ProbeKafkaProducer();
		ciTopic = Helper.getMiscProperties(EnterasConstants.KAFKA_CI_TOPIC);

		for (String key : properties.stringPropertyNames()) {
			
			String[] assetids = properties.getProperty(key).split(",");
			for (String assetId : assetids) {
				clusterIdMap.put(assetId, key);
			}
		}

	}



	public List<AssetCI> execute() throws Exception {

		logger.debug("class field map" + AssetCI.classFieldMap);
		List<AssetCI> result = new ArrayList<>(); 


		logger.debug(" Fetching List of hosts monitored hosts");
		JsonNode hostObjects = zabbixApi.getAllHost();
		ArrayNode arrayNode = Helper.getArrayNode(hostObjects);

		for(JsonNode jsonNode : arrayNode ) {

			if(AssetCIs.isServerAsset(jsonNode))
				result.addAll( createServerRelAsset(jsonNode) );
			else if( AssetCIs.isNetworkDeviceAsset(jsonNode))
				result.add( createNetworkDeviceAsset(jsonNode) );
			else  {
				logger.warn("Host type is not yet handled");
				//Helper.jsonPrettyPrint(jsonNode);
			}

		}

		return result;

	}


	private  Collection<AssetCI> createServerRelAsset(JsonNode jsonNode){

		Map<String, AssetCI> assetCIMap = new HashMap<>();

		String hostId = jsonNode.get("hostid").asText();
		logger.debug(" Fetching Application for given host :" + hostId );
		JsonNode jsonAppNodes = zabbixApi.getAllAppsDesc(hostId);

		for( JsonNode jsonAppNode : Helper.getArrayNode(jsonAppNodes) ) {

			String appName = jsonAppNode.get("name").asText();
			String appId = jsonAppNode.get("applicationid").asText();
			//Helper.createIdForTemplate(jsonAppNode, hostId);

			AssetCI assetCI = AssetCIs.getAssetCIForApp(assetCIMap, appName);
			if( assetCI == null) continue;

			logger.debug(" Fetching items for given application :" + appName);
			JsonNode itemNodes = zabbixApi.getAllItemForApp(appId);	

			assetCI.populateField(jsonNode);
			assetCI.populateField(jsonAppNode);

			assetCI.populateFieldBasedOnItems(itemNodes);

		}

		AssetCI serverCI = AssetCIs.getServerCI(assetCIMap.values());
		for(AssetCI assetCI : assetCIMap.values()) 
			assetCI.populateLinksFields(new AssetCI[] {serverCI } );

		//Removing the duplicates
		Set<AssetCI> assetSet = new HashSet<>();
		assetSet.addAll(assetCIMap.values());

		return assetSet;

	}


	private AssetCI createNetworkDeviceAsset(JsonNode jsonNode){

		NetworkDeviceCI  networkDeviceCI = NetworkDeviceCI.createNDInstance(jsonNode);
		String hostId = jsonNode.get("hostid").asText();

		JsonNode itemNodes = zabbixApi.getAllItemForHost(hostId);

		networkDeviceCI.populateField(jsonNode);
		networkDeviceCI.populateFieldBasedOnItems(itemNodes);

		return networkDeviceCI;
	}



	public static void writeFile( List<Object> ltObj) {

		File file = loadFile();
		try {

			PrintWriter out = new PrintWriter(new FileOutputStream(file, true));

			for(Object obj : ltObj) {
				out.append(Helper.getJsonString(obj));
				out.append("\n");
			}

			out.flush();
			out.close();

		} catch (IOException e1) {

			logger.debug("Exception in write file:" + e1 );
			e1.printStackTrace();

		}

	}


	public  void postToKafka( List<AssetCI> ltObj) {

		//String DBServicemsg = "{\"assetId\":\"781\",\"serviceId\":null,\"timeStamp\":"+String.valueOf(System.currentTimeMillis())+",\"type\":\"DBServerCI\",\"typeDetails\":\"MysqlService\",\"hostAssetId\":\"10160\",\"clusterId\":\"1\",\"opPerSec\":null,\"bytePerSec\":null,\"version\":\"mysql  Ver 14.14 Distrib 5.1.73, for redhat-linux-gnu (x86_64) using readline 5.1\"}";
		
		List<AssetCI> clusterObject = loadClusterApplications(ltObj);
		
		JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
		ArrayNode arrNode = nodeFactory.arrayNode();
		
		for(AssetCI obj : ltObj) {

			//logger.debug("Message posted to kafka topic: " + Helper.getJsonString(obj));
			arrNode.add(Helper.convStrToJsonNode(Helper.getJsonString(obj)));
		}
		
		for (AssetCI eachClusteredApp : clusterObject) {
			//logger.debug("Message posted to kafka topic: " + Helper.getJsonString(eachClusteredApp));
			
			arrNode.add(Helper.convStrToJsonNode(Helper.getJsonString(eachClusteredApp)));
		}
		connectServerToSwitch(arrNode);
		
		logger.debug("Message posting to kafka without switch - \n"+Helper.getJsonString(arrNode)+"\n\n");
		
		producer.sendMessage(ciTopic, String.valueOf(System.currentTimeMillis()), Helper.getJsonString(arrNode));
	}


	private List<AssetCI> loadClusterApplications(List<AssetCI> ltObj) {
		
		Map<String, List<AssetCI>> clusterAssetMap = new HashMap<String, List<AssetCI>>();
		
		// TODO Auto-generated method stub
		for (AssetCI obj : ltObj) {
			
			if(clusterIdMap.containsKey(obj.assetId)){
				List<AssetCI> assetList = new ArrayList<AssetCI>();
				if(clusterAssetMap.containsKey(clusterIdMap.get(obj.assetId))){
					assetList = clusterAssetMap.get(clusterIdMap.get(obj.assetId));
					assetList.add(obj);
					clusterAssetMap.put(clusterIdMap.get(obj.assetId), assetList);
				}
				else{
					assetList.add(obj);
					clusterAssetMap.put(clusterIdMap.get(obj.assetId), assetList);
				}
			}
			
		}
		
		List<AssetCI> resultList = new ArrayList<AssetCI>();
		
		for(String key : clusterAssetMap.keySet()){
			
			if(key.equalsIgnoreCase("DBServiceGroupCI")){
				resultList.add(DBServiceGroupCI.createCluster(clusterAssetMap.get(key)));
			}
			if(key.equalsIgnoreCase("ClusteredJMXAppCI")){
				resultList.add(JMXServiceGroupCI.createCluster(clusterAssetMap.get(key)));
			}
			
		}
		
		return resultList;
	}

	public static void writeToFileInNeo4JFor( List<Object> ltObj) {

		File file = loadFile();
		Map<String, List<String>> resMap = new HashMap<>();
		for(Object obj : ltObj) {

			String className = obj.getClass().getSimpleName();
			List<String> ltStr = resMap.get(className) == null ? new ArrayList<String>() :  resMap.get(className);
			ltStr.add(Helper.getJsonString(obj));
			resMap.put(className, ltStr);

		}

		String response = resMap.toString();
		response = response.replace("=", ":");


		try {

			PrintWriter out = new PrintWriter(new FileOutputStream(file, true)); 

			out.append(response);

			out.flush();
			out.close();

		} catch (IOException e1) {

			logger.debug("Exception in write file:" + e1 );
			e1.printStackTrace();

		}

	}



	public static File loadFile() {

		File file =new File(filePath);
		if(file.exists()) {

			double megabytes = ( file.length()  / 1024 * 1024 );
			if( megabytes > 15 ) {

				Path source = Paths.get(filePath);
				Path target = Paths.get(filePath + "_" + nextId);

				try {
					Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
					nextId.addAndGet(1);
					file.createNewFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}else 
				return file;

		}

		try {
			file.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return file;

	}

	@Override
	public void run() {

		try {
			while(true) {

				List<AssetCI> result = execute();
				//writeToFileInNeo4JFor(result);
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
			return "CI Probe is alreading monitoring this server: " + url;

		try {
			Thread thread = new Thread(new Probev1( url, userName, password, Integer.parseInt( duration)) );
			probeThreadMap.put(url, thread);
			thread.start();
			return "CI Probe started for monitoring Zabbix server: " + url ;

		} catch(Exception exp) {
			exp.printStackTrace();
			return exp.toString();
		}


	}


	public static String stopProbe(String url) {

		if( !probeThreadMap.containsKey(url) )
			return "No CI Probe is monitoring this zabbix server: " + url;

		try {

			Thread thread = probeThreadMap.remove(url);
			thread.interrupt();
			thread.join();

			return "Stopped CI Probe from monitoring Zabbix server: " + url ;

		} catch(Exception exp) {
			exp.printStackTrace();
			return exp.toString();
		}


	}


	@SuppressWarnings("deprecation")
	static void connectServerToSwitch(ArrayNode arrNode) {
		
		logger.debug("Checking the switch connection with the servers");
		List<String> serverLt = new ArrayList<>();
		JsonNode switchCI = null;
		
		for(JsonNode jsonNode : arrNode) {
			if(jsonNode.get("type").asText().equalsIgnoreCase("ServerCI")){
				serverLt.add((jsonNode.get("assetId").asText()));
			}else if(jsonNode.get("type").asText().equalsIgnoreCase("SwitchCI")) {
				switchCI = jsonNode;
			}
		}
		
		((ObjectNode)switchCI).put("connectedTo", Helper.convObjToJsonNode(serverLt));
		
	}
	


	public static void main(String[] args) throws Exception {

		String url = "http://192.168.204.152/zabbix/api_jsonrpc.php";
		String userName = "Admin";
		String password = "zabbix";

		logger.debug("[debug] Classpath: "+System.getProperty("java.class.path"));
		logger.debug(" Libaries inlcuded:");

		ClassLoader cl = ClassLoader.getSystemClassLoader();
		URL[] urls = ((URLClassLoader)cl).getURLs();
		for(URL url1: urls){
			logger.debug(url1.getFile());
		}

		logger.debug("Input arguments:");
		//logger.debug(args[0] + "  " + args[1] + "  " + args[2] + "  ");

		logger.debug( createProbe(url, userName, password, "10") );
		Thread.sleep(5000*12);
		logger.debug( createProbe(url, userName, password, "10") );
		Thread.sleep(5000);
		logger.debug( stopProbe(url) );
		Thread.sleep(5000);
		logger.debug( stopProbe(url) );









		/*logger.error("AssetCI" +  System.identityHashCode(AssetCI.ciAttMap));
		logger.error(AssetCI.ciAttMap);

		logger.error("DBServerCI" + System.identityHashCode(DBServerCI.ciAttMap) );
		logger.error(DBServerCI.ciAttMap);

		logger.error("GeneralAppCI" + System.identityHashCode(GeneralAppCI.ciAttMap) );
		logger.error(GeneralAppCI.ciAttMap);

		logger.error("HardwareCI" + System.identityHashCode(HardwareCI.ciAttMap) );
		logger.error(HardwareCI.ciAttMap);

		logger.error("ServerCI" + System.identityHashCode(ServerCI.ciAttMap));
		logger.error(ServerCI.ciAttMap);

		logger.error("ServiceCI" + System.identityHashCode(ServiceCI.ciAttMap));
		logger.error(ServiceCI.ciAttMap);

		logger.error("SoftwareCI" + System.identityHashCode(SoftwareCI.ciAttMap));
		logger.error(SoftwareCI.ciAttMap);*/




	}



}
