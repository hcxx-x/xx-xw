package com.example.project.gateway.handler.instance;


import com.example.project.gateway.exception.SignConsistencyVerifyException;
import com.example.project.gateway.handler.ISignConsistencyVerifyHandler;
import org.springframework.web.server.ServerWebExchange;

public class DefaultNullSignConsistencyVerifyHandler implements ISignConsistencyVerifyHandler {

    private static DefaultNullSignConsistencyVerifyHandler instance = new DefaultNullSignConsistencyVerifyHandler();

    private DefaultNullSignConsistencyVerifyHandler(){}

    public static ISignConsistencyVerifyHandler instance() {
        return instance;
    }

    @Override
    public void verify(String sign, String timestamp, String accessKey, ServerWebExchange exchange) throws SignConsistencyVerifyException {

    }
}
