package com.xx.docker;

import com.xx.docker.component.AsyncComponent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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

    @Resource
    private AsyncComponent asyncComponent;

    @GetMapping("/docker")
    public String docker(){
        for (int i = 0; i < 1000; i++) {
            asyncComponent.asyncExecutorTask();
        }
        return "docker";
    }
}
