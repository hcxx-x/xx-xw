package com.xx.webconfig.base;

import lombok.Data;

/**
 * @author hanyangyang
 * @since 2024/7/1
 */
@Data
public class Result<T> {
    private int code;
    private String msg;
    private T data;



    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<>();
        result.setData(data);
        result.setCode(0);
        return result;
    }
    public static <T> Result<T> ok() {
        Result<T> result = new Result<>();
        result.setCode(0);
        return result;
    }
    public static <T> Result<T> fail() {
        Result<T> result = new Result<>();
        result.setCode(500);
        return result;
    }
    public static <T> Result<T> fail(String msg) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMsg(msg);
        return result;
    }
}
