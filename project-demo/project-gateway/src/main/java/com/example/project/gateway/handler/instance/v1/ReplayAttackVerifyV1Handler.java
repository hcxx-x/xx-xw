package com.example.project.gateway.handler.instance.v1;


import com.example.project.gateway.constant.HttpHeaderConstants;
import com.example.project.gateway.exception.ReplayAttackVerifyException;
import com.example.project.gateway.handler.IReplayAttackVerifyHandler;
import com.example.project.gateway.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerWebExchange;

import java.util.Objects;

/**
 * 重放攻击校验(v1版)
 */
@Slf4j
public class ReplayAttackVerifyV1Handler implements IReplayAttackVerifyHandler {

    private final static String NONCE_REDIS_KEY_PREFIX = "gw:nonce:";

    private static ReplayAttackVerifyV1Handler instance = new ReplayAttackVerifyV1Handler();

    private ReplayAttackVerifyV1Handler(){}

    @Override
    public void verify(String nonce, String timestamp, long timeout, ServerWebExchange exchange) throws ReplayAttackVerifyException {
        /**
         * 1、判断时间戳是否合法
         * 2、判断nonce是否合法
         * 3、缓存合法的nonce
         */
//        if(exchange.getAttributeOrDefault(IS_VERIFIED_TIMESTAMP, false)){
//            if(!UisBusinessValidateUtil.validateTimestamp(timestamp, timeout)){
//                log.error("replay attack verify fail, request header[timestamp:{}] is illegal ", timestamp);
//                throw new ReplayAttackVerifyException();
//            }
//        }
        if(Objects.isNull(nonce)){
            log.error("replay attack verify fail, request headers['{}'] is null ", HttpHeaderConstants.X_NONCE);
            throw new ReplayAttackVerifyException();
        }
        if(RedisUtils.hasKey(NONCE_REDIS_KEY_PREFIX + nonce)){
            log.error("replay attack verify fail, request headers['{}']=['{}'] not first use in {}ms", HttpHeaderConstants.X_NONCE, nonce, timeout);
            throw new ReplayAttackVerifyException();
        }
        RedisUtils.expire(NONCE_REDIS_KEY_PREFIX + nonce, timeout / 1000);
    }

    public static IReplayAttackVerifyHandler instance(){
        return instance;
    }
}
