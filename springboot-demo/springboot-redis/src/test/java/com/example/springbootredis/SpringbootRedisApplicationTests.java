package com.example.springbootredis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class SpringbootRedisApplicationTests {

    @Resource
    private ObjectMapper redisObjectMapper;

    @Resource
    private ObjectMapper objectMapper;

    @Test
    void contextLoads() {
        System.out.println(redisObjectMapper);
        System.out.println(objectMapper);
    }

}
