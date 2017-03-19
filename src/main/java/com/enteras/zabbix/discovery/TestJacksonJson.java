package com.enteras.zabbix.discovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enteras.Helper;
import com.enteras.ci.SoftwareCI;

public class TestJacksonJson {
	
	static final Logger logger = LoggerFactory.getLogger(TestJacksonJson.class);

	public static void main(String[] args){
		
		/*Configuration configuration = Configuration.builder()
				.jsonProvider(new JacksonJsonNodeJsonProvider())
				.mappingProvider(new JacksonMappingProvider())
				.build();
		
		
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
		
		
		
		JsonNode updatedJson = JsonPath.using(configuration)
				.parse(originalJson).set("$.session.name", "MYSESSINID").json();
		
		ObjectNode jNode = (ObjectNode) JsonNodeFactory.instance.objectNode();
		
		ArrayNode aNode = jNode.putArray("templates");
		aNode.add( ((ObjectNode) JsonNodeFactory.instance.objectNode()).put("templateid", "10069") );
		aNode.add( ((ObjectNode) JsonNodeFactory.instance.objectNode()).put("templateid", "10069") );
		
		logger.debug(jNode);*/
		
		
		
		/*ServiceCI serviceCi = new ServiceCI();
		logger.debug( Helper.getJsonString(serviceCi) );*/
		
		
		
		SoftwareCI softwareCI = new SoftwareCI();
		logger.debug( Helper.getJsonString(softwareCI) );
		
	}
	

}
