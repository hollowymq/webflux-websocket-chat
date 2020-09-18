package com.example.webflux.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @program: webflux-websocket-chat
 * @description: 聊天配置
 * @author: Yang Mingqiang
 * @create: 2020-09-17 14:58
 * @vsersion: V1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "chat")
public class ChatConfig {
    private String topic;
}