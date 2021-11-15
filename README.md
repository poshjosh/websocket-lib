# Websocket demo server

__Spring Boot WebSocket Server.__

### Requirements

1. Java - 1.8.x

2. Maven - 3.x.x

### Default Properties

```yml
looseboxes
  websocket:
    allowed-origins:
      - http://localhost:${server.port}
    application-destination-prefixes: 
      - /messaging
```

To call the websocket server from a JavaScript client, add the following:

```html
    <script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.1.4/sockjs.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
```

### Default Controller and Interceptor

There is a default `@Controller` i.e `com.looseboxes.websocket.server.controller.WebsocketMessageController`
which routes messages sent to the value of `/${looseboxes.application-destination-prefixes}/send` 
to /topic. For example `/messaging/send/abc/def` will be re-routed to `/topic/abc/def`
If you want to intercept the messages for any reason, the provide an implementation of
`com.looseboxes.websocket.server.WebsocketApplicationDestinationMessageInterceptor` on your classpath.