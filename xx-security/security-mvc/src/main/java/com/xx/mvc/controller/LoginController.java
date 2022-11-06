package com.xx.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Controller
public class LoginController {
    @GetMapping("/login.html")
    public String login(HttpServletRequest request){
        Object springSecurityLastException = request.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
        return "login";
    }

    @PostMapping("/loginFail")
    public String loginFail(){
        return "login_error";
    }

}
