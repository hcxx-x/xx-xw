package com.example.springbootredis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.annotation.Resource;


@EnableAspectJAutoProxy(proxyTargetClass = true) // 确保启用 AOP 自动代理
@SpringBootApplication(scanBasePackages = "com.example.springbootredis")
public class SpringbootRedisApplication {



    public static void main(String[] args) {
        SpringApplication.run(SpringbootRedisApplication.class, args);
    }


}
