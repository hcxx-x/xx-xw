package com.example.project.gateway.exception;

import static com.example.project.gateway.enums.GatewayErrorEnum.REPLAY_ATTACK_VERIFT_ERROR;

/**
 * 重放攻击异常
 */
public class ReplayAttackVerifyException extends GatewayBaseException{

    public ReplayAttackVerifyException(){
        super(REPLAY_ATTACK_VERIFT_ERROR);
    }
}
