package com.xx.st.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hanyangyang
 * @since 2024/4/11
 */
@RestController
@RequestMapping("/service2")
public class Service2Controller {

    @RequestMapping("/api1")
    public String api1(){
        return "service 2 api1";
    }
}
