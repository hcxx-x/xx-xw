package com.example.springbootredis.plan2.config;

import com.example.springbootredis.dto.TestDTO;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.aspectj.weaver.ast.Test;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ResolvableType;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author hanyangyang
 * @date 2025/5/8
 */
@Configuration
@EnableCaching
public class RedisConfigPlanB {

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
        /*mapper.activateDefaultTyping(
                ptv,
                ObjectMapper.DefaultTyping.NON_FINAL,  // 对非 final 类记录类型
                JsonTypeInfo.As.EXISTING_PROPERTY               // 类型信息作为独立字段（默认 "@class"）
        );*/

        // 配置3：处理类名/包名变更（通过 MixIn 注解映射旧类名）
        // 示例：将旧类名 com.oldpackage.User 映射到新类 com.newpackage.User
        //mapper.addMixIn(User.class, OldUserMixIn.class);  // 关键行


        return mapper;
    }

    /**
     * 配置 Spring Cache 管理器
     * 作用：整合 Redis 作为缓存存储，统一缓存序列化策略
     */
    @Bean
    @Primary
    public CacheManager cacheManager(
            RedisConnectionFactory factory    // 自动注入 Redis 连接工厂
    ) {
        // 创建 JSON 序列化器（与 RedisTemplate 配置一致）
        ObjectMapper objectMapper = redisObjectMapper();
        Jackson2JsonRedisSerializer<Object> testDTOJackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        testDTOJackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        // 定义缓存通用配置
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(2))    // 默认缓存过期时间：1 小时
                .disableCachingNullValues()       // 禁止缓存 null 值（防御性配置）
                .serializeKeysWith(  // Key 序列化策略
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new StringRedisSerializer()
                        )
                )
                .serializeValuesWith(  // Value 序列化策略
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                testDTOJackson2JsonRedisSerializer
                        )
                );


        // 扫描
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .forPackages("com.example")
                        .setScanners(Scanners.MethodsAnnotated)
        );


         //动态注册每个缓存的序列化器
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();


        Set<Method> methods = reflections.getMethodsAnnotatedWith(Cacheable.class);
        if (methods.isEmpty()) {
            System.out.println("No methods found with @Cacheable!");
        } else {
            methods.forEach(method -> {System.out.println("Found: " + method);
                Cacheable cacheable = method.getAnnotation(Cacheable.class);
                if (cacheable != null) {
                    // 获取方法的返回类型
                    ResolvableType returnType = ResolvableType.forMethodReturnType(method);

                    JavaType javaType = convertResolvableTypeToJavaType(returnType);
                    Jackson2JsonRedisSerializer<?> serializer = new Jackson2JsonRedisSerializer<>(javaType);
                    serializer.setObjectMapper(objectMapper);

                    for (String cacheName : cacheable.value()) {
                        RedisCacheConfiguration otherConfig = RedisCacheConfiguration.defaultCacheConfig()
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                                .entryTtl(Duration.ofHours(1));
                        configMap.put(cacheName, otherConfig);
                    }
                }
            });
        }

        // 构建缓存管理器
        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)     // 应用通用配置
                .transactionAware()        // 支持缓存事务
                .initialCacheNames(configMap.keySet())
                .withInitialCacheConfigurations(configMap)
                .build();
    }


    private JavaType convertResolvableTypeToJavaType(ResolvableType resolvableType) {
        if (resolvableType.getGenerics().length == 0) {
            return TypeFactory.defaultInstance().constructType(resolvableType.resolve());
        }

        JavaType[] generics = new JavaType[resolvableType.getGenerics().length];
        for (int i = 0; i < resolvableType.getGenerics().length; i++) {
            generics[i] = convertResolvableTypeToJavaType(resolvableType.getGeneric(i));
        }

        return TypeFactory.defaultInstance().constructParametricType(
                resolvableType.resolve(),
                generics
        );
    }
}
