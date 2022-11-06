package com.xx.mvc.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
public class HelloController {

    @GetMapping("/hello/security")
    public String helloSecurity(){
        return "hello security";
    }

    @GetMapping("/allow")
    public String allow(){
        return "allow";
    }

    @GetMapping("/allow/next")
    public String allowNext(){
        return "allow next";
    }
}
