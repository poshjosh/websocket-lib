package com.looseboxes.websocket.client;

import com.looseboxes.websocket.server.model.WebsocketMessage;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSession.Subscription;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.messaging.WebSocketStompClient;

/**
 * @author hp
 */
//https://docs.spring.io/spring-framework/docs/4.3.x/spring-framework-reference/html/websocket.html
public class WebSocketClientHandler implements StompSessionHandler, InitializingBean, DisposableBean{
    
    private final Logger log = LoggerFactory.getLogger(WebSocketClientHandler.class);

    private final WebSocketStompClient client;

    private StompSession stompSession;
    
    private final Map<String, Subscription> subscriptionsByDestination;

    /**
     * Every time there is a connection, these destinations are automatically subscribed to
     */
    private final Map<String, StompFrameHandler> autoSubscriptions;

    private final AtomicBoolean connecting = new AtomicBoolean();

    public WebSocketClientHandler(WebSocketStompClient client) {
        this.client = client;
        this.autoSubscriptions = Collections.synchronizedMap(new HashMap<>());
        this.subscriptionsByDestination = Collections.synchronizedMap(new HashMap<>());
    }

    public Optional<StompSession> getStompSessionOptional() {
        return Optional.ofNullable(stompSession);
    }

    public ListenableFuture<StompSession> connect(String url) {

        log.debug("Connecting to: {}", url);

        setConnecting(true);

        return client.connect(url, this);
    }

    public boolean isConnecting() {
        synchronized (connecting) {
            return connecting.get();
        }
    }

    private void setConnecting(boolean flag) {
        synchronized (connecting) {
            connecting.compareAndSet(!flag, flag);
        }
    }

    public boolean isConnected() {
        return stompSession != null && stompSession.isConnected();
    }

    public boolean disconnect() {
        if(this.isConnected()) {
            try{
                this.stompSession.disconnect();
                return true;
            }catch(RuntimeException e) {
                log.warn("Exception disconnecting from session: " + this.stompSession.getSessionId(), e);
            }
        }
        return false;
    }
    
    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        log.debug("Received via websocket\nHeaders: {}\nPayload: ", headers, payload);
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return WebsocketMessage.class;
    }

    /**
     * Invoked when the session is ready to use, i.e. after the underlying
     * transport (TCP, WebSocket) is connected and a STOMP CONNECTED frame is
     * received from the broker.
     * @param session the client STOMP session
     * @param connectedHeaders the STOMP CONNECTED frame headers
     */
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        setConnecting(false);
        log.debug("Websocket stomp session connected, session: {}", session.getSessionId());

        // Disconnects any existing stomp session, so we can assign this new session
        this.disconnect();

        this.stompSession = session;
        this.reSubscribe(session);

        synchronized (autoSubscriptions) {
            autoSubscriptions.forEach((destination, stompFrameHandler) -> {
                this.subscribeIfNotAlready(destination, stompFrameHandler);
            });
        }
    }

    /**
     * Add to destinations which will be automatically subscribed to on each connect
     * @param destination The destination to add
     * @param stompFrameHandler
     */
    public void addAutoSubscription(String destination, StompFrameHandler stompFrameHandler) {
        synchronized (autoSubscriptions) {
            autoSubscriptions.put(destination, stompFrameHandler);
        }
    }

    public Map<String, Subscription> getSubscriptions() {
        synchronized (subscriptionsByDestination) {
            return Collections.unmodifiableMap(subscriptionsByDestination);
        }
    }

    public boolean isSubscribed(String destination) {
        synchronized (subscriptionsByDestination) {
            return subscriptionsByDestination.get(destination) != null;
        }
    }
    
    private void reSubscribe(StompSession session) {
        unsubscribe().forEach(destination -> subscribeIfNotAlready(session, destination, this));
    }
    
    /**
     * Subscribe to the given destination by sending a SUBSCRIBE frame and handle
     * received messages with the specified {@link StompFrameHandler}.
     * @param destination the destination to subscribeIfNotAlready to
     * @param handler the handler for received messages
     * @return {@code true} if the subscription request was sent and a 
     * {@link org.springframework.messaging.simp.stomp.StompSession.Subscription Subscription}
     * response was received, otherwise return {@code false}.
     */
    public boolean subscribeIfNotAlready(String destination, StompFrameHandler handler) {
        return this.subscribeIfNotAlready(stompSession, destination, handler);
    }

    private boolean subscribeIfNotAlready(StompSession session, String destination, StompFrameHandler handler) {
        
        boolean success = false;
        
        if(this.isConnected()) {

            synchronized (subscriptionsByDestination) {
                if (!subscriptionsByDestination.containsKey(destination)) {

                    Subscription subscription = session.subscribe(destination, handler == null ? this : handler);

                    if (subscription != null) {

                        subscriptionsByDestination.put(destination, subscription);

                        success = true;
                    }
                }
            }
        }
        
        if(log.isTraceEnabled()) {
            log.trace("Subscribed {} = {}, remaining: {} in session: {}", 
                    destination, success, subscriptionsByDestination.size(), this.stompSession.getSessionId());
        }
        
        return success;
    }
    
    public Collection<String> unsubscribe() {
        synchronized (subscriptionsByDestination) {
            Set<String> destinations = new HashSet<>(subscriptionsByDestination.keySet());
            List<String> result = new ArrayList<>();
            destinations.forEach(destination -> {
                if (this.unsubscribe(destination)) {
                    result.add(destination);
                }
            });
            return result;
        }
    }

    public boolean unsubscribe(String destination) {

        boolean success = false;

        synchronized (subscriptionsByDestination) {

            Subscription subscription = subscriptionsByDestination.get(destination);

            if (subscription != null) {

                subscription.unsubscribe();

                subscriptionsByDestination.remove(destination);

                success = true;
            }
        }
        
        if(log.isTraceEnabled()) {
            log.trace("Unsubscribed {} = {}, remaining: {} in session: {}", 
                    destination, success, subscriptionsByDestination.size(), this.stompSession.getSessionId());
        }
        
        return success;
    }
    
    /**
     * Handle any exception arising while processing a STOMP frame such as a
     * failure to convert the payload or an unhandled exception in the
     * application {@code StompFrameHandler}.
     * @param session the client STOMP session
     * @param command the STOMP command addBidTopicForAuctionItem the frame
     * @param headers the headers
     * @param payload the raw payload
     * @param exception the exception
     */
    @Override
    public void handleException(StompSession session, @Nullable StompCommand command,
                    StompHeaders headers, byte[] payload, Throwable exception) {
        log.warn("Exception", exception);
    }

    /**
     * Handle a low level transport error which could be an I/O error or a
     * failure to encode or decode a STOMP message.
     * <p>Note that
     * {@link org.springframework.messaging.simp.stomp.ConnectionLostException
     * ConnectionLostException} will be passed into this method when the
     * connection is lost rather than closed normally via
     * {@link StompSession#disconnect()}.
     * @param session the client STOMP session
     * @param exception the exception that occurred
     */
    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        log.warn("Transport error", exception);
    }

    /** {@inheritDoc} */
    @Override
    public void afterPropertiesSet() throws Exception {
        log.debug("#afterPropertiesSet()");
        if (client instanceof InitializingBean) {
            InitializingBean bean = (InitializingBean)client;
            bean.afterPropertiesSet();
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public void destroy() throws Exception{
        log.debug("#destroy()");
        
        if(this.isConnected()) {
            try{
                this.unsubscribe();
            }finally{
                this.disconnect();
            }
        }
        
        if(this.client.isRunning()) {
            this.client.stop();
        }

        if (client instanceof DisposableBean) {
            DisposableBean bean = (DisposableBean)client;
            bean.destroy();
        }
    }
}
