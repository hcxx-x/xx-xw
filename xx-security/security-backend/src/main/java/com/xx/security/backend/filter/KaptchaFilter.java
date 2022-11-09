package com.xx.security.backend.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.xx.security.backend.exception.KaptchaNotMatchException;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.ObjectUtils;
import sun.misc.IOUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @auther: hanyangyang
 * @date: 2022/11/9
 */
public class KaptchaFilter extends UsernamePasswordAuthenticationFilter {
    public static final String SPRING_SECURITY_FORM_KAPTCHA_KEY = "kaptcha";

    private String kaptchaParameter = SPRING_SECURITY_FORM_KAPTCHA_KEY;

    public final String getKaptchaParameterParameter() {
        return this.kaptchaParameter;
    }

    public void setKaptchaParameter(String kaptchaParameter) {
        this.kaptchaParameter = kaptchaParameter;
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
            String kaptcha = (String) bodyMap.get(getKaptchaParameterParameter());
            HttpSession session = request.getSession();
            String sessionKaptcha = (String) session.getAttribute("kaptcha");
            if (!kaptcha.equalsIgnoreCase(sessionKaptcha)) {
                throw new KaptchaNotMatchException("验证码不匹配");
            }
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
            // Allow subclasses to set the "details" property
            setDetails(request, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);
        }
        return super.attemptAuthentication(request, response);
    }
}
