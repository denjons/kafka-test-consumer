package org.kafka.test.client;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;


@ApplicationScoped
public class SubscriberService {

    private final int MAX_SIZE = 100;

    BlockingQueue<String> messageQueue = new ArrayBlockingQueue<String>(MAX_SIZE);
    ArrayList<Subscriber> subscribers = new ArrayList<Subscriber>();


    @PostConstruct
    void startConsumers(){
        Runnable consumer = new Runnable(){

            @Override
            public void run() {
                String msg = messageQueue.poll();
                if(msg != null){
                    for (Subscriber subscriber : subscribers) {
                        subscriber.accept(msg);
                    }
                }
            }
        };
    }

    public void addConsumer(Subscriber subscriber){
        ArrayList<Subscriber> newSubscribers = new ArrayList<Subscriber>();
        newSubscribers.add(subscriber);
        newSubscribers.addAll(subscribers.stream()
                .filter(e -> e.isAlive())
                .collect(Collectors.toList()));

        subscribers = newSubscribers;
    }


    public void produce(String msg){
        messageQueue.add(msg);
    }

    public interface Subscriber{
        void accept(String message);

        default boolean isAlive() {
            return false;
        }
    }
}
