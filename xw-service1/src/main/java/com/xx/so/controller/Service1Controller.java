package com.xx.so.controller;

import com.xx.so.feign.Service2Client;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author hanyangyang
 * @since 2024/4/11
 */
@RestController
@RequestMapping("/service1")
public class Service1Controller {
    @Resource
    private Service2Client service2Client;

    @RequestMapping("/api1")
    public String api1(){
        return "service 1 api1";
    }

    @RequestMapping("/feign/api1")
    public String feignApi1() throws Throwable {
        return service2Client.api1();
    }
}
