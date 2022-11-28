package com.xx.springboot.redis.config;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author 土味儿
 * Date 2022/3/10
 * @version 1.0
 */
@Configuration
public class MyRedisConfig {
    /**
     * 自定义RedisTemplate
     * @param redisConnectionFactory
     * @return RedisTemplate<String, Object>
     */
    @Bean
    public <V> RedisTemplate<String, V>  redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // 直接使用 <String,Object>，避免类型转换
        RedisTemplate<String, V> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // ===== 序列化设置 =====
        // --------------- Jackson 序列化 -----------------
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // enableDefaultTyping方法过期
        //objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        // 替换enableDefaultTyping方法
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance ,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.WRAPPER_ARRAY);
        // 设置对象映射
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        // --------------- String 序列化 -----------------
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // ===== RedisTemplate 序列化设置 =====
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hashKey采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);

        // value采用Jackson的序列化方式
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // HashValue采用Jackson的序列化方式
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        template.afterPropertiesSet();
        return template;
    }
}
