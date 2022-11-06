package com.xx.mvc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 自定义WebSecurity配置
 */
@Configuration
public class CustomWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()// 开启对http请求的认证
                .mvcMatchers("/allow","/login.html","/loginFail").permitAll()// 放行"/allow"请求，放行的请求需要在anyRequest().authenticated()之前配置
                .anyRequest().authenticated()// 表示所有请求都需要经过认证
                .and() // 返回 HttpSecurity 对象
                .formLogin()// 采用表单登陆（默认表单）
                .loginPage("/login.html")  // 用来指定默认的登陆页面，在mvc中，指定的是跳转到登录页面的后端接口地址，注意：一旦自定义登陆页面需要现实的指定处理登陆的url
                .loginProcessingUrl("/login") // 指定处理登陆请求的url
                //.usernameParameter("username") // 自定义表单参数名-用户名，默认username
                //.passwordParameter("password")// 自定义表单参数名-密码，默认password
                //.successForwardUrl("/hello/security")// 认证成功之后forward的路径
                //.defaultSuccessUrl("/hello/security")// 认证之后的redirect路径，和successForwardUrl只能使用其中一个，并且如果之前有请求路径，会重定向到之前的url,只有当直接请求登陆接口后才会重定向到这里配置的url, 但是这种形式可以通过传入第二个参数(传true)让其始终跳到这个url
                .failureForwardUrl("/loginFail")// 认证失败后的forword的跳转，注意"/loginFail"这个请求必须支持post请求，
                //.failureUrl("/loginFail") // 认证失败后的redirect跳转，具体的请求方式暂时未尝试，应该是都可以的
                .and()
                .csrf().disable() // 禁用csrf(跨站请求保护)
        ;
    }
}
