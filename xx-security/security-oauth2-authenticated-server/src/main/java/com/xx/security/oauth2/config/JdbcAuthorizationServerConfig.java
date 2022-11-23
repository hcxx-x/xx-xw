package com.xx.security.oauth2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.builders.JdbcClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

/**
 * @auther: hanyangyang
 * @date: 2022/11/22
 */
@Configuration
@EnableAuthorizationServer// 指定当前服务为授权服务
public class JdbcAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Resource
    private UserDetailsService userDetailsService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private DataSource dataSource;

    /**
     * 注意，这里声明bean的时候名称不能为clientDetailsService 即方法名称不能为clientDetailsService,因为容器中默认存在一个名称为clientDetailsService 的bean
     *
     * @return
     */
    @Bean
    public ClientDetailsService JdbcClientDetailsService() throws Exception {
        return new JdbcClientDetailsServiceBuilder().jdbc().dataSource(dataSource).build();
    }


    /**
     * 用来配置可以给哪些应用授权
     * 获取用户授权：http://localhost:8080/oauth/authorize?client_id=client&response_type=code&redirect_uri=https://www.baidu.com
     * 根据用户授权后获取的code获取token /oauth/token 获取token的接口地址
     *
     * @param clients the client details configurer
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
       clients.withClientDetails(JdbcClientDetailsService());
    }


    @Bean
    public TokenStore tokenStore(){
        return new JdbcTokenStore(dataSource);
    }

    /**
     * 使用刷新令牌的时候需要指定userDetailService
     * 使用密码模式的时候需要指定全局的authenticationManager，这个authenticationManager 可以在自定义WebSecurityConfigurerAdapter中进行暴露
     */

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.userDetailsService(userDetailsService) // 设置userDetailService
                .authenticationManager(authenticationManager) // 设置authenticationManager
                .tokenStore(tokenStore());  // 设置token 存储方式

        // 配置tokenService参数 修改默认令牌生成服务配置
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        // 配置基于数据库令牌生成
        tokenServices.setTokenStore(endpoints.getTokenStore());
        // 配置令牌增强策略（如没有增强是一个字符串，增强后存在数据库中就是一段二进制数据）
        tokenServices.setTokenEnhancer(endpoints.getTokenEnhancer());
        // 配置是否支持刷新令牌
        tokenServices.setSupportRefreshToken(true);
        // 配置是否重复使用刷新令牌，当为true的时候，使用刷新令牌获取access_token后刷新令牌不变
        tokenServices.setReuseRefreshToken(true);
        // 设置access_token 过期时间 7天
        tokenServices.setAccessTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(7));
        // 设置refresh_token 过期时间 30天
        tokenServices.setRefreshTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(30));
        // 设置客户端服务
        tokenServices.setClientDetailsService(endpoints.getClientDetailsService());
        // 使用自定义的令牌服务
        endpoints.tokenServices(tokenServices);
    }
}
