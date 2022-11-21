package com.security.oauth2.github.controller;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @auther: hanyangyang
 * @date: 2022/11/21
 */
@RestController
public class UserController {

    @GetMapping("/userInfo")
    public Object userInfo(OAuth2AuthenticationToken token){
        return token.getPrincipal();
    }

    @GetMapping("/userInfo2")
    public DefaultOAuth2User userInfo2(){
        return (DefaultOAuth2User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
