/*
package com.xx.security.oauth2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;

import javax.annotation.Resource;

*/
/**
 * @auther: hanyangyang
 * @date: 2022/11/22
 *//*

@Configuration
@EnableAuthorizationServer// 指定当前服务为授权服务
public class CustomAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Resource
    private UserDetailsService userDetailsService;

    @Resource
    private AuthenticationManager authenticationManager;
    */
/**
     * 用来配置可以给哪些应用授权
     * 获取用户授权：http://localhost:8080/oauth/authorize?client_id=client&response_type=code&redirect_uri=https://www.baidu.com
     * 根据用户授权后获取的code获取token /oauth/token 获取token的接口地址
     * @param clients the client details configurer
     * @throws Exception
     *//*

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory().
                withClient("client")
                .secret("{noop}secret")
                .redirectUris("https://www.baidu.com")
                .authorizedGrantTypes("authorization_code","refresh_token","implicit","password","client_credentials") // 授权码模式、刷新令牌、简化模式、密码模式、客户端凭证模式
                .scopes("userInfo")
        ;
    }

    */
/**
     *  使用刷新令牌的时候需要指定userDetailService
     *  使用密码模式的时候需要指定全局的authenticationManager，这个authenticationManager 可以在自定义WebSecurityConfigurerAdapter中进行暴露
     *//*


    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.userDetailsService(userDetailsService).authenticationManager(authenticationManager);
    }
}
*/
