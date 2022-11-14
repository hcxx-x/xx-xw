package com.xx.security.rememberme.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.UUID;

/**
 * 自定义WebSecurity配置
 */
@Configuration
public class CustomWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    /**
     * 如果没有手动进行UserDetailsService Bean 的设置的话，这里默注入的是InMemoryUserDetailsManager
     *
     * 声明该bean未知的未知是 UserDetailsServiceAutoConfiguration
     *
     * 实验证明，通过这种方式注入的userDetailsService 搭配remember me 后使用会出现用户名或密码错误问题，
     * 原因大概是这里注入的userDetailService中没有将在配置文件中配置的用户和密码设置进来
     */
   /* @Resource
    private UserDetailsService userDetailsService;*/

    /**
     * 自定义UserDetailsService，并添加到容器中
     *
     * 注意，要加上@Bean注解，如果不加上@Bean注解，那么默认认证使用的UserDetailService 中的用户可能和这里的不同
     * 如果需要在不加@Bean注解使用remember me的话需要保证这个方法中配置的用户名密码和配置文件中配置的相同，不然可能会出现用户名密码错误的问题
     * @return
     */
    @Bean
    public UserDetailsService userDetailsService(){
        return new InMemoryUserDetailsManager(User.builder().username("user").password("{noop}user").roles("adminn").build());
    }

    @Resource
    private DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()// 开启对http请求的认证
                .anyRequest().authenticated()// 表示所有请求都需要经过认证
                .and() // 返回 HttpSecurity 对象
                .formLogin()// 采用表单登陆（默认表单）
                .and()
                .rememberMe()// 开启记住我功能，该功能需要手动指定UserDetailsService
                .alwaysRemember(true)// 无论前端是都勾选记住我功能，都开启记住我*/
                .userDetailsService(userDetailsService())
                .rememberMeServices(rememberMeServices())
                .and()
                .csrf().disable() // 禁用csrf(跨站请求保护)
        ;
    }

    /**
     * 使用自定义的rememberMeService, 并将生产的token放入内存中
     *
     * PersistentTokenBasedRememberMeServices 持久化token记住我服务，这个service 会在每次请求的时候刷新cookie中remember-me的值
     * @return RememberMeServices
     */
    @Bean
    public RememberMeServices rememberMeServices(){
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return new PersistentTokenBasedRememberMeServices(UUID.randomUUID().toString(),userDetailsService(),tokenRepository);
    }
}
