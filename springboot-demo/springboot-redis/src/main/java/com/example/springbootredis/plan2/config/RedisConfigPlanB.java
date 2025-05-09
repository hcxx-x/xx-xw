package com.example.springbootredis.plan2.config;

import com.example.springbootredis.dto.TestDTO;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.DefaultBaseTypeLimitingValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Test;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.aop.support.AopUtils;
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
 * 通过提前扫描被@cacheable注解的方法，获取方法返回值类型，并为每一个cachename租注册一个缓存配置来实现不依赖@class属性的反序列化
 * 存在的问题：
 *  1、只能通过cachename来映射，cachename 其实就是@Cachaable注解的value值，不能根据完整Redis key名映射
 *
 *
 * @author hanyangyang
 * @date 2025/5/8
 */
@Slf4j
@Configuration
@EnableCaching
public class RedisConfigPlanB {

    static final String SCAN_PACKAGE = "com.example";

    static final ObjectMapper defaultObjectMapper;

    static {
        defaultObjectMapper = new ObjectMapper();
        // 配置1：忽略未知字段（避免新增/删除字段导致反序列化失败）
        defaultObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 配置：允许访问所有字段，
        // PropertyAccessor.ALL：表示该配置应用于所有类型的属性访问器（包括字段、getter、setter 等），
        // JsonAutoDetect.Visibility.ANY：允许 Jackson 检测并处理 任何修饰符的字段（包括 private、protected、public 等）。
        defaultObjectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 注意，这里不能启用多态类型处理，否则会因为json字符串中没有包含@class属性而反序列化失败，
        // 因为新版的jackson修复了一个反序列化漏洞，如果要启动多态处理，就会需要一个验证器，但是这个验证器是依赖@class属性的，
        // 所以当json字符串中没有class属性的时候，不能启用这个多态处理器，甚至可以清空多态处理设置
        // mapper.deactivateDefaultTyping();
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
        Jackson2JsonRedisSerializer<Object> testDTOJackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        testDTOJackson2JsonRedisSerializer.setObjectMapper(defaultObjectMapper);

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


        //缓存名称和缓存配置的映射关系
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();

        // 扫描指定包中被cacheable注解的方法
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .forPackages(SCAN_PACKAGE)
                        .setScanners(Scanners.MethodsAnnotated)
        );
        Set<Method> methods = reflections.getMethodsAnnotatedWith(Cacheable.class);

        if (methods.isEmpty()) {
            log.info("没有找到被@Cacheable注解的方法");
        } else {
            methods.forEach(method -> {
                log.info("找到被@Cacheable注解的方法：{}.{}", method.getDeclaringClass().getName(), method.getName());
                Cacheable cacheable = method.getAnnotation(Cacheable.class);
                if (cacheable != null) {
                    // 获取方法的返回类型
                    ResolvableType returnType = ResolvableType.forMethodReturnType(method);
                    JavaType javaType = convertResolvableTypeToJavaType(returnType);
                    Jackson2JsonRedisSerializer<?> serializer = new Jackson2JsonRedisSerializer<>(javaType);
                    serializer.setObjectMapper(defaultObjectMapper);
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


    /**
     * 将Spring的ResolvableType转换为Jackson的JavaType
     * 这个方法主要用于处理泛型类型，将嵌套的泛型类型结构转换为Jackson可以理解的JavaType
     * 以便在序列化和反序列化过程中正确处理泛型信息
     *
     * @param resolvableType Spring的ResolvableType对象，代表需要转换的类型
     * @return Jackson的JavaType对象，代表转换后的类型
     */
    private JavaType convertResolvableTypeToJavaType(ResolvableType resolvableType) {
        // 当没有泛型时，直接将ResolvableType转换为JavaType
        if (resolvableType.getGenerics().length == 0) {
            return TypeFactory.defaultInstance().constructType(resolvableType.resolve());
        }

        // 初始化泛型JavaType数组，用于存储转换后的泛型类型
        JavaType[] generics = new JavaType[resolvableType.getGenerics().length];
        // 遍历ResolvableType的泛型，递归转换每个泛型为JavaType
        for (int i = 0; i < resolvableType.getGenerics().length; i++) {
            generics[i] = convertResolvableTypeToJavaType(resolvableType.getGeneric(i));
        }

        // 构造并返回参数化类型（处理泛型类或接口）
        return TypeFactory.defaultInstance().constructParametricType(
                resolvableType.resolve(),
                generics
        );
    }

}
