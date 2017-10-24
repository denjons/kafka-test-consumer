package org.kafka.test.client.websocket;

import javax.inject.Inject;
//import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint("/speedlayer")
public class KafkaClientWebsocket {


    @OnMessage
    public String hello(String message) {
        System.out.println("Received : " + message);
        return message;
    }

    @OnOpen
    public void myOnOpen(Session session, EndpointConfig config) {
        System.out.println("WebSocket opened: " + session.getId());
    }

    @OnClose
    public void myOnClose(CloseReason reason) {
        System.out.println("Closing a WebSocket due to " + reason.getReasonPhrase());
    }
}
