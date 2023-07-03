package com.xx.xw.stmp.controller;

import com.xx.xw.stmp.service.ITestService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private ITestService testService;

    @RequestMapping("/tx")
    public String testTx(){
        testService.testTx();
        return "ok";
    }

    @RequestMapping("/batch")
    public void testBatch(){
        testService.testBatchFail();
    }
}
