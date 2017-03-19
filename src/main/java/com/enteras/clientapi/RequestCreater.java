package com.enteras.clientapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RequestCreater {
	
	static final Logger logger = LoggerFactory.getLogger(RequestCreater.class);

	private RequestCreater() { }

	public static Request getAllHost() {

		Request request = RequestBuilder.newBuilder()
				.method("host.get")
				.paramEntry("output", "extend")
				.paramEntry("sortfield", "name")	
				.build();

		return request;
	}

	public static Request getAllApps(String hostId) {

		Request request = RequestBuilder.newBuilder()
				.method("application.get")
				.paramEntry("output", "extend")
				.paramEntry("sortfield", "name")
				.paramEntry("hostids", hostId)
				.build();

		return request;
	}

	public static Request getAllItem(String appId) {

		Request request = RequestBuilder.newBuilder()
				.method("item.get")
				.paramEntry("output", "extend")
				.paramEntry("sortfield", "name")
				.paramEntry("applicationids", appId)
				.build();

		return request;
	}


	public static Request createHostGroup(String ipRange, ProtocolType protocol) {

		Request request = RequestBuilder.newBuilder()
				.method("hostgroup.create")
				.paramEntry("output", "extend")
				.paramEntry("name", "Host Group_" + ipRange + "_" + protocol)
				.build();

		return request;
	}

	public static Request createTemplate(String hostGroup,  String ipRange, ProtocolType protocol) {



		List<Object> tempIdsLt = new ArrayList<>();
		for(String tempId : protocol.templates) {
			Map<String, String> tempIdsMap = new HashMap<>();
			tempIdsMap.put("templateid",  tempId);
			tempIdsLt.add(tempIdsMap);
		}

		Map<String, String> groupIdsMap = new HashMap<String, String>();
			groupIdsMap.put("groupid", hostGroup);
		

		Request request = RequestBuilder.newBuilder()
				.method("template.create")
				.paramEntry("host", "template_" + ipRange + "_" + protocol)
				.paramEntry("output", "extend")
				.paramEntry("groups", groupIdsMap)
				.paramEntry("templates", tempIdsLt)
				.build();


		return request;
	}



	public static Request createRule(String ipRange, ProtocolType protocol) {
		
		List<Object> ruleList = new ArrayList<>();
		ruleList.add(protocol.getDCheckMap());
		

		Request request = RequestBuilder.newBuilder()
				.method("drule.create")
				.paramEntry("name", "discovery rule_" + ipRange + "_" + protocol)
				.paramEntry("iprange", ipRange)
				.paramEntry("dchecks", ruleList)
				.build();


		return request;
	}



	/**
	 * 
	 * @param ruleId
	 * @param templateId
	 * @param groupId
	 * @return
	 * 
	 * {
  "jsonrpc": "2.0",
  "method": "action.create",
  "params": {
    "name": "My SNMP Discovery Action1",
    "eventsource": 1,
    "status": 0,
    "esc_period": 0,
    "filter": {
      "evaltype": 0,
      "conditions": [
        {
          "conditiontype": 10,
          "value": "0"
        },
        {
          "conditiontype": 18,
          "value": "6"
        },
        {
          "conditiontype": 8,
          "value": "11"
        }
      ]
    },
    "operations": [
      {
        "optemplate": [
          {
            "templateid": "10118"
          }
        ],
        "operationtype": 6
      },
      {
        "opgroup": [
          {
            "groupid": "10"
          }
        ],
        "operationtype": 4
      }
    ]
  },
  "auth": "8557d8eca900e7567406dccb9fd7b663",
  "id": 1
}
	 * 
	 */



	public static Request createActionForRule(String ruleId, String templateId, String groupId, String ipRange, ProtocolType protocol) {


		List<Object> condsLt = new ArrayList<Object>();

		Map<String, String> condMap_0 = new HashMap<String, String>();
		condMap_0.put("conditiontype", "10");
		condMap_0.put("value", "0");

		Map<String, String> condMap_1 = new HashMap<String, String>();
		condMap_1.put("conditiontype", "18");
		condMap_1.put("value", ruleId);


		Map<String, String> condMap_2 = new HashMap<String, String>();
		condMap_2.put("conditiontype", "8");
		condMap_2.put("value", protocol.serviceType);

		condsLt.add(condMap_0);
		condsLt.add(condMap_1);
		condsLt.add(condMap_2);


		List<Object> lt_0 = new ArrayList<>();
		Map<String, String> opTempMap = new HashMap<>();
		opTempMap.put("templateid", templateId);
		lt_0.add(opTempMap);

		Map<String, Object> opMap_0 = new HashMap<>();
		opMap_0.put("optemplate", lt_0);
		opMap_0.put("operationtype", 6);

		List<Object> lt_1 = new ArrayList<>();
		Map<String, String> opGroupMap = new HashMap<>();
		opGroupMap.put("groupid", groupId);
		lt_1.add(opGroupMap);

		Map<String, Object> opMap_1 = new HashMap<>();
		opMap_1.put("opgroup", lt_1);
		opMap_1.put("operationtype", 4);

		List<Object> optLt = new ArrayList<>();
		optLt.add(opMap_1);
		optLt.add(opMap_0);


		Map<String, Object> filterMap = new HashMap<>();
		filterMap.put("conditions", condsLt);
		filterMap.put("evaltype", "0");


		Request request = RequestBuilder.newBuilder()
				.method("action.create")
				.paramEntry("name", "Action_" + ipRange + "_" + protocol)
				.paramEntry("eventsource", "1")
				.paramEntry("status", "0")
				.paramEntry("esc_period", "0")
				.paramEntry("filter", filterMap)
				.paramEntry("operations", optLt)
				.build();


		return request;
	}


	public static void main(String[] args){


		//logger.debug(createActionForRule("6", "10108", "10"));
		
		
		//logger.debug(createSNMPDRule("0.1.40.1-254"));

		logger.debug(	 createTemplate( "10", "1-120", ProtocolType.SNMP ).toString());

	}



}
