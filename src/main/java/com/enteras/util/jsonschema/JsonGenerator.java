package com.enteras.util.jsonschema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enteras.ci.SwitchCI;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonGenerator {
	
	static final Logger logger = LoggerFactory.getLogger(JsonGenerator.class);
	
	public static void main(String[] args){
		
		SwitchCI objDef = new SwitchCI();
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = "";
		try {
			jsonString = mapper.writeValueAsString(objDef);
			logger.debug(jsonString);
		} catch (JsonProcessingException e) {
			System.err.println("Eception while converting Object to String " + e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}

}
