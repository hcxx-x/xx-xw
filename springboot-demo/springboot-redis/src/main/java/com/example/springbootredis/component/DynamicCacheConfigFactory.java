package com.example.springbootredis.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.event.EventListener;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
@Component

public class DynamicCacheConfigFactory {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;



    @Bean
    public RedisCacheManager cacheManager() {
        log.info("开始构建新的cacheManager");
        // 获取所有已注册的缓存类型
        Map<String, Class<?>> typeMap = CacheTypeRegistry.getAllTypes();

        // 为每个缓存名称创建专用配置
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        typeMap.forEach((cacheName, targetType) -> {
            Jackson2JsonRedisSerializer<?> serializer =
                    new Jackson2JsonRedisSerializer<>(targetType);

            RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                    .serializeValuesWith(
                            RedisSerializationContext.SerializationPair.fromSerializer(serializer)
                    );

            cacheConfigs.put(cacheName, config);
        });

        RedisCacheManager newCacheManager = RedisCacheManager.builder(redisConnectionFactory)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
        return newCacheManager;
    }



}
