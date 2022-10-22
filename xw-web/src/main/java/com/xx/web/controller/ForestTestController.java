package com.xx.web.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.xx.web.client.ForestTestClient;
import com.xx.web.context.ForestThreadLocalContext;
import com.xx.web.event.SimpleEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

/**
 * @author hanyangyang
 */
@Slf4j
@RequestMapping("forest")
@RestController
public class ForestTestController {

    @Autowired
    private ForestTestClient forestTestClient;

    @Autowired
    private ApplicationEventPublisher publisher;

    @GetMapping
    public String test(){
        String snowflakeNextIdStr = IdUtil.getSnowflakeNextIdStr();
        log.info("controller，当前线程id：{},内容：{}",Thread.currentThread().getId(),snowflakeNextIdStr);
        ForestThreadLocalContext.setAttribute("key", snowflakeNextIdStr);
        String value = forestTestClient.helloForest();
        log.info("forest调用结果：{}",value);
        if (!ObjectUtil.equal(snowflakeNextIdStr,value)){
            throw new RuntimeException("出现并发问题");
        }
        return snowflakeNextIdStr;
    }

    @GetMapping("/getValue")
    public String getValue(@RequestParam String value){
        return value;
    }


    @GetMapping("/publish/event")
    public void publishEvent(){
        log.info("事件发布，当前线程id:{}", Thread.currentThread().getId());
        String snowflakeNextIdStr = IdUtil.getSnowflakeNextIdStr();
        SimpleEvent simpleEvent = new SimpleEvent(snowflakeNextIdStr);
        //事件发布
        publisher.publishEvent(simpleEvent);
    }
}
