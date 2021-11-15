package com.looseboxes.websocket.server.config;

import com.looseboxes.websocket.server.controller.WebsocketEndpoints;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

/**
 * @author hp
 */
public abstract class AbstractWebSocketSecurityConfiguration 
      extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    private final WebsocketProperties websocketProperties;
    
    public AbstractWebSocketSecurityConfiguration(WebsocketProperties websocketProperties) {
        this.websocketProperties = websocketProperties;
    }
    
    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
            .simpDestMatchers(WebsocketEndpoints.ENDPOINT + "/**/*").permitAll()
            .simpDestMatchers(websocketProperties.getApplicationEndpointsForSuffix("/**/*")).permitAll()
            .simpDestMatchers(websocketProperties.getUserEndpointForSuffix("/**/*")).authenticated();
// This led to exception:
// org.springframework.messaging.MessageDeliveryException: Failed to send message to ExecutorSubscribableChannel[clientInboundChannel]; nested exception is org.springframework.security.access.AccessDeniedException: Access is denied
// org.springframework.security.access.AccessDeniedException: Access is denied
//            .anyMessage().denyAll();
    }

    /**
     * Disables CSRF for Websockets.
     */
    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}