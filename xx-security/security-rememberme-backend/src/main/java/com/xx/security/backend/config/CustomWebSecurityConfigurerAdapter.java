package com.xx.security.backend.config;

import com.xx.security.backend.filter.CustomUsernamePasswordFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * 自定义WebSecurity配置
 */
@Configuration
public class CustomWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {


    @Bean
    public UserDetailsService userDetailsService(){
        InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
        inMemoryUserDetailsManager.createUser(User.builder().username("user").password("{noop}user").roles("admin").build());
        return inMemoryUserDetailsManager;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public RememberMeServices rememberMeServices(){
        return new CustomRememberMeService(UUID.randomUUID().toString(),userDetailsService(),new InMemoryTokenRepositoryImpl());
    }

    @Bean
    public CustomUsernamePasswordFilter customUsernamePasswordFilter() throws Exception {
        CustomUsernamePasswordFilter filter = new CustomUsernamePasswordFilter();
        // 后面需要使用这个自定义的filter 替换UsernamePasswordAuthenticationFilter，
        // 所以原来在UsernamePasswordAuthenticationFilter中的相关配置就可以移动到这里来进行设置了，比如下面这些
        filter.setUsernameParameter("username");
        filter.setPasswordParameter("password");
        filter.setRememberMeParameter("remember-me");
        filter.setRememberMeServices(rememberMeServices());
        filter.setAuthenticationManager(authenticationManagerBean());
        return filter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()// 开启对http请求的认证
                .mvcMatchers("/allow", "/kaptcha/getImage").permitAll()// 放行"/allow"请求，放行的请求需要在anyRequest().authenticated()之前配置
                .anyRequest().authenticated()// 表示所有请求都需要经过认证
                .and() // 返回 HttpSecurity 对象
                .formLogin()// 采用表单登陆（默认表单）
                .successHandler(new CustomAuthenticationSuccessHandler())//处理前后端分离项目需要返回json数据的场景
                .failureHandler(new CustomAuthenticationFailHandler())
                .and()
                .logout().logoutSuccessHandler(new CustomLogoutSuccessHandler())
                .and()
                .rememberMe()
                .rememberMeServices(rememberMeServices())
                .and()
                .csrf().disable() // 禁用csrf(跨站请求保护)
        ;

        http.addFilterAt(customUsernamePasswordFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
