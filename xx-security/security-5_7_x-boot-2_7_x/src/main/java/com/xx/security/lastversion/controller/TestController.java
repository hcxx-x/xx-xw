package com.xx.security.lastversion.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @auther: hanyangyang
 * @date: 2022/11/25
 */
@RequestMapping("/test")
@RestController
public class TestController {

    @GetMapping
    public String test(){
        return "test";
    }
}
