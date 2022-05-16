package com.xx.web;

import com.dtflys.forest.springboot.annotation.ForestScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 *  com.gitee.sunchenbin.mybatis.actable.dao.*  和  com.gitee.sunchenbin.mybatis.actable.manager.* 的相关配置是使用acTable 逆向生成表所需要的
 *  如果不需要可以将相关配置去掉
 */
@SpringBootApplication(scanBasePackages = {"com.gitee.sunchenbin.mybatis.actable.manager.*","com.xx.web"})
@MapperScan(basePackages = {"com.xx.web.mapper","com.gitee.sunchenbin.mybatis.actable.dao"})
@ForestScan(basePackages = "com.xx.web.client")
public class XxWebApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(XxWebApplication.class);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}