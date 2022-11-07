package com.xx.mvc.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @auther: hanyangyang
 * @date: 2022/11/7
 */
@RestController()
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/info")
    public void getUserInfo(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("身份信息："+authentication.getPrincipal().toString());
        System.out.println("权限信息"+authentication.getAuthorities().toString());
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 在默认情况下SecurityContextHolder采用的策略是ThreadLocal获取认证信息，所以在子线程中获取不到，
                // 如果需要在子线程中获取认证信息，需要修改策略,修改方式：在启动参数中添加 -Dspring.security.strategy=MODE_INHERITABLETHREADLOCAL
                Authentication authentication1 = SecurityContextHolder.getContext().getAuthentication();
                System.out.println("子线程："+authentication1.getPrincipal().toString());
                System.out.println("子线程："+authentication1.getAuthorities().toString());
            }
        }).start();
    }
}
