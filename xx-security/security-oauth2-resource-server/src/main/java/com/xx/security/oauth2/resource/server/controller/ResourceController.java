package com.xx.security.oauth2.resource.server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模拟资源请求
 *
 * 请求这个资源的时候必须要要经过认证，或者携带授权服务器办法的访问令牌才可以
 * 访问令牌的需要放在header中，对应的形式为 Authorization: Bearer access_token
 */
@RestController
public class ResourceController {

    @GetMapping
    public String getResource(){
        return "resource";
    }
}
