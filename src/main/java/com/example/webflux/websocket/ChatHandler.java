package com.example.webflux.websocket;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.example.webflux.redis.ChatConfig;
import com.example.webflux.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class ChatHandler implements WebSocketHandler {
    ConcurrentHashMap<String,Map<String, WebSocketClient>> roomCacheMap = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        HandshakeInfo handshakeInfo = session.getHandshakeInfo();
        InetSocketAddress remoteAddress = handshakeInfo.getRemoteAddress();
        String params = handshakeInfo.getUri().getQuery();

        // Map<String, String> paramMap = Stream.of(params.split("&")).collect(Collectors.toMap(param -> param.split("=")[0], param -> param.split("=")[1]));
        HashMap<String, String> paramMap = HttpUtil.decodeParamMap(params, "UTF-8");

        String roomName = paramMap.get("roomName");
        String userId = paramMap.get("userId");

        //出站
        Mono<Void> output = session.send(Flux.create(sink -> handleClient(roomName, userId, new WebSocketClient(sink, session))));

        //入站
        Mono<Void> input = session.receive()
                .doOnSubscribe(conn -> {
                    log.info("建立连接：{}，用户ip：{}，房间号：{}，用户：{}", session.getId(),
                            remoteAddress.getHostName(), roomName, userId);
                })
                .doOnNext(msg -> {
                    String message = msg.getPayloadAsText();
                    broadcast(roomName, userId, message);
                })
                .doOnComplete(() -> {
                    log.info("关闭连接：{}", session.getId());
                    session.close().toProcessor().then();
                    broadcast(roomName, userId, "退出房间！");
                    removeUser(roomName, userId);
                }).doOnCancel(() -> {
                    log.info("关闭连接：{}", session.getId());
                    session.close().toProcessor().then();
                    broadcast(roomName, userId, "退出房间！");
                    removeUser(roomName, userId);
                }).then();

        return Mono.zip(input, output).then();
    }

    private void removeUser(String roomName, String userId) {
        log.info("用户：{}，退出房间：{}！", userId, roomName);
        Map<String, WebSocketClient> socketClientCacheMap = roomCacheMap.get(roomName);
        socketClientCacheMap.remove(userId);
        if (socketClientCacheMap.isEmpty()) {
            log.info("房间：{}没人了，关闭房间！", roomName);
            roomCacheMap.remove(roomName);
        }
    }

    private void handleClient(String roomName, String userId, WebSocketClient client) {
        if (!roomCacheMap.containsKey(roomName)) {
            log.info("用户：{}，创建房间：{}", userId, roomName);
            Map<String, WebSocketClient> socketClientCacheMap = new HashMap<>();
            socketClientCacheMap.put(userId, client);
            roomCacheMap.put(roomName, socketClientCacheMap);
        } else {
            Map<String, WebSocketClient> socketClientCacheMap = roomCacheMap.get(roomName);
            if (!socketClientCacheMap.containsKey(userId)) {
                log.info("用户：{}，进入房间：{}", userId, roomName);
                socketClientCacheMap.put(userId, client);
            }
        }
    }

    /**
     * 发布消息广播
     */
    public void broadcast(String roomName, String userId, String message) {
        JSONObject msgObj = new JSONObject();
        msgObj.put("roomName",roomName);
        msgObj.put("userId",userId);
        msgObj.put("message",message);

        ChatConfig chatConfig = SpringContextUtil.getBean(ChatConfig.class);
        RedisUtil.convertAndSend(chatConfig.getTopic(),message);

        Map<String, WebSocketClient> clients = roomCacheMap.get(roomName);
        clients.forEach((user, client) -> {
            // 发送消息给除了自己的所有用户
            if (!userId.equals(user)) {
                client.sendData(userId + "：" + message);
            }
        });
    }
}
