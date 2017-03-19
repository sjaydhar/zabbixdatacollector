package com.enteras.util.jsonschema;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JacksonProgram {
	
	static final Logger logger = LoggerFactory.getLogger(JacksonProgram.class);

    public static void main(String[] args) throws IOException {
        /*ObjectMapper mapper = new ObjectMapper();
        SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
        mapper.acceptJsonFormatVisitor(NetworkDeviceCI.class, visitor);
        JsonSchema schema = visitor.finalSchema();
        logger.debug(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(schema));   
    	int min = 720;
    	int max = 799;
    	logger.debug(min+(int)(Math.random() * ((max - min) + 1))); */

    }
}
