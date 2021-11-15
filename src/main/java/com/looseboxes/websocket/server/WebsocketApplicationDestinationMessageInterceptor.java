package com.looseboxes.websocket.server;

import com.looseboxes.websocket.server.model.WebsocketMessage;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

public interface WebsocketApplicationDestinationMessageInterceptor {

    WebsocketMessage intercept(String path, SimpMessageHeaderAccessor simpMessageHeaderAccessor, WebsocketMessage websocketMessage);
}
