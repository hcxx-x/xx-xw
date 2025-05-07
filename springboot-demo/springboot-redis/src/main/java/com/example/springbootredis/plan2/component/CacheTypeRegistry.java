package com.example.springbootredis.plan2.component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheTypeRegistry {
    private static final Map<String, Class<?>> CACHE_TYPE_MAP = new ConcurrentHashMap<>();

    public static void registerAll(Map<String, Class<?>> typeMap) {
        CACHE_TYPE_MAP.putAll(typeMap);
    }

    public static Class<?> getType(String cacheName) {
        return CACHE_TYPE_MAP.get(cacheName);
    }

    // 可选：允许运行时动态覆盖类型
    public static void overrideType(String cacheName, Class<?> type) {
        CACHE_TYPE_MAP.put(cacheName, type);
    }

    public static Map<String, Class<?>> getAllTypes() {
        return CACHE_TYPE_MAP;
    }
}