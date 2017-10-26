package org.kafka.test.client;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.Properties;
import java.util.stream.Collectors;

@Startup
@ApplicationScoped
public class KafkaTestConsumer {

    private Properties properties;
    private KafkaConsumer kafkaConsumer;
    private Thread pollingThread;

    ArrayList<Subscriber> subscribers = new ArrayList<Subscriber>();

    ArrayList<String> topics;

    public void addSubscriber(Subscriber subscriber){

        ArrayList<Subscriber> newSubscribers = new ArrayList<Subscriber>();
        newSubscribers.add(subscriber);
        newSubscribers.addAll(subscribers.stream()
                .filter(e -> e.isAlive())
                .collect(Collectors.toList()));

        subscribers = newSubscribers;
    }

    public ArrayList<Subscriber> getSubscribers() {
        return subscribers;
    }

    public Properties getProperties(){
        return properties;
    }

    @PostConstruct
    public void init(){
        properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092, localhost:9093");
        properties.setProperty("kqey.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.setProperty("value.serializer","org.apache.kafka.common.serialization.StringSerializer");

        kafkaConsumer = new KafkaConsumer(properties);
        ArrayList<String> topics = new ArrayList<String>();
        topics.add("kafka_test");
    }

    @PreDestroy
    private void killPollingThread(){
        pollingThread.interrupt();
    }

    public void startPolling(){

        kafkaConsumer.subscribe(topics);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try{

                    while(true){
                        ConsumerRecords<String, String> consumerRecord = kafkaConsumer.poll(100);
                        consumerRecord.iterator().forEachRemaining(this::feedSubscriber);
                    }

                }finally {
                    kafkaConsumer.close();
                }

            }

            public void feedSubscriber(ConsumerRecord message){
                subscribers.stream().filter(e -> e.isAlive()).forEach( e -> e.accept(message.value().toString()));
            }
        };

        pollingThread = new Thread(runnable);
        pollingThread.run();

    }

    public interface Subscriber{
        void accept(String message);
        default boolean isAlive() {
            return false;
        }
    }


}
