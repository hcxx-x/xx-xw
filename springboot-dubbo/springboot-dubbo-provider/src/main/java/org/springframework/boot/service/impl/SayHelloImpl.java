package org.springframework.boot.service.impl;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.boot.springbootdubboapi.SayHello;

/**
 * @author hanyangyang
 * @date 2025/9/2
 */
@DubboService
public class SayHelloImpl implements SayHello {
    @Override
    public String sayHello() {
        System.out.println("sayHello invoke");
        return "hello dubbo";
    }
}
