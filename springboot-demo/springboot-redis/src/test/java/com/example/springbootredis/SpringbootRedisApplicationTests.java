package com.example.springbootredis;

import com.example.springbootredis.service.TestServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@Slf4j
@SpringBootTest(classes = SpringbootRedisApplication.class)
class SpringbootRedisApplicationTests {

    @Resource
    private TestServices testServices;

    @Test
    void contextLoads() {
       log.info("testDTO:{}",testServices.getTestDTO());
    }

}
