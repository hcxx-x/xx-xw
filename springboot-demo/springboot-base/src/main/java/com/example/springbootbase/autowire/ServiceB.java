package com.example.springbootbase.autowire;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author hanyangyang
 * @since 2025/8/15
 **/
@Component
public class ServiceB {

    /**
     * Autowire u默认情况下先根据类型查找，如果查找到多个，者根据属性名称查找bean 找到了就注入
     */
    @Autowired
    private IServiceA serviceA;
}
