package com.example.project.gateway.factory;


import com.example.project.gateway.exception.GlobalMustHeaderException;
import com.example.project.gateway.property.GatewayProperties;
import org.springframework.web.server.ServerWebExchange;

/**
 * token校验处理器接口
 */
public interface IMustHeaderVerifyHandler {

    /**
     * 校验header字段
     *
     * @param exchange
     * @throws GlobalMustHeaderException header校验不通过
     */
    void verify(GatewayProperties properties, ServerWebExchange exchange) throws GlobalMustHeaderException;
}
