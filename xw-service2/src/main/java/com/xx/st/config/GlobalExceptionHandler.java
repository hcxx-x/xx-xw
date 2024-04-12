package com.xx.st.config;

import com.xx.core.http.HttpR;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.SessionException;



/**
 * @author hanyangyang
 * @since 2024/4/12
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public HttpR handleSessionException(Exception ex){
//        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
//        HttpServletResponse response = ((ServletRequestAttributes) requestAttributes).getResponse();
//        response.setStatus(500);
        return HttpR.fail(500,"系统异常");
    }
}
