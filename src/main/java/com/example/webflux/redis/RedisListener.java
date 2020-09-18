package com.example.webflux.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

/**
 * @program: webflux-websocket-chat
 * @description:
 * @author: 71ang~
 * @create: 2020-07-14 19:00
 * @vsersion: V1.0
 */
@Component
public class RedisListener {

    @Autowired
    private ChatConfig chatConfig;

    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();

        container.setConnectionFactory(connectionFactory);

        // 所有订阅该主题的节点都能收到消息
        container.addMessageListener(listenerAdapter, new PatternTopic(chatConfig.getTopic()));

        return container;
    }
}