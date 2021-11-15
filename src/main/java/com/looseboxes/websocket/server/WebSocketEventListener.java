package com.looseboxes.websocket.server;

import com.looseboxes.websocket.server.controller.WebsocketEndpoints;
import com.looseboxes.websocket.server.model.WebsocketMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * @author chinomso ikwuagwu
 */
@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired private SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.debug("Received a new web socket connection");
        // We’re already broadcasting user join event in the addUser() method 
        // defined inside ChatController. So, we don’t need to do anything in 
        // the SessionConnected event.
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // Extract the user’s name from the websocket session and broadcast a 
        // user leave event to all the connected clients.        
        //
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if(username != null) {
            logger.debug("User Disconnected : " + username);

            WebsocketMessage chatMessage = new WebsocketMessage();
            chatMessage.setType(WebsocketMessage.MessageType.LEAVE);
            chatMessage.setSender(username);

            messagingTemplate.convertAndSend(WebsocketEndpoints.PUBLIC, chatMessage);
        }
    }
}
