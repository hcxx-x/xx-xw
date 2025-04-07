package com.example.project.gateway.exception;

import static com.example.project.gateway.enums.GatewayErrorEnum.SIGN_CONSISTENCY_VERIFT_ERROR;

/**
 * 签名一致性校验异常
 */
public class SignConsistencyVerifyException extends GatewayBaseException{

    public SignConsistencyVerifyException(){
        super(SIGN_CONSISTENCY_VERIFT_ERROR);
    }
}
