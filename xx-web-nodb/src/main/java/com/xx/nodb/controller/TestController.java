package com.xx.nodb.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("click")
    public void testClickNum(){
        log.info("当前系统时间：{}",System.currentTimeMillis());
    }
}
