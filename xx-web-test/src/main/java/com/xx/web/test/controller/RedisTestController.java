package com.xx.web.test.controller;

import com.xx.web.test.util.RedisUtils;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/redis")
public class RedisTestController {

    @RequestMapping("/set")
    public void set(){
        System.out.println(Thread.currentThread().getName());
       RedisUtils.set("key","value");
    }

    @RequestMapping("/test")
    @XxlJob("redisTestJob")
    public void test() throws InterruptedException {
        System.out.println("开始执行.......");
        RedisUtils.lock("lock",10L);
        long timeoutMills = System.currentTimeMillis() + 20 * 1000;
        while (true){
            System.out.println(System.currentTimeMillis());
            if (System.currentTimeMillis()>timeoutMills){
                break;
            }

        }
        RedisUtils.set("test","test");
        RedisUtils.del("lock");
    }
}
