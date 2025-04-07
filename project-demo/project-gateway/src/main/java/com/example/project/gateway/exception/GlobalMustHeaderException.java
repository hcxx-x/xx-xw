package com.example.project.gateway.exception;

import lombok.extern.slf4j.Slf4j;

import static com.example.project.gateway.enums.GatewayErrorEnum.MUST_HEADER_VERIFT_ERROR;

@Slf4j
public class GlobalMustHeaderException extends GatewayBaseException {
    public GlobalMustHeaderException(String headerName){
        super(MUST_HEADER_VERIFT_ERROR);
        log.error("MUST_HEADER_VERIFT_ERROR Err: {}", headerName);
    }
}
