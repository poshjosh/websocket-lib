package com.looseboxes.websocket.server.controller;

import com.looseboxes.websocket.server.WebsocketApplicationDestinationMessageInterceptor;
import com.looseboxes.websocket.server.model.WebsocketMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import reactor.util.annotation.Nullable;

/**
 * @author chinomso ikwuagwu
 */
@Controller
public class WebsocketMessageController {

    private final Logger log = LoggerFactory.getLogger(WebsocketMessageController.class);

    private final SimpMessageSendingOperations messagingTemplate;
    @Nullable private final WebsocketApplicationDestinationMessageInterceptor websocketApplicationDestinationMessageInterceptor;

    public WebsocketMessageController(SimpMessageSendingOperations messagingTemplate,
                                      @Autowired(required=false) WebsocketApplicationDestinationMessageInterceptor websocketApplicationDestinationMessageInterceptor) {
        this.messagingTemplate = messagingTemplate;
        this.websocketApplicationDestinationMessageInterceptor = websocketApplicationDestinationMessageInterceptor;
    }

    // This did not catch '/abc/def', that is why we added the next method with /{path0}/{path1}
    @MessageMapping(WebsocketEndpoints.SEND + "/{path}")
    public void sendMessage(@Payload WebsocketMessage message,
                            SimpMessageHeaderAccessor simpMessageHeaderAccessor,
                            @DestinationVariable String path) {

        path = "/" + path;

        send(message, simpMessageHeaderAccessor, path);
    }

    @MessageMapping(WebsocketEndpoints.SEND + "/{path0}/{path1}")
    public void sendMessage(@Payload WebsocketMessage message,
                            SimpMessageHeaderAccessor simpMessageHeaderAccessor,
                            @DestinationVariable String path0,
                            @DestinationVariable String path1) {

        final String path = "/" + path0 + '/' + path1;

        send(message, simpMessageHeaderAccessor, path);
    }

    private void send(WebsocketMessage message, SimpMessageHeaderAccessor simpMessageHeaderAccessor, String path) {

        if(log.isTraceEnabled()) {
            log.trace("Path: {}, headers: {}, message: {}", path, simpMessageHeaderAccessor.getSessionAttributes(), message);
        }

        if(websocketApplicationDestinationMessageInterceptor != null) {
            message = websocketApplicationDestinationMessageInterceptor.intercept(path, simpMessageHeaderAccessor, message);
        }

        messagingTemplate.convertAndSend(WebsocketEndpoints.MESSAGE_BROKER_DESTINATION_TOPIC + path, message);
    }

    @MessageMapping(WebsocketEndpoints.JOIN)
    @SendTo(WebsocketEndpoints.PUBLIC)
    public WebsocketMessage addUser(@Payload WebsocketMessage message,
                               SimpMessageHeaderAccessor headerAccessor) {

        log.debug("JOIN Message: {}", message);

        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", message.getSender());
        
        // By sending the message to a public topic, we are broadcasting the
        // user join event. This way, we don’t need to do anything in the 
        // WebSocketEventListener method handling SessionConnected event.        
        
        return message;
    }

}
