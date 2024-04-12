package com.xx.st.config;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.websocket.SessionException;

/**
 * @author hanyangyang
 * @since 2024/4/12
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleSessionException(Exception ex){
        return "系统异常";
    }
}
