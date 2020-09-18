package com.example.webflux.websocket;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.FluxSink;

import java.io.Serializable;

/**
 * @program: webflux-websocket-chat-websocket-chat
 * @description:
 * @author: 71ang~
 * @create: 2020-07-14 13:39
 * @vsersion: V1.0
 */
@Slf4j
@Data
public class WebSocketClient implements Serializable {
    private static final long serialVersionUID = 3126044575672218399L;
    private FluxSink<WebSocketMessage> sink;
    private WebSocketSession session;

    public WebSocketClient(FluxSink<WebSocketMessage> sink, WebSocketSession session) {
        this.sink = sink;
        this.session = session;
    }

    public void sendData(String data) {
        sink.next(session.textMessage(data));
    }
}