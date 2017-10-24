package org.kafka.test.client;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class KafkaTestConsumer {

    private Properties properties;

    private KafkaConsumer kafkaConsumer;

    private Thread PoolingThread;

    private List<SocketSubscriber> subscribers;

    @PostConstruct
    public void init(){
        properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092, localhost:9093");
        properties.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.setProperty("value.serializer","org.apache.kafka.common.serialization.StringSerializer");

        kafkaConsumer = new KafkaConsumer(properties);
        ArrayList<String> topics = new ArrayList<String>();
        topics.add("kafka_test");
    }

    public void startPolling(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try{

                    while(true){
                        ConsumerRecords<String, String> consumerRecord = kafkaConsumer.poll(100);
                        subscribers.stream().filter(e -> e.isAlive()).forEach(e -> e.accept());
                    }

                }finally {
                    kafkaConsumer.close();
                }

            }
        }
        kafkaConsumer.poll();
    }


}
