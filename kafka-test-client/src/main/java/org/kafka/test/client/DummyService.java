package org.kafka.test.client;


import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DummyService {

    public void doSomething(){
        System.out.println(" ------- method invoked");
    }
}
