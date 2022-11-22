package com.xx.security.oauth2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

/**
 * @auther: hanyangyang
 * @date: 2022/11/22
 */
@Configuration
@EnableAuthorizationServer// 指定当前服务为授权服务
public class CustomAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    /**
     * 用来配置可以给哪些应用授权
     * 获取用户授权：http://localhost:8080/oauth/authorize?client_id=client&response_type=code&redirect_uri=https://www.baidu.com
     * 根据用户授权后获取的code获取token /oauth/token 获取token的接口地址
     * @param clients the client details configurer
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory().
                withClient("client")
                .secret("{noop}secret")
                .redirectUris("https://www.baidu.com")
                .authorizedGrantTypes("authorization_code") // 授权码模式
                .scopes("userInfo")
        ;
    }
}
