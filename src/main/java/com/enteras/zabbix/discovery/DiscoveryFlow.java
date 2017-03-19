package com.enteras.zabbix.discovery;

public interface DiscoveryFlow {
	
	String hostGroupCreate = "HostGroupCreate.json";
	String templateGet = "TemplateGet.json";
	String templateCreate = "TemplateCreate.json";
	String druleCreate = "DruleCreate.json";
	String actionCreate = "ActionCreate.json";
	
	String protocolTeamplateMapFile = "Zabbix_ProtolTemplateMapping.properties";

	String createGroup(String name);
	String[] getListTemplateToAssosiate(String protocol);
	String createTemplateGroup(String tempalteName, String[] template);
	String createDiscoveryRule(String protocol);
	String assosiateActionWithDiscoveryRule(String hostGroup, String ruleId );

	
}
