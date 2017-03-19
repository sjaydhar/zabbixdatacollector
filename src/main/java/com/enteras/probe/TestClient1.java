package com.enteras.probe;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TestClient1 {

	static final Logger logger = LoggerFactory.getLogger(TestClient1.class);
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		
		 HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead 

		    try {
		    	
		        HttpPost request = new HttpPost("http://10.1.40.39/zabbix/api_jsonrpc.php");
		        
		        ObjectMapper mapper = new ObjectMapper();
		        ObjectNode requestObj = mapper.createObjectNode();
		        
		        requestObj.put("jsonrpc", "2.0");
		        requestObj.put("method", "user.login");
		        
		        requestObj.put("id", 1);
		        requestObj.set("auth", null);

		        
		        ObjectNode params = mapper.createObjectNode();
		        params.put("user", "Admin");
		        params.put("password", "zabbix");
		        
		        requestObj.set("params", params);

		        request.addHeader("content-type", "application/json-rpc");
		        request.addHeader("jsonrpc", "2.0");
		        
		        request.setEntity(new StringEntity(requestObj.toString()));
		        		      
		        
		        HttpResponse response = httpClient.execute(request);
		        
		       
		        BufferedReader rd = new BufferedReader(
		    	        new InputStreamReader(response.getEntity().getContent()));
		        
		        StringBuffer result = new StringBuffer();
		    	String line = "";
		    	while ((line = rd.readLine()) != null) {
		    		result.append(line);
		    	}
		        
		       // Gson g = new Gson();

		        logger.debug( "result:" + result );
		        
		       /* JsonObject jsonObject = new JsonParser().parse(response.getEntity().getContent().toString()).getAsJsonObject(); 
		        logger.debug(" id:" + jsonObject.get("result").getAsString());*/
		        

		        // handle response here...
		    }catch (Exception ex) {
		        logger.debug(" Execption in main:" + ex.getMessage());
		        ex.printStackTrace();
		    } finally {
		        httpClient.getConnectionManager().shutdown(); //Deprecated
		    }

	}
	
	
	
	//{   		 \"jsonrpc\": \"2.0\",		    \"method\": \"user.login\",		    \"params\": {		        \"user\": \"Admin\",		        \"password\": \"zabbix\"		    },		    \"id\": 1,		    \"auth\": null		}


	//{    \"jsonrpc\": \"2.0\",    \"method\": \"item.get\",    \"params\": {        \"output\": \"extend\",        \"filter\": {            \"host\": [                \"Template OS Linux\"             ],	\"hostids\": [                \"10105\"             ]        }    },    \"auth\": \"6ba5d5426e545c1c6b65356e2d37acde\",    \"id\": 1}
	
}
