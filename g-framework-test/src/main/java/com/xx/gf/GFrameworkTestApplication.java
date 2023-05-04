package com.xx.gf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"com.xx.gf","com.gf"})
public class GFrameworkTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(GFrameworkTestApplication.class, args);
    }

}
