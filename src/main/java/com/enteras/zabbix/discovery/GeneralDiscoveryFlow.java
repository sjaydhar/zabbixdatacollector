package com.enteras.zabbix.discovery;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enteras.Helper;
import com.enteras.clientapi.DefaultZabbixApi;
import com.enteras.clientapi.ProtocolType;
import com.enteras.clientapi.RequestCreater;
import com.enteras.clientapi.ZabbixApi;
import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

public class GeneralDiscoveryFlow implements DiscoveryFlow {
	
	static final Logger logger = LoggerFactory.getLogger(GeneralDiscoveryFlow.class);


	String path = "/rest/input/zabbix";	
	ZabbixApi zabbixApi ;

	Configuration configuration = Configuration.builder()
			.jsonProvider(new JacksonJsonNodeJsonProvider())
			.mappingProvider(new JacksonMappingProvider())
			.build();

	public GeneralDiscoveryFlow(String url, String userName, String password) {

		/*zabbixApi = new DefaultZabbixApi(url);
		zabbixApi.init();
		boolean login = zabbixApi.login(userName, password);*/
		
		zabbixApi = DefaultZabbixApi.getInstance(url, userName, password);

	}


	public  String autoDiscover(String ipRange, ProtocolType protocol) {

		try {

			logger.debug("Creating a host Group");
			JsonNode response = zabbixApi.call( RequestCreater.createHostGroup(ipRange, protocol) );
			String hostGroup = response.get("groupids").get(0).asText();
			logger.debug("Host Group Created: " + hostGroup);

			logger.debug("Creating a template for host group: " + hostGroup);
			response = zabbixApi.call( RequestCreater.createTemplate(hostGroup, ipRange, protocol) );
			String templateId = response.get("templateids").get(0).asText();
			logger.debug("template Created: " + templateId);

			logger.debug("Creating a Rule for iprange: " + ipRange);
			response = zabbixApi.call( RequestCreater.createRule(ipRange, protocol) );
			String ruleId = response.get("druleids").get(0).asText();
			logger.debug("rule Created: " + ruleId);

			logger.debug("Creating a Action for rule: " + ruleId);
			response = zabbixApi.call( RequestCreater.createActionForRule( ruleId,  templateId,  hostGroup, ipRange, protocol) );
			String actionId = response.get("actionids").get(0).asText();
			logger.debug("action Created: " + actionId);

			return "action Created: " + actionId;
			
		}catch(Exception exp) {
			exp.printStackTrace();
			return "Exception: "+ exp;
		}


	}

	@SuppressWarnings("unused")
	private void loadPTmapping() {
		Helper.readFile(path + "/" + hostGroupCreate);


	}



	@Override
	public String createGroup(String name) {

		String jsonInput = Helper.readFile(path + "/" + hostGroupCreate);
		String response = zabbixApi.call(jsonInput);

		return response;
	}

	@SuppressWarnings("unused")
	@Override
	public String[] getListTemplateToAssosiate(String protocol) {

		String jsonInput = Helper.readFile(path + "/" + hostGroupCreate);
		String response = zabbixApi.call(jsonInput);


		return null;
	}

	@Override
	public String createTemplateGroup(String tempalteName, String[] template) {

		return null;
	}

	@Override
	public String createDiscoveryRule(String protocol) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String assosiateActionWithDiscoveryRule(String hostGroup, String ruleId) {
		// TODO Auto-generated method stub
		return null;
	}






	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {

		String originalJson = "{\n"
				+ "\"session\":\n"
				+ "    {\n"
				+ "        \"name\":\"JSESSIONID\",\n"
				+ "        \"value\":\"5864FD56A1F84D5B0233E641B5D63B52\"\n"
				+ "    },\n"
				+ "\"loginInfo\":\n"
				+ "    {\n"
				+ "        \"loginCount\":77,\n"
				+ "        \"previousLoginTime\":\"2014-12-02T11:11:58.561+0530\"\n"
				+ "    }\n"
				+ "}";

		//JsonPath.using(configuration);

		//JsonNode updatedJson = JsonPath.using(configuration).parse(originalJson).set("$.session.name", "MYSESSINID").json();
		//logger.debug(updatedJson.toString());



		/*GeneralDiscoveryFlow generalDiscoveryFlow = 
				new GeneralDiscoveryFlow("http://10.1.40.39/zabbix/api_jsonrpc.php", "admin",  "zabbix");

		logger.debug(generalDiscoveryFlow.autoDiscover("10.1.40.1-255", ProtocolType.SNMP ));*/

		/*GeneralDiscoveryFlow generalDiscoveryFlow = 
				new GeneralDiscoveryFlow("http://10.1.40.39/zabbix/api_jsonrpc.php", "Admin",  "zabbix");				
		logger.debug(generalDiscoveryFlow.autoDiscover("10.1.40.38-40", ProtocolType.ZABBIX_AGENT ));*/

		/*GeneralDiscoveryFlow generalDiscoveryFlow = 
				new GeneralDiscoveryFlow("http://192.168.204.152/zabbix/api_jsonrpc.php", "Admin",  "zabbix");				
		logger.debug(generalDiscoveryFlow.autoDiscover("192.168.200.1-255", ProtocolType.SNMP ));*/


		/*GeneralDiscoveryFlow generalDiscoveryFlow = 
				new GeneralDiscoveryFlow("http://192.168.204.152/zabbix/api_jsonrpc.php", "Admin",  "zabbix");				
		logger.debug(generalDiscoveryFlow.autoDiscover("192.168.201.1-255", ProtocolType.SNMP ));*/

		GeneralDiscoveryFlow generalDiscoveryFlow = 
				new GeneralDiscoveryFlow("http://192.168.204.152/zabbix/api_jsonrpc.php", "Admin",  "zabbix");				
		logger.debug(generalDiscoveryFlow.autoDiscover("192.168.201.1-10", ProtocolType.SNMP ));

		/*GeneralDiscoveryFlow generalDiscoveryFlow = 
				new GeneralDiscoveryFlow("http://192.168.204.152/zabbix/api_jsonrpc.php", "Admin",  "zabbix");				
		logger.debug(generalDiscoveryFlow.autoDiscover("192.168.250.1-255", ProtocolType.SNMP ));*/


		/*GeneralDiscoveryFlow generalDiscoveryFlow = 
				new GeneralDiscoveryFlow("http://10.1.40.39/zabbix/api_jsonrpc.php", "Admin",  "zabbix");				
		logger.debug(generalDiscoveryFlow.autoDiscover("10.1.40.39", ProtocolType.ZABBIX_AGENT ));*/



		/*logger.debug( ProtocolType.SNMP.templates );
		logger.debug( ProtocolType.ZABBIX_AGENT.templates );*/



	}

}
