package com.xx.so.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hanyangyang
 * @since 2024/4/11
 */
@RestController
@RequestMapping("/service1")
public class Service1Controller {

    @RequestMapping("/api1")
    public String api1(){
        return "service 1 api1";
    }
}
