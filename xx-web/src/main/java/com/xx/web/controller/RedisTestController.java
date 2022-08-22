package com.xx.web.controller;

import com.xx.web.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/redis")
public class RedisTestController {

    @RequestMapping("/test")
    @Transactional(rollbackFor = Exception.class)
    public void test() throws InterruptedException {
        RedisUtils.lock("lock",10L);
        int count =1000;
        while (count>0){
            log.info("当前count:{}",count);
            --count;
            Thread.sleep(1000L);
        }
        RedisUtils.set("test","test");
        RedisUtils.del("lock");
    }
}
