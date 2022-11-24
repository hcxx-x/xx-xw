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
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableAuthorizationServer
public class JwtAuthServerConfig extends AuthorizationServerConfigurerAdapter {


    @Resource
    private UserDetailsService userDetailsService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private DataSource dataSource;

    /**
     * 声明客户端服务
     * 注意，这里声明bean的时候名称不能为clientDetailsService 即方法名称不能为clientDetailsService,因为容器中默认存在一个名称为clientDetailsService 的bean
     *
     * @return
     */
    @Bean
    public ClientDetailsService JdbcClientDetailsService() throws Exception {
        return new JdbcClientDetailsServiceBuilder().jdbc().dataSource(dataSource).build();
    }


    /**
     * 配置客户端
     *
     * 用来配置可以给哪些应用授权，相当于是告诉授权服务器可以给哪些客户端进行授权
     * 获取用户授权：http://localhost:8080/oauth/authorize?client_id=client&response_type=code&redirect_uri=https://www.baidu.com
     * 根据用户授权后获取的code获取token /oauth/token 获取token的接口地址
     *
     * @param clients the client details configurer
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // 设置使用什么服务来处理允许授权的客户端数据，这里就是告诉授权服务器通过jdbc的客户端管理方式来进行管理可以被授权的所有的客户端
        clients.withClientDetails(JdbcClientDetailsService());
    }

    /**
     * 配置jwt转换器
     * @return
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        // 设置生成jwt的加密密钥
        jwtAccessTokenConverter.setSigningKey("jwtSecret");
        return jwtAccessTokenConverter;
    }

    /**
     * oath2 授权服务器在生成jwt token的时候需要吧用户的信息放到jwt token负载中，但是用户信息又是一个对象，所以需要在这里配置一下转换器
     * 为什么要使用这么一个转换器？因为jwt token 会包含用户信息，而用户信息是一个对象，所以需要一个转化器将对象转换成一个字符串，然后将字符串放到对应的jwtToken 字符串中
     * @return
     */
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    /**
     * 使用刷新令牌的时候需要指定userDetailService
     * 使用密码模式的时候需要指定全局的authenticationManager，这个authenticationManager 可以在自定义WebSecurityConfigurerAdapter中进行暴露
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.accessTokenConverter(jwtAccessTokenConverter())// 配置jwt转换器
                .authenticationManager(authenticationManager) // 设置authenticationManager
                .tokenStore(tokenStore());  // 设置token 存储方式

    }
}


