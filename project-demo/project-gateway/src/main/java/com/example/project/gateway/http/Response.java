package com.example.project.gateway.http;

import com.example.project.gateway.enums.ResponseEnum;
import lombok.Data;

/**
 * @author hanyangyang
 * @since 2025/4/7
 */
@Data
public class Response<T> {
    private int code;
    private String msg;
    private T data;




    public static <T> Response<T> success(T data) {
        return restResult(data, ResponseEnum.SUCCESS, "success");
    }

    public static <T> Response<T> success(T data, String msg) {
        return restResult(data, ResponseEnum.SUCCESS, msg);
    }





    public static <T> Response<T> fail(T data, int code, String msg) {
        return restResult(data, code, msg);
    }

    public static <T> Response<T> fail(T data, ResponseEnum responseEnum) {
        return restResult(data, responseEnum.getCode(), responseEnum.getMessage());
    }



    public static <T> Response<T> restResult(T data, int code, String msg) {
        Response<T> apiResult = new Response<T>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }

    public static <T> Response<T> restResult(int code, String msg) {
        Response<T> apiResult = new Response<T>();
        apiResult.setCode(code);
        apiResult.setData(null);
        apiResult.setMsg(msg);
        return apiResult;
    }

    public Response<T> successWithData(T data) {
        this.data = data;
        this.code = ResponseEnum.SUCCESS;
        this.msg = "success";
        return this;
    }

    public Response<T> failWithData(T data, int code, String msg) {
        this.data = data;
        this.code = code;
        this.msg = msg;
        return this;
    }
}
