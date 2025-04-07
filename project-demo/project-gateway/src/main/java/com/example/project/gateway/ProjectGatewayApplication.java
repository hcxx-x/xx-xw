package com.example.project.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * @author hanyangyang
 * @since 2025/4/7
 */

@SpringBootApplication
@ConfigurationPropertiesScan({"com.example.project.gateway.property"})
public class ProjectGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProjectGatewayApplication.class, args);
    }


}
