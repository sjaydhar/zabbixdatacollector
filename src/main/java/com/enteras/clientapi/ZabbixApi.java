package com.enteras.clientapi;

import com.fasterxml.jackson.databind.JsonNode;
//My Changes

public interface ZabbixApi {

	void init();

	void destroy();

	String apiVersion();

	//My Changes
	JsonNode call(Request request);
	
	String call(String jsonRequest);

	boolean login(String user, String password);
	
	JsonNode getAllHost() ;
	
	JsonNode getAllAppsDesc(String hostId);
	
	JsonNode getAllAppsAsc(String hostId);

	JsonNode getAllTempForHost(String hostId);

	JsonNode getAllItemForTemp(String tempId);

	JsonNode getAllItemForHost(String hostId);
	
	JsonNode getAllItemForApp(String appId);
	
	JsonNode getEventsForHost(String hostId, String startTime, String endTime);

	String getProperty(String key);
	
}
