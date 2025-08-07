package com.xx.log.beanlife;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hanyangyang
 * @since 2025/8/7
 **/
@Configuration
public class BeanLifeCycleConfig {
    @Bean(initMethod = "initMethod",destroyMethod = "customDestroyMethod")
    public BeanLifeCicirle beanLifeCicirle(){
        return new BeanLifeCicirle();
    }
}
