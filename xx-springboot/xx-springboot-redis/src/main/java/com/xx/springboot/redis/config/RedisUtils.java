package com.xx.springboot.redis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @author hanyangyang
 * @since 2023/5/9
 */
@Component
public class RedisUtils {
    private static RedisTemplate redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        RedisUtils.redisTemplate = redisTemplate;
    }

    public static <T> Set<T> sGet(String key,Class<T> clazz){
        //return redisTemplate.opsForSet().members(key);

        Set<T> set = key == null ? null : redisTemplate.opsForSet().members(key);
        return set;
    }


    public static long lAdd(final String key, Object value)
    {
        Long count = redisTemplate.opsForList().leftPush(key, value);
        return count == null ? 0 : count;
    }


    //存储时，value为一个对象，返回时，value默认为 object类型，需要进行类型转换
    @SuppressWarnings("unchecked")
    public static <T> List<T> getList(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForList().range(key,0,-1);
        if (value != null) {
            return (List<T>) value;
        }
        return null;
    }




}
