package com.xx.md.dd;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author hanyangyang
 * @since 2024/3/26
 */
@SpringBootApplication
@MapperScan(basePackages = "com.xx.md.dd.mapper.*")
public class MDDDApplication {
    public static void main(String[] args) {
        SpringApplication.run(MDDDApplication.class, args);
    }
}
