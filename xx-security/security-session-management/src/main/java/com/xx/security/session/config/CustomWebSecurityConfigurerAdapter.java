package com.xx.security.session.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
                        HttpServletResponse response = event.getResponse();
                        Map<String, Object> result = new HashMap<>();
                        result.put("msg","您已在其他地方登陆");
                        result.put("status",500);
                        response.setContentType("application/json;charset=UTF-8");
                        String resultJosnStr = new ObjectMapper().writeValueAsString(result);
                        response.getWriter().println(resultJosnStr);
                    }
                })// 前后端分离中回话失效后的处理逻辑，如果使用这个方法配置了前后端分离的处理逻辑，那么上面的跳转url则会失效
                //.maxSessionsPreventsLogin(true) // 当达到最大回话的时候拒绝登陆
        ;
    }

    /**
     * 据说是在以前的版本需要加上这个配置，目前的的新版本 已经不需要了（案例所用版本为springboot2.6.2）
     * @return
     */
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher(){
        return new HttpSessionEventPublisher();
    }
}
