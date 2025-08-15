package com.example.springbootbase.autowire;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author hanyangyang
 * @since 2025/8/15
 **/
@Component
public class ServiceC {

    /**
     * 这里会注入失败，因为能够根据serviceA 找到对应的Bean 但是类型不是Serviceb的类型，所以失败了
     * 可以说明resource 是先根据属性名称区找Bean得
     */
    @Resource
    private ServiceB serviceA;
}
