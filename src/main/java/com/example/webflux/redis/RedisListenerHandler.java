package com.example.webflux.redis;

import com.alibaba.fastjson.JSONObject;
import com.example.webflux.websocket.ChatHandler;
import com.example.webflux.websocket.WebSocketClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

/**
 * @program: webflux-websocket-chat
 * @description: redis消息订阅发布
 * @author: 71ang~
 * @create: 2020-07-14 18:53
 * @vsersion: V1.0
 */
@Slf4j
@Component
public class RedisListenerHandler extends MessageListenerAdapter {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 消息订阅处理
     * @param message
     * @param pattern
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        JdkSerializationRedisSerializer serializer = new JdkSerializationRedisSerializer();

        byte[] body = message.getBody();
        String rawMsg;
        try {
            rawMsg = String.valueOf(serializer.deserialize(body));

            JSONObject msgObj = JSONObject.parseObject(rawMsg);
            String roomName = msgObj.getString("roomName");
            String userId = msgObj.getString("userId");
            String msg = msgObj.getString("message");

            // 发送消息
            ChatHandler.sendToAll(roomName,userId,msg);
        } catch (Exception e) {
            return;
        }
    }
}