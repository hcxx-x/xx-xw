package com.xx.security.execption.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @auther: hanyangyang
 * @date: 2022/11/16
 */
@Configuration
public class CustomWebSecurityAdapter extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                .authenticated()
                .and().
                formLogin()
                .and()
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint((request,response,authenticationException)->{
                    Map<String,Object> rs = new HashMap<>();
                    rs.put("code",401);
                    rs.put("msg","尚未认证!");
                    ObjectMapper objectMapper = new ObjectMapper();
                    String json = objectMapper.writeValueAsString(rs);
                    response.setStatus(200);
                    response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                    response.getWriter().println(json);
                }) // 认证异常的处理
                .accessDeniedHandler((request,response,accessDeniedException)->{
                    Map<String,Object> rs = new HashMap<>();
                    rs.put("code",401);
                    rs.put("msg","没有权限!");
                    ObjectMapper objectMapper = new ObjectMapper();
                    String json = objectMapper.writeValueAsString(rs);
                    response.setStatus(200);
                    response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                    response.getWriter().println(json);
                })// 授权异常的处理
                ;
    }
}
