package com.xx.core.http;

import lombok.Data;

/**
 * @author hanyangyang
 * @since 2024/4/12
 */
@Data
public class HttpR<T> {
    private int code;
    private String msg;
    private T data;

    public static <T> HttpR<T> ok() {
        HttpR<T> httpR = new HttpR<>();
        httpR.setCode(0);
        httpR.setMsg("success");
        return httpR;
    }

    public static <T> HttpR<T> ok(T data) {
        HttpR<T> httpR = new HttpR<>();
        httpR.setCode(0);
        httpR.setMsg("success");
        httpR.setData(data);
        return httpR;
    }

    public static <T> HttpR<T> fail(int code, String msg) {
        HttpR<T> httpR = new HttpR<>();
        httpR.setCode(code);
        httpR.setMsg(msg);
        return httpR;
    }


}
