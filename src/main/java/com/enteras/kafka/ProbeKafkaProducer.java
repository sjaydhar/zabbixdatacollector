package com.enteras.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enteras.Helper;

public class ProbeKafkaProducer {
	
	static final Logger logger = LoggerFactory.getLogger(ProbeKafkaProducer.class);

	Producer<String, String> producer ;
	String topic ;

	private static final String KAFKA_SERVERS = "bootstrap.servers";
	private static final String KAFKA_ACKS = "acks";
	private static final String KAFKA_RETRIES = "retries";
	private static final String KAFKA_BATCH_SIZE = "batch.size";
	private static final String KAFKA_LINGER_MS = "linger.ms";
	private static final String KAFKA_BUFFER_MEMORY= "buffer.memory";

	public ProbeKafkaProducer() {
		init();
	}

	private void init() {

		Properties props = new Properties();
		props.put(KAFKA_SERVERS, Helper.getMiscProperties(KAFKA_SERVERS));
		props.put(KAFKA_ACKS, Helper.getMiscProperties(KAFKA_ACKS));
		props.put(KAFKA_RETRIES, Helper.getMiscProperties(KAFKA_RETRIES));
		props.put(KAFKA_BATCH_SIZE, Helper.getMiscProperties(KAFKA_BATCH_SIZE));
		props.put(KAFKA_LINGER_MS, Helper.getMiscProperties(KAFKA_LINGER_MS));
		props.put(KAFKA_BUFFER_MEMORY, Helper.getMiscProperties(KAFKA_BUFFER_MEMORY));


		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		producer = new KafkaProducer<>(props);
		//topic = Helper.getMiscProperties(KAFKA_TOPIC);

	}


	public void sendMessage( String topic, String key, String msg) {

		producer.send( new ProducerRecord<String, String>(topic, key, msg) ,

				new Callback() {
			@Override
			public void onCompletion(RecordMetadata metadata, Exception exception) {
				if(metadata != null)
					logger.debug("Posted msg" + metadata);
				else { 
					exception.printStackTrace();
					logger.error("Posted msg:"  + exception);
				}
			}
		}
				);

	}

}
