package com.example.webflux.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @program: webflux-websocket-chat
 * @description:
 * @author: 71ang~
 * @create: 2020-07-14 14:45
 * @vsersion: V1.0
 */
@Component
public class RedisUtil {
    @Autowired
    private RedisTemplate redisTemplate;
    public static RedisTemplate redis;

    @PostConstruct
    public void getRedisTemplate() {
        redis = this.redisTemplate;
    }

    public static Map getOfMap(String key) {
        return redis.opsForHash().entries(key);
    }

    public static void putOfMap(String key, Map map) {
        redis.opsForHash().putAll(key, map);
    }

    public static boolean delete(String key) {
        return redis.delete(key);
    }

    public static void convertAndSend(String channel, Object message) {
        redis.convertAndSend(channel,message);
    }
}