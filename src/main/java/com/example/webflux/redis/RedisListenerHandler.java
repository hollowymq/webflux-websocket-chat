package com.example.webflux.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.util.Arrays;

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

    @Override
    public void onMessage(Message message, byte[] pattern) {
        JdkSerializationRedisSerializer serializer = new JdkSerializationRedisSerializer();

        byte[] body = message.getBody();
        byte[] channel = message.getChannel();
        String rawMsg;
        String topic;
        try {
            rawMsg = String.valueOf(serializer.deserialize(body));
            topic = redisTemplate.getStringSerializer().deserialize(channel);

            System.out.println(rawMsg);
        } catch (Exception e) {
            return;
        }
    }
}