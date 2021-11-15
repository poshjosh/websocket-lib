package com.looseboxes.websocket.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

/**
 * @author hp
 */
public class WebsocketClientConfigurationSource {
    
    @Bean public WebSocketStompClient websocketStompClient() {
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        return this.configure(stompClient);
    }

    protected WebSocketStompClient configure(WebSocketStompClient stompClient) {
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        return stompClient;
    }
}
