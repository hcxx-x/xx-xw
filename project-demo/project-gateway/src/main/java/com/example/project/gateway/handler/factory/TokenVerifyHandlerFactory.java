package com.example.project.gateway.handler.factory;


import com.example.project.gateway.handler.ITokenVerifyHandler;
import com.example.project.gateway.handler.instance.v1.TokenVerifyV1Handler;

/**
 * token 校验处理器factory
 */
public class TokenVerifyHandlerFactory {

    /**
     * 获取实例化 ITokenVerifyHandler
     *
     * @param protocolVersion 协议版本号
     * @return
     */
    public static ITokenVerifyHandler instance(String protocolVersion){
        return TokenVerifyV1Handler.instance();
    }
}
