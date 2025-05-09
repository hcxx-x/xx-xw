package com.example.springbootredis.baseconfig;

import org.springframework.data.redis.cache.CacheKeyPrefix;

/**
 * @author hanyangyang
 * @date 2025/5/9
 */
public class MyCacheKeyPrefix implements CacheKeyPrefix {
    String SEPARATOR = ":";

    @Override
    public String compute(String cacheName) {
        return "projectName:"+cacheName+SEPARATOR;
    }
}
