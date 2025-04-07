package com.example.project.gateway.handler;


import com.example.project.gateway.exception.ReplayAttackVerifyException;
import org.springframework.web.server.ServerWebExchange;

/**
 * 重放攻击校验处理器接口
 */
public interface IReplayAttackVerifyHandler {

    /**
     * 校验是否为重放攻击(是则抛出异常)
     *
     * @param nonce
     * @param timestamp
     * @param exchange
     * @throws ReplayAttackVerifyException 判断本次请求为重放攻击
     */
    void verify(String nonce, String timestamp, long timeout, ServerWebExchange exchange) throws ReplayAttackVerifyException;
}
