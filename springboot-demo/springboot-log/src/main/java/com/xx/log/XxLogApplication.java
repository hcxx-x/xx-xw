package com.xx.log;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(value = "com.xx.log.mapper")
public class XxLogApplication {
    public static void main(String[] args) {
        SpringApplication.run(XxLogApplication.class, args);
    }

}
