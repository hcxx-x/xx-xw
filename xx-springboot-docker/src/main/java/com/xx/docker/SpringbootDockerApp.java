package com.xx.docker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hanyangyang
 * @since 2023/3/8
 */

@RestController
@SpringBootApplication
public class SpringbootDockerApp {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootDockerApp.class, args);
    }
    @GetMapping("/docker")
    public String docker(){
        return "docker";
    }
}
