package com.xx.security.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义失败之后的处理
 */
public class CustomAuthenticationFailHandler implements AuthenticationFailureHandler {
       @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        Map<String, Object> result = new HashMap<>();
        result.put("msg",exception.getMessage());
        result.put("status",500);
        response.setContentType("application/json;charset=UTF-8");
        String resultJosnStr = new ObjectMapper().writeValueAsString(result);
        response.getWriter().println(resultJosnStr);
    }
}
