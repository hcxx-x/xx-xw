package com.example.project.gateway.handler.instance;


import com.example.project.gateway.exception.TokenVerifyException;
import com.example.project.gateway.handler.ITokenVerifyHandler;
import org.springframework.web.server.ServerWebExchange;

/**
 * token 校验
 */
public class DefaultNullTokenVerifyHandler implements ITokenVerifyHandler {
    private static DefaultNullTokenVerifyHandler instance = new DefaultNullTokenVerifyHandler();

    private DefaultNullTokenVerifyHandler(){}

    @Override
    public void verify(String token, ServerWebExchange exchange) throws TokenVerifyException {
        return;
    }

    public static DefaultNullTokenVerifyHandler instance(){
        return instance;
    }
}
