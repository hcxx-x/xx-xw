package org.springframework.boot;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author hanyangyang
 * @date 2025/9/2
 */
@EnableDubbo
@SpringBootApplication
public class SpringbootDubboProviderApp {
    public static void main(String[] args) {
        SpringApplication.run(SpringbootDubboProviderApp.class,args);
    }
}
