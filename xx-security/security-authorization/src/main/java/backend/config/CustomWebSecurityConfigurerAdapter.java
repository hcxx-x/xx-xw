package backend.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;


import javax.annotation.Resource;

/**
 * 自定义WebSecurity配置
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled=true,jsr250Enabled = true)
public class CustomWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {


    @Bean
    public UserDetailsService userDetailsService(){
        InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
        inMemoryUserDetailsManager.createUser(User.builder().username("admin").password("{noop}123").roles("ROLE_ADMIN").build());
        inMemoryUserDetailsManager.createUser(User.builder().username("user").password("{noop}123").roles("ROLE_USER").build());
        inMemoryUserDetailsManager.createUser(User.builder().username("employee").password("{noop}123").roles("ROLE_EMPLOYEE").build());
        inMemoryUserDetailsManager.createUser(User.builder().username("auth").password("{noop}123").authorities("AUTH").build());
        return inMemoryUserDetailsManager;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()// 开启对http请求的认证
                .mvcMatchers("/role_admin").hasRole("ROLE_ADMIN")// 拥有role_admin角色的用户可以访问/role_admin
                .mvcMatchers("/role_employee").hasRole("ROLE_EMPLOYEE")// 拥有role_employee角色的用户可以访问/role_employee
                .mvcMatchers("/auth").hasAuthority("AUTH")// 拥有auth权限的用户可以访问/auth
                .anyRequest().authenticated()// 表示所有请求都需要经过认证
                .and() // 返回 HttpSecurity 对象
                .formLogin()// 采用表单登陆（默认表单）
                .and()
                .csrf().disable() // 禁用csrf(跨站请求保护)
        ;

    }

}
