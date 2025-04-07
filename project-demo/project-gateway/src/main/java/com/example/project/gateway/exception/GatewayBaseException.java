package com.example.project.gateway.exception;


import com.alibaba.fastjson2.JSONObject;
import com.example.project.gateway.enums.GatewayErrorEnum;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;

/**
 * 网关异常基类
 */
@FieldNameConstants
@Getter
public class GatewayBaseException extends RuntimeException{

    private int code;

    private String msg;

    public GatewayBaseException(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public GatewayBaseException(GatewayErrorEnum err){
        this.code = err.getCode();
        this.msg = err.getMsg();
    }

    public String toResponse(){
        JSONObject response = new JSONObject();
        response.put(Fields.code, code);
        response.put(Fields.msg, msg);
        return response.toJSONString();
    }
}
