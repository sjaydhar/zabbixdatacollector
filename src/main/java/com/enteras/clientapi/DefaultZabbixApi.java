package com.enteras.clientapi;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enteras.EnterasConstants;
import com.enteras.Helper;
import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;;

public class DefaultZabbixApi implements ZabbixApi {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultZabbixApi.class);
	
	private CloseableHttpClient httpClient;
	private URI uri;
	private String auth;
	
	private Properties properties = new Properties();
	
	private DefaultZabbixApi(String url) {
		
		try {
			uri = new URI(url.trim());
		} catch (URISyntaxException e) {
			throw new RuntimeException("url invalid", e);
		}
		
		properties = Helper.loadProperties(EnterasConstants.ZABBIX_MISC_PROPERTIES_PATH);
		
	}

	public DefaultZabbixApi(URI uri) {
		this.uri = uri;
	}

	public DefaultZabbixApi(String url, CloseableHttpClient httpClient) {
		this(url);
		this.httpClient = httpClient;
	}

	public DefaultZabbixApi(URI uri, CloseableHttpClient httpClient) {
		this(uri);
		this.httpClient = httpClient;
	}

	@Override
	public void init() {
		if (httpClient == null) {
			httpClient = HttpClients.custom().build();
		}
	}

	@Override
	public void destroy() {
		if (httpClient != null) {
			try {
				httpClient.close();
			} catch (Exception e) {
				logger.error("close httpclient error!", e);
			}
		}
	}

	@Override
	public boolean login(String user, String password) {
		
		Request request = RequestBuilder.newBuilder().paramEntry("user", user)
				.paramEntry("password", password).method("user.login").build();
		JsonNode response = call(request);
		String auth = response.asText();
		if (auth != null && !auth.isEmpty()) {
			this.auth = auth;
			return true;
		}
		return false;
		
	}

	@Override
	public String apiVersion() {
		Request request = RequestBuilder.newBuilder().method("apiinfo.version")
				.build();
		JsonNode response = call(request);
		return response.get("result").textValue();
	}

	public boolean hostExists(String name) {
		Request request = RequestBuilder.newBuilder().method("host.exists")
				.paramEntry("name", name).build();
		JsonNode response = call(request);
		return response.get("result").asBoolean();
	}

	/*public String hostCreate(String host, String groupId) {
		JSONArray groups = new JSONArray();
		JSONObject group = new JSONObject();
		group.put("groupid", groupId);
		groups.add(group);
		Request request = RequestBuilder.newBuilder().method("host.create")
				.paramEntry("host", host).paramEntry("groups", groups).build();
		JSONObject response = call(request);
		return response.getJSONObject("result").getJSONArray("hostids")
				.getString(0);
	}
	
	public String hostgroupCreate(String name) {
		Request request = RequestBuilder.newBuilder()
				.method("hostgroup.create").paramEntry("name", name).build();
		JsonObject response = call(request);
		
		return response.getJSONObject("result").getJSONArray("groupids")
				.getString(0);
		
		return response.get("result").getAsJsonObject()
				.get("groupids").getAsJsonArray().get(0).getAsString();
		
	}*/

	@Override
	public JsonNode call(Request request) {
		
		if (request.getAuth() == null) {
			request.setAuth(auth);
		}

		try {
			
			HttpUriRequest httpRequest = org.apache.http.client.methods.RequestBuilder
					.post().setUri(uri)
					.addHeader("Content-Type", "application/json")
					.setEntity(new StringEntity(request.toString()))
					.build();
			logger.error("Request: " + request.toString());
			
			CloseableHttpResponse response = httpClient.execute(httpRequest);
			HttpEntity entity = response.getEntity();
			
			String strReponse =  EntityUtils.toString(entity);
			
			JsonNode jsonNode = Helper.convStrToJsonNode(strReponse);			
			logger.error("Response: ");
			Helper.jsonPrettyPrint(jsonNode);

			if(jsonNode.has("error")){
				logger.error("Error:" + jsonNode.get("error").get("message").asText());
				throw new RuntimeException("Error for Api call:" + jsonNode.get("error").get("message").asText() 
						+ " data:" + jsonNode.get("error").get("data").asText());
			}
			
			return jsonNode.get("result");
		
			
		} catch (IOException e) {
			throw new RuntimeException("DefaultZabbixApi call exception!", e);
		}
	}

	@Override
	public JsonNode getAllHost() {
		
		String methodName = "host.get";

		Request request = RequestBuilder.newBuilder()
				.method(methodName)
				.paramEntry("output", "extend")
				.paramEntry("sortfield", "name")	
				.build();

		JsonNode response = call(request);
		return response;	
	
	}

	@Override
	public JsonNode getAllAppsDesc(String hostId) {
		
		String methodName = "application.get";

		Request request = RequestBuilder.newBuilder()
				.method(methodName)
				.paramEntry("output", "extend")
				.paramEntry("sortfield", "name")
				.paramEntry("hostids", hostId)
				.paramEntry("sortorder", "DESC")
				.build();			

		JsonNode response = call(request);
	
		return response;	
		
	}
	
	
	@Override
	public JsonNode getAllAppsAsc(String hostId) {
		
		String methodName = "application.get";

		Request request = RequestBuilder.newBuilder()
				.method(methodName)
				.paramEntry("output", "extend")
				.paramEntry("sortfield", "name")
				.paramEntry("hostids", hostId)
				.build();			

		JsonNode response = call(request);
	
		return response;	
		
	}


	@Override
	public String call(String jsonRequest) {
		
		try {
			
			HttpUriRequest httpRequest = org.apache.http.client.methods.RequestBuilder
					.post().setUri(uri)
					.addHeader("Content-Type", "application/json")
					.setEntity(new StringEntity(setAuth(jsonRequest) ) )
					.build();
			CloseableHttpResponse response = httpClient.execute(httpRequest);
			HttpEntity entity = response.getEntity();
			
			String data = EntityUtils.toString(entity);
					
			return data;
			
		} catch (IOException e) {
			throw new RuntimeException("DefaultZabbixApi call exception!", e);
		}

	}

	
	private String setAuth(String jsonString) {
		
		Configuration configuration = Configuration.builder()
				.jsonProvider(new JacksonJsonNodeJsonProvider())
				.mappingProvider(new JacksonMappingProvider())
				.build();
		
		return JsonPath.using(configuration).parse(jsonString).set("$.auth", auth).jsonString();
		
	}

	@Override
	public String getProperty(String key) {	
		
		if(properties.containsKey(key))
			return properties.getProperty(key);
		
		throw new RuntimeException("No property found with name:" + key);
		
	}

	@Override
	public JsonNode getAllTempForHost(String hostId) {
		
		String methodName = "template.get";

		Request request = RequestBuilder.newBuilder()
				.method(methodName)
				.paramEntry("output", "extend")
				.paramEntry("sortfield", "name")
				.paramEntry("hostids", hostId)
				.build();
			
		JsonNode response = call(request);
	
		return response;	
	}

	@Override
	public JsonNode getAllItemForTemp(String tempId) {
		
		String methodName = "item.get";

		Request request = RequestBuilder.newBuilder()
				.method(methodName)
				.paramEntry("output", "extend")
				.paramEntry("sortfield", "name")
				.paramEntry("templateids", tempId)
				.build();
		
		JsonNode response = call(request);
		
		return response;
		
	}

	@Override
	public JsonNode getAllItemForHost(String hostId) {
		
		String methodName = "item.get";

		Request request = RequestBuilder.newBuilder()
				.method(methodName)
				.paramEntry("output", "extend")
				.paramEntry("sortfield", "name")
				.paramEntry("hostids", hostId)
				.build();
		
		JsonNode response = call(request);
		
		return response;
		
	}
	
	
	@Override
	public JsonNode getAllItemForApp(String appId) {
		
		String methodName = "item.get";

		Request request = RequestBuilder.newBuilder()
				.method(methodName)
				.paramEntry("output", "extend")
				.paramEntry("sortfield", "name")
				.paramEntry("applicationids", appId)
				.build();
		
		JsonNode response = call(request);
		
		return response;
		
	}
	
	
	public static ZabbixApi getInstance(String url, String userName, String password) {
		
		
		ZabbixApi zabbixApi = new DefaultZabbixApi(url);
		zabbixApi.init();

		if(!zabbixApi.login(userName, password)) {
			logger.error("failed to log into zabbix server");
			new RuntimeException("failed to log into zabbix server inputs url:" + url 
					+ "userName: " + userName
					+ "password: " + password);
			
		}
		
		return zabbixApi;
	}

	@Override
	public JsonNode getEventsForHost(String hostId, String startTime, String endTime) {
		// TODO Auto-generated method stub
		
		String methodName = "event.get";

		Request request = RequestBuilder.newBuilder()
				.method(methodName)
				.paramEntry("output", "extend")
				.paramEntry("time_from", startTime)
				.paramEntry("time_till", endTime)
				.paramEntry("sortfield", "clock")
				.paramEntry("hostids", hostId)
				.build();
		
		JsonNode response = call(request);
		
		return response;
		
	}
	
	

}
