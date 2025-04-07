package com.example.project.gateway.handler;


import com.example.project.gateway.enums.GatewayErrorEnum;
import com.example.project.gateway.exception.GatewayBaseException;
import com.example.project.gateway.http.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.net.ConnectException;

/**
 *  全局统一异常处理
 */
@Slf4j
public class GlobalExceptionHandler {
    public GlobalExceptionHandler(){}

    /**
     * 统一异常处理入口
     * <br/>
     *
     * @param throwable
     * @return
     */
    public Response<?> response(Throwable throwable){
        if (throwable instanceof GatewayBaseException) {
            return Response.restResult(((GatewayBaseException) throwable).getCode(), ((GatewayBaseException) throwable).getMsg());
        }

        if(throwable instanceof ResponseStatusException){
            return response((ResponseStatusException) throwable);
        }

        if(throwable instanceof ConnectException){
            return Response.restResult(GatewayErrorEnum.SERVER_UIS_GATEWAY_TIMEOUT_ERROR.getCode(), GatewayErrorEnum.SERVER_UIS_GATEWAY_TIMEOUT_ERROR.getMsg());
        }
        return Response.restResult(GatewayErrorEnum.SERVER_UIS_GATEWAY_ERROR.getCode(), GatewayErrorEnum.SERVER_UIS_GATEWAY_ERROR.getMsg());
    }

    /**
     * 统一封装：ResponseStatusException
     *
     * @param responseStatusException
     * @return
     */
    private Response<?> response(ResponseStatusException responseStatusException) {
        switch (responseStatusException.getStatus()){
            case NOT_FOUND:
                return Response.restResult(GatewayErrorEnum.REQUEST_ILLEGAL.getCode(), GatewayErrorEnum.REQUEST_ILLEGAL.getMsg());
            case GATEWAY_TIMEOUT:
                return Response.restResult(GatewayErrorEnum.SERVER_UIS_GATEWAY_TIMEOUT_ERROR.getCode(), GatewayErrorEnum.SERVER_UIS_GATEWAY_TIMEOUT_ERROR.getMsg());
            case SERVICE_UNAVAILABLE:
                return Response.restResult(GatewayErrorEnum.SERVER_UIS_SERVICE_UNAVAILABLE_ERROR.getCode(), GatewayErrorEnum.SERVER_UIS_SERVICE_UNAVAILABLE_ERROR.getMsg());
            default:
                return Response.restResult(GatewayErrorEnum.SERVER_UIS_GATEWAY_ERROR.getCode(), GatewayErrorEnum.SERVER_UIS_GATEWAY_ERROR.getMsg());
        }
    }
}
