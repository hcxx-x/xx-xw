package com.xx.security.backend.exception;

import org.springframework.security.authentication.AuthenticationServiceException;

import javax.naming.AuthenticationException;

/**
 * @auther: hanyangyang
 * @date: 2022/11/9
 */
public class KaptchaNotMatchException extends AuthenticationServiceException {
    public KaptchaNotMatchException(String msg) {
        super(msg);
    }
}
