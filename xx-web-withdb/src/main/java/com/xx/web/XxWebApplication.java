package com.xx.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @MapperScan中的 “com.gitee.sunchenbin.mybatis.actable.dao.*”  和  “com.gitee.sunchenbin.mybatis.actable.manager.*”
 * 是使用acTable 逆向生成表所需要的
 */
@SpringBootApplication(scanBasePackages = {"com.xx.web"})
@MapperScan(basePackages = {"com.xx.web.mapper", "com.gitee.sunchenbin.mybatis.actable.dao", "com.gitee.sunchenbin.mybatis.actable.manager.*"})
@EnableAsync
public class XxWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(XxWebApplication.class);
    }
}