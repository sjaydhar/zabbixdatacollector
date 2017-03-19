package com.enteras.probe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enteras.Helper;
import com.enteras.clientapi.DefaultZabbixApi;
import com.enteras.clientapi.Request;
import com.enteras.clientapi.RequestBuilder;
import com.enteras.clientapi.ZabbixApi;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;


public class TestAPI {
	
	static final Logger logger = LoggerFactory.getLogger(TestAPI.class);

	static private ZabbixApi zabbixApi ;
	//My Changes


	static {

		String url = "http://10.1.40.39/zabbix/api_jsonrpc.php";

		/*zabbixApi = new DefaultZabbixApi(url);
		zabbixApi.init();
		
		boolean login = zabbixApi.login("admin", "zabbix");
		logger.error("login:" + login);*/
		
		zabbixApi = DefaultZabbixApi.getInstance(url, "admin", "zabbix");


	}

	public static void main(String[] args) {
		
		
		//logger.debug( getAllHosts() );
		
		String ip = "192.168.200.5";
		
		if(ip != null) {
			logger.debug( ip.substring(0, ip.lastIndexOf(".")) );
		}
		
		
		//logger.debug( getAllAppOnHost( new String[] {"10105"}  ) );
		//logger.debug( getCIItemForApp( new String[] {"460"}  ) );
		

		

		
		
		/*JsonObject hostObjects = getAllHosts();
		
		String[] arrHosts = getKeyValues(hostObjects, "hostid");
		for(String hostid : arrHosts) {
			String[] arrApps = getKeyValues(getAllAppOnHost(hostid), "applicationid");
			for(String appid : arrApps ) {
				logger.debug( getCIItemForApp(appid) );
			}
		}*/
		
		
	}



	/*public static JsonObject getAllZS(String zsURL, String userName, String password){

		boolean login = zabbixApi.login("admin", "zabbix");
		logger.error("login:" + login);

		Request request = RequestBuilder.newBuilder()
				.method("item.get")
				//.paramEntry("hostids", 10105)
				.paramEntry("output", "extend")
				.paramEntry("sortfield", "name")
				//.searchParamEntry("key_", new String[] { "switches", "cpu" })
				.build();

		JsonParser jsonParser = new JsonParser();			
		logger.error(jsonParser.parse(request.toString()));

		return zabbixApi.call(request);

	}*/


	/*public static List<AssetCI> getCIObjets(List<JsonObject> ltJsonObject){


		for(JsonObject jsonObject : ltJsonObject) {
			JsonArray jsonArray = jsonObject.getAsJsonArray();
			for(JsonElement jElement : jsonArray) {
				jElement.getAsJsonObject().get("hostid")

			}


		}


		return null;
	}*/



	public static JsonNode getAllHosts() {

		String methodName = "host.get";

		Request request = RequestBuilder.newBuilder()
				.method(methodName)
				.paramEntry("output", "extend")
				.paramEntry("sortfield", "name")	
				.build();

		logger.error(request.toString());

		return zabbixApi.call(request);

	}

	
	public static String[] getKeyValues(JsonNode input, String keyName) {
		
		logger.debug("response:" + input);
		
		ArrayNode jArr = Helper.getArrayNode(input.get("result"));
		
		String[] arrHosts = new String[jArr.size()];
		
		int pos = 0;
		for(JsonNode jNode : jArr) {
			arrHosts[pos++] = jNode.get(keyName).asText();
		}
		
		return arrHosts;
	}
	
	public static JsonNode getAllAppOnHost(String hostid) {

		String methodName = "application.get";

		Request request = RequestBuilder.newBuilder()
				.method(methodName)
				.paramEntry("output", "extend")
				.paramEntry("sortfield", "name")
				.paramEntry("hostids", hostid)
				.build();

		logger.error(request.toString());

		return zabbixApi.call(request);

	}
	
	

	public static JsonNode getCIItemForApp(String appid) {
		
		String methodName = "item.get";

		Request request = RequestBuilder.newBuilder()
				.method(methodName)
				.paramEntry("output", "extend")
				.paramEntry("sortfield", "name")
				.paramEntry("applicationids", appid)
				.build();

		logger.error(request.toString());

		return zabbixApi.call(request);

	}



}
