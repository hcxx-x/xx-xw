package com.xx.security.lastversion.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @auther: hanyangyang
 * @date: 2022/11/25
 *
 * 在springboot的2.7版本甚至是2.6.x 靠后的版本中，引入的spring security 依赖就已经将WebSecurityConfigurerAdapter标识为过时的了
 * 那么在新版本中要如何使用呢？
 *
 * 查看新版的WebSecurityConfigurerAdapter源代码，发现注释中有这么一句话
 *
 * Use a org.springframework.security.web.SecurityFilterChain Bean to configure HttpSecurity
 * or a WebSecurityCustomizer Bean to configure WebSecurity
 *
 * 翻译一下就是
 * 可以使用自定义SecurityFilterChain Bean的形式来代替 WebSecurityConfigurerAdapter 中配置HttpSecurity的方法 即用来代替 configure(HttpSecurity http)
 * 可以使用自定义 WebSecurityCustomizer Bean的形式来代替 WebSecurityConfigurerAdapter中配置WebSecurity的方法 即用来代替 configure(WebSecurity web)
 *
 * 下面来看一下怎么使用吧
 *
 *
 */
@Configuration
public class SecurityConfig{

    /**
     * 自定义UserDetailService 来管理系统用户
     * @return
     */
    @Bean
    public UserDetailsService userDetailsService(){
        InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
        inMemoryUserDetailsManager.createUser(User.builder().username("admin").password("{noop}admin").roles("admin").build());
        return inMemoryUserDetailsManager;
    }

    /**
     * 如果想要放行一些资源的话可以在这里配置
     * @return
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return new WebSecurityCustomizer() {
            @Override
            public void customize(WebSecurity web) {
                web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
            }
        };
    }

    /**
     * 这种就属于完全自定义securityFilterChain的方式了，如果想要实现个性化的配置稍稍有点复杂
     * 并且如果想要放行一些特定的资源的可能还需要通过上面自定义 WebSecurityCustomizer Bean的方式实现
     * @return SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(){
        RequestMatcher requestMatcher = new AntPathRequestMatcher("/**");
        List<Filter> filters = new ArrayList<>();
        // 在各个Filter中进行自定义设置，比如下面的登陆页面的设置，默认的登陆url是/login，下面通过自定义的形式将登陆的url修改为/toLogin
        DefaultLoginPageGeneratingFilter defaultLoginPageGeneratingFilter = new DefaultLoginPageGeneratingFilter();
        defaultLoginPageGeneratingFilter.setLoginPageUrl("/toLogin");
        filters.add(defaultLoginPageGeneratingFilter);
        return new DefaultSecurityFilterChain(requestMatcher,filters);
    }

    //如果想好看
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/login").permitAll() // 放行/login请求
                .antMatchers("/users/**", "/settings/**")
                .hasAuthority("Admin")  // 拥有admin权限可以访问 "/users/**", "/settings/**"
                //.hasAnyAuthority("Admin", "Editor", "Salesperson", "Shipper") //拥有下面的任意一种权限即可
                .anyRequest().authenticated()// 其余所有请求均需认证后访问
                .and()
                .formLogin()
                .loginPage("/login") // mvc 模式自定义登陆页面
                .usernameParameter("email") // 自定义登陆是用户名对应的参数
                .permitAll()
                .and()
                .rememberMe().key("AbcdEfghIjklmNopQrsTuvXyz_0123456789")
                .and()
                .logout().permitAll();

        http.headers().frameOptions().sameOrigin();
        return http.build();
    }

}
