package com.xx.security.session.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @auther: hanyangyang
 * @date: 2022/11/15
 */
@RestController
public class TestController {
    @RequestMapping("/test")
    public String test(){
        return "test";
    }
}
