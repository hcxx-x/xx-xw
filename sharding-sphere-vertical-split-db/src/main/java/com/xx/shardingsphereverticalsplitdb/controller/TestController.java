package com.xx.shardingsphereverticalsplitdb.controller;

import com.xx.shardingsphereverticalsplitdb.service.TestService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/test")
public class TestController {
    @Resource
    private TestService testService;

    @RequestMapping("/tx")
    public void testTx(){
        testService.testTx();
    }
}
