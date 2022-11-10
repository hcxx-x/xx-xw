package com.xx.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/log")
public class LogTestController {

    @GetMapping("/loop/print")
    public void loopPrintLog(){
        for (int i = 0; i < 1000; i++) {
            //log.error("循环输出日志，第{}次输出",i);
        }
    }

}
