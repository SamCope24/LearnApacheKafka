package com.github.samcope24.learnapachekafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class ProducerKeysDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Logger logger = LoggerFactory.getLogger(ProducerKeysDemo.class);
        String bootstrapServers = "localhost:9092";

        // create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create the Producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        for (int i = 0; i < 10; i++) {
            String topic = "first_topic";
            String value = "hello world" + Integer.toString(i);
            String key = "id_" + Integer.toString(i);

            // create a Producer record
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);

            logger.info("Key: " + key); // log the key
            // id_0 partition 1
            // id_1 partition 0
            // id_2 partition 2
            // id_3 partition 0
            // id_4 partition 2
            // id_5 partition 2
            // id_6 partition 0
            // id_7 partition 2
            // id_8 partition 1
            // id_9 partition 2

            // send data - asynchronous
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    // executes when record sent/exception is thrown
                    if (e == null) {
                        // the record was successfully sent
                        logger.info("Received new metadata. \n" +
                                "Topic: " + recordMetadata.topic() + "\n" +
                                "Partition: " + recordMetadata.partition() + "\n" +
                                "Offset: " + recordMetadata.offset() + "\n" +
                                "Timestamp: " + recordMetadata.timestamp());
                    } else {
                        logger.error("Error while producing", e);
                    }
                }
            }).get(); // block the .send() to make it synchronous
        }

        // flush data
        producer.flush();

        // flush and close
        producer.close();
    }
}
