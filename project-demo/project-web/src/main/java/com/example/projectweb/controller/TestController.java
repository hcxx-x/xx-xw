package com.example.projectweb.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hanyangyang
 * @since 2025/4/7
 */
@Slf4j
@RequestMapping("/test")
@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        log.info("test");
        return "success";
    }
}
