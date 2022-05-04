package com.xx.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.xx.web.mapper")
public class XxWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(XxWebApplication.class);
    }
}