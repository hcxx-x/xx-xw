package com.xx.security.backend.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

/**
 * @auther: hanyangyang
 * @date: 2022/11/9
 */
public class CustomUsernamePasswordFilter extends UsernamePasswordAuthenticationFilter {

    public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "remember-me";
    private String rememberMeParameter = SPRING_SECURITY_FORM_USERNAME_KEY;

    public String getRememberMeParameter() {
        return rememberMeParameter;
    }

    public void setRememberMeParameter(String rememberMeParameter) {
        this.rememberMeParameter = rememberMeParameter;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        if (request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)) {
                       Map bodyMap = null;
            try {
                String requestBody = request.getReader().readLine();
                bodyMap = new ObjectMapper().readValue(requestBody, Map.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String username = (String) bodyMap.get(getUsernameParameter());
            String password = (String) bodyMap.get(getPasswordParameter());
            String rememberMe = (String) bodyMap.get(getRememberMeParameter());
            if (StringUtils.hasLength(rememberMe)){
                request.setAttribute(this.rememberMeParameter,rememberMe);
            }
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
            // Allow subclasses to set the "details" property
            setDetails(request, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);
        }
        return super.attemptAuthentication(request, response);
    }
}
