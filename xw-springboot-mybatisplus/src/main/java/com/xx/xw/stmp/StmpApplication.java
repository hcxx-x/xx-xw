package com.xx.xw.stmp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.xx.xw.stmp.mapper")
public class StmpApplication {
    public static void main(String[] args) {
        SpringApplication.run(StmpApplication.class,args);
    }
}
