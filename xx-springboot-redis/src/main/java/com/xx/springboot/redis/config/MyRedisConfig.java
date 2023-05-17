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
     * 这里没有指定泛型，表示可以接受所有类型
     * 这个类的配置在当前项目中有些配置会失效，比如对于jackson中配置序列化后的json对象包含类信息的配置，在这个项目中测试就没有生效
     * 不知道为什么，可能是这个项目的环境太复杂了，各种版本，各种配置都有，把这个配置拷贝到一个单独的项目中就生效了，不知道为什么...
     * @param redisConnectionFactory
     */
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // 直接使用 <String,Object>，避免类型转换
        RedisTemplate template = new RedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);

        // --------------- Jackson 序列化 -----------------
        // 这里面的一段代码可以让该序列化器在序列化的时候将对应的类信息添加到序列化后的json对象中，
        // 这样就可以再反序列化的时候直接强转为对应的对象，而如果不加那段配置序列化后进行反序列化不可强转为对应的对象，
        // 因为反序列化后的类型为LinkedHashMap, 据说同样的功能使用GenericJackson2JsonRedisSerializer 也可以做到
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = getJackson2JsonRedisSerializer();
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

    private static Jackson2JsonRedisSerializer<Object> getJackson2JsonRedisSerializer() {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        // ===================配置jackson序列化的时候将类型信息添加到序列化后的json字符串中====================
        // 老版本使用下面这个方法，不过新版本enableDefaultTyping方法已经过期
        //objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        // 代代替enableDefaultTyping方法的方法
        objectMapper = objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.WRAPPER_ARRAY);
        // ===================配置jackson序列化的时候将类型信息添加到序列化后的json字符串中-结束====================
        // 设置对象映射
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        return jackson2JsonRedisSerializer;
    }
}
