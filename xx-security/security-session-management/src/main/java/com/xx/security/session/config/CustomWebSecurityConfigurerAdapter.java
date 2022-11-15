package com.xx.security.session.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @auther: hanyangyang
 * @date: 2022/11/15
 */
@Configuration
public class CustomWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()// 开启对http请求的认证
                .anyRequest().authenticated()// 表示所有请求都需要经过认证
                .and() // 返回 HttpSecurity 对象
                .formLogin()// 采用表单登陆（默认表单）
                .and()
                .csrf().disable()
                .sessionManagement()//配置session管理器
                .maximumSessions(1) // 设置最大回话数量
                .expiredUrl("/login")  //回话过期后跳转的路径
                .expiredSessionStrategy(new SessionInformationExpiredStrategy() {
                    @Override
                    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {

                    }
                })// 前后端分离中回话失效后的处理逻辑
                .maxSessionsPreventsLogin(true) // 当达到最大回话的时候拒绝登陆
        ;
    }
}
