package com.xx.springboot3.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hanyangyang
 * @since 2023/4/13
 */
@RestController
@RequestMapping("/native")
public class NativeController {

    @GetMapping("")
    public String getNativeInfo(){
        return "springboot3 native";
    }
}
