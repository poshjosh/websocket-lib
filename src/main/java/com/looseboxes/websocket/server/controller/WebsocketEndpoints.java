package com.looseboxes.websocket.server.controller;

/**
 * @author chinomso ikwuagwu
 */
public final class WebsocketEndpoints {
    private WebsocketEndpoints(){ }
    public static final String ENDPOINT = "/ws";
    public static final String SEND = "/send";
    public static final String JOIN = "/join";
    public static final String MESSAGE_BROKER_DESTINATION_TOPIC = "/topic";
    public static final String MESSAGE_BROKER_DESTINATION_QUEUE = "/queue";
    public static final String PUBLIC = MESSAGE_BROKER_DESTINATION_TOPIC + "/public";
}
