package com.example.project.gateway.handler.instance;


import com.example.project.gateway.exception.ReplayAttackVerifyException;
import com.example.project.gateway.handler.IReplayAttackVerifyHandler;
import org.springframework.web.server.ServerWebExchange;

/**
 * 重放攻击校验
 */
public class DefaultNullReplayAttackVerifyHandler implements IReplayAttackVerifyHandler {

    private static DefaultNullReplayAttackVerifyHandler instance = new DefaultNullReplayAttackVerifyHandler();

    private DefaultNullReplayAttackVerifyHandler(){}

    @Override
    public void verify(String nonce, String timestamp, long timeout,ServerWebExchange exchange) throws ReplayAttackVerifyException {
        return;
    }

    public static IReplayAttackVerifyHandler instance(){
        return instance;
    }
}
