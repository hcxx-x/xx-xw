package com.example.project.gateway.handler.factory;


import com.example.project.gateway.handler.ISignConsistencyVerifyHandler;
import com.example.project.gateway.handler.instance.v1.SignConsistencyVerifyV1Handler;
import com.example.project.gateway.handler.instance.v2.SignConsistencyVerifyV2Handler;

/**
 * 签名一致性校验处理器factory
 */
public class SignConsistencyVerifyHandlerFactory {

    /**
     * 获取实例化ISignConsistencyVerifyHandler
     *
     * @param protocolVersion 协议版本号
     * @return
     */
    public static ISignConsistencyVerifyHandler instance(String protocolVersion){
        if("1.0.1".equals(protocolVersion)){
            return SignConsistencyVerifyV2Handler.instance();
        }else{
            return SignConsistencyVerifyV1Handler.instance();
        }
    }
}
