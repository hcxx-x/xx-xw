package com.xx.security.backend.config;

import com.xx.security.backend.filter.KaptchaFilter;
import com.xx.security.backend.entity.CustomUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;
import java.util.List;

/**
 * 自定义WebSecurity配置
 */
@Configuration
public class CustomWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Bean
    public UserDetailsService userDetailsService(){
        return new UserDetailsService(){
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                List<CustomUser> customUsers = CustomUser.mockData();
                for (CustomUser customUser : customUsers) {
                    if (customUser.getUsername().equals(username)){
                        return customUser;
                    }
                }
                throw new UsernameNotFoundException("用户不存在");
            }
        };
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
    public KaptchaFilter kaptchaFilter() throws Exception {
        KaptchaFilter kaptchaFilter = new KaptchaFilter();
        // 后面需要使用这个自定义的filter 替换UsernamePasswordAuthenticationFilter，
        // 所以原来在UsernamePasswordAuthenticationFilter中的相关配置就可以移动到这里来进行设置了，比如下面这些
        kaptchaFilter.setUsernameParameter("username");
        kaptchaFilter.setPasswordParameter("password");
        kaptchaFilter.setKaptchaParameter("kaptcha");
        kaptchaFilter.setAuthenticationManager(authenticationManagerBean());
        return kaptchaFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()// 开启对http请求的认证
                .mvcMatchers("/allow","/kaptcha/getImage").permitAll()// 放行"/allow"请求，放行的请求需要在anyRequest().authenticated()之前配置
                .anyRequest().authenticated()// 表示所有请求都需要经过认证
                .and() // 返回 HttpSecurity 对象
                .formLogin()// 采用表单登陆（默认表单）
                .successHandler(new CustomAuthenticationSuccessHandler())//处理前后端分离项目需要返回json数据的场景
                .failureHandler(new CustomAuthenticationFailHandler())
                .and()
                .logout().logoutSuccessHandler(new CustomLogoutSuccessHandler())
                .and()
                .csrf().disable() // 禁用csrf(跨站请求保护)
        ;

        http.addFilterAt(kaptchaFilter(), UsernamePasswordAuthenticationFilter.class);
    }

}
