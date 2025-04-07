package com.example.project.gateway.exception;


import com.example.project.gateway.enums.GatewayErrorEnum;

/**
 * token校验异常
 */
public class TokenVerifyException extends GatewayBaseException{

    public TokenVerifyException(GatewayErrorEnum err) {
        super(err);
    }
}
