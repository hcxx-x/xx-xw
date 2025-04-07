package com.example.project.gateway.handler.factory;


import com.example.project.gateway.handler.IReplayAttackVerifyHandler;
import com.example.project.gateway.handler.instance.v1.ReplayAttackVerifyV1Handler;

/**
 * 重放攻击校验处理器factory
 */
public class ReplayAttackVerifyHandlerFactory {
    /**
     * 获取实例化 IReplayAttackVerifyHandler
     *
     * @param protocolVersion 协议版本号
     * @return
     */
    public static IReplayAttackVerifyHandler instance(String protocolVersion){
        return ReplayAttackVerifyV1Handler.instance();
    }
}
