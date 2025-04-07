package com.example.project.gateway.handler.factory;


import com.example.project.gateway.handler.IMustHeaderVerifyHandler;
import com.example.project.gateway.handler.instance.v1.MustHeaderVerifyV1Handler;

/**
 * token 校验处理器factory
 */
public class MustHeaderVerifyHandlerFactory {

    /**
     * 获取实例化 IMustHeaderVerifyHandler
     *
     * @param protocolVersion 协议版本号
     * @return
     */
    public static IMustHeaderVerifyHandler instance(String protocolVersion){
        return MustHeaderVerifyV1Handler.instance();
    }
}
