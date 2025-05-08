package com.example.springbootredis.baseconfig;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * 基础版配置
 */
/*@Configuration
@EnableCaching  // 启用 Spring 缓存功能*/
public class RedisConfig {

    /**
     * TIP: 不建议配置成Bean, 一旦配置成Bean会导致springboot 全局的ObjectMapper 被自定义的覆盖，所以不建议这样配置，可以单独创建一个对象去配置RedisTemplate
     * springboot 默认的objectMapper配置可以参考 org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
     *
     * 配置自定义的 ObjectMapper Bean
     * 作用：控制 JSON 序列化/反序列化行为，解决以下问题：
     * 1. 类型擦除导致的 LinkedHashMap 转换异常
     * 2. 类名或包名变更后的反序列化兼容性
     */
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // 配置1：忽略未知字段（避免新增/删除字段导致反序列化失败）
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 配置：允许访问所有字段，
        // PropertyAccessor.ALL：表示该配置应用于所有类型的属性访问器（包括字段、getter、setter 等），
        // JsonAutoDetect.Visibility.ANY：允许 Jackson 检测并处理 任何修饰符的字段（包括 private、protected、public 等）。
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        // 配置2：启用类型信息嵌入（解决 LinkedHashMap 问题）
        // 使用 BasicPolymorphicTypeValidator 允许所有 Object 子类
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(Object.class)  // 允许所有 Object 子类
                .build();
        // 激活默认类型信息，序列化时添加 @class 字段
        mapper.activateDefaultTyping(
                ptv,
                ObjectMapper.DefaultTyping.NON_FINAL,  // 对非 final 类记录类型
                JsonTypeInfo.As.PROPERTY               // 类型信息作为独立字段（默认 "@class"）
        );

        // 配置3：处理类名/包名变更（通过 MixIn 注解映射旧类名）
        // 示例：将旧类名 com.oldpackage.User 映射到新类 com.newpackage.User
        //mapper.addMixIn(User.class, OldUserMixIn.class);  // 关键行


        return mapper;
    }

    /**
     * 定义旧类名的 MixIn 抽象类
     * 作用：当反序列化遇到 JSON 中的旧类名时，自动映射到新类,用于解决类重命名或者包名变更后的兼容性
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@class")
    @JsonTypeName("com.oldpackage.User")  // 指定旧类全限定名
    private abstract static class OldUserMixIn {
        // 此抽象类无需实现，仅用于通过注解声明类型映射
    }

    /**
     * 配置 RedisTemplate Bean
     * 作用：定义 Redis 数据操作模板的序列化策略
     */
    @Bean
    @Primary  // 标记为主 Bean，覆盖 Spring Boot 默认配置
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory factory // 自动注入 Redis 连接工厂
    ) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 关键配置1：Key 序列化器（统一使用字符串序列化）
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);          // 字符串键序列化
        template.setHashKeySerializer(stringSerializer);       // Hash类型内部 键序列化

        // 关键配置2：Value 序列化器（使用带类型信息的 JSON 序列化）
        GenericJackson2JsonRedisSerializer jsonSerializer =
                new GenericJackson2JsonRedisSerializer(redisObjectMapper());
        template.setValueSerializer(jsonSerializer);           // 值序列化
        template.setHashValueSerializer(jsonSerializer);       // Hash类型内部 值序列化
        // 应用配置，检查 RedisTemplate 的必需属性是否已正确配置。 必须调用！确保配置生效
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 配置 Spring Cache 管理器
     * 作用：整合 Redis 作为缓存存储，统一缓存序列化策略
     */
    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory factory    // 自动注入 Redis 连接工厂
    ) {
        // 创建 JSON 序列化器（与 RedisTemplate 配置一致）
        GenericJackson2JsonRedisSerializer jsonSerializer =
                new GenericJackson2JsonRedisSerializer(redisObjectMapper());

        // 定义缓存通用配置
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))    // 默认缓存过期时间：1 小时
                .disableCachingNullValues()       // 禁止缓存 null 值（防御性配置）
                .serializeKeysWith(  // Key 序列化策略
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new StringRedisSerializer()
                        )
                )
                .serializeValuesWith(  // Value 序列化策略
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                jsonSerializer
                        )
                );


        // 构建缓存管理器
        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)     // 应用通用配置
                .transactionAware()        // 支持缓存事务
                .build();
    }
}