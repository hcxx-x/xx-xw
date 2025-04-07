package com.example.project.gateway.handler;


import com.example.project.gateway.exception.TokenVerifyException;
import org.springframework.web.server.ServerWebExchange;

/**
 * token校验处理器接口
 */
public interface ITokenVerifyHandler {

    /**
     * 校验token合法性
     *
     * @param token 票据
     * @param exchange
     * @throws TokenVerifyException token校验不通过(token为空、不合法、过期等)
     */
    void verify(String token, ServerWebExchange exchange) throws TokenVerifyException;
}
