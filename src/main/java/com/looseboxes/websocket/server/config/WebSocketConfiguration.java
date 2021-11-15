package com.looseboxes.websocket.server.config;

import com.looseboxes.websocket.server.controller.WebsocketEndpoints;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

/**
 * @author chinomso ikwuagwu
 */
@Configuration
@EnableWebSocketMessageBroker // This enables the websocket server
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
    
    private final WebsocketProperties properties;

    public WebSocketConfiguration(WebsocketProperties properties) {
        this.properties = properties;
    }

    /**
     * Register a websocket endpoint that the clients will use to connect to our
     * websocket server.
     * 
     * Register STOMP endpoints mapping each to a specific URL and (optionally)
     * enabling and configuring SockJS fallback options.
     * 
     * @param registry 
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // We add the endpoint twice, once with sockjs and once without
        // SockJS is used to enable fallback options for browsers that don’t support websocket.
        // https://www.baeldung.com/websockets-spring
        registry.addEndpoint(WebsocketEndpoints.ENDPOINT).setAllowedOrigins(properties.getAllowedOrigins());
        registry.addEndpoint(WebsocketEndpoints.ENDPOINT).setAllowedOrigins(properties.getAllowedOrigins()).withSockJS();
    }

    /**
     * Configure a message broker that will be used to route messages from
     * one client to another.
     * 
     * The message broker broadcasts messages to all the connected clients who 
     * are subscribed to a particular topic.
     * 
     * @param registry 
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        
        // Messages whose destination starts with the specified destination prefixes 
        // should be routed to message-handling methods. Such methods are annotated 
        // with @MessageMapping
        registry.setApplicationDestinationPrefixes(properties.getApplicationDestinationPrefixes());

        registry.setUserDestinationPrefix(properties.getUserDestinationPrefix());

        // Enables a simple in-memory broker

        // Messages whose destination starts with the specified destination prefixes 
        // should be routed to the message broker. The message broker broadcasts 
        // messages to all the connected clients who are subscribed to a particular topic.
        registry.enableSimpleBroker(WebsocketEndpoints.MESSAGE_BROKER_DESTINATION_TOPIC, WebsocketEndpoints.MESSAGE_BROKER_DESTINATION_QUEUE);

        //   Use this for enabling a Full featured broker like RabbitMQ

        /*
        registry.enableStompBrokerRelay(WebsocketEndpoints.MESSAGE_BROKER_DESTINATION)
                .setRelayHost("localhost")
                .setRelayPort(61613)
                .setClientLogin("guest")
                .setClientPasscode("guest");
        */
    }
}
