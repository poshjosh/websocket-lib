package com.looseboxes.websocket.server.model;

import java.time.Instant;
import java.util.Objects;

/**
 * @author chinomso ikwuagwu
 * @param <ID_TYPE> The type of the id
 * @param <CONTENT_TYPE> The type of the content
 */
public class WebsocketMessage<ID_TYPE, CONTENT_TYPE>{
    
    public enum MessageType {
        INFO,
        ERROR,
        JOIN,
        LEAVE
    }
    
    private ID_TYPE id;
    private MessageType type = MessageType.INFO;
    private String sender;
    private Instant timestamp = Instant.now();
    private CONTENT_TYPE content;
    
    public WebsocketMessage<ID_TYPE, CONTENT_TYPE> id(ID_TYPE id) {
        setId(id);
        return this;
    }

    public ID_TYPE getId() {
        return id;
    }

    public void setId(ID_TYPE id) {
        this.id = id;
    }

    public WebsocketMessage<ID_TYPE, CONTENT_TYPE> type(MessageType type) {
        setType(type);
        return this;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public WebsocketMessage<ID_TYPE, CONTENT_TYPE> sender(String sender) {
        setSender(sender);
        return this;
    }
    
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public WebsocketMessage<ID_TYPE, CONTENT_TYPE> timestamp(Instant timestamp) {
        setTimestamp(timestamp);
        return this;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public WebsocketMessage<ID_TYPE, CONTENT_TYPE> content(CONTENT_TYPE content) {
        setContent(content);
        return this;
    }

    public CONTENT_TYPE getContent() {
        return content;
    }

    public void setContent(CONTENT_TYPE content) {
        this.content = content;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WebsocketMessage<?, ?> other = (WebsocketMessage<?, ?>) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "WebsocketMessage{" + "id=" + id + ", type=" + type + ", sender=" + sender + ", timestamp=" + timestamp + ", content=" + content + '}';
    }
}
