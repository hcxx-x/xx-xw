package com.example.springbootredis.baseconfig;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 只是用来生成 redis key的最后一部分，默认情况下就是::后面的部分,当@Cacheable 指定了key时不支持指定生成器
 * @author hanyangyang
 * @date 2025/5/9
 */
@Component
public class MyKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        return target.getClass().getSimpleName() + "#" + method.getName() ;
    }
}
