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
        HttpR<T> r = new HttpR<>();
        r.setCode(0);
        r.setMsg("success");
        return r;
    }

    public static <T> HttpR<T> ok(T data) {
        HttpR<T> r = new HttpR<>();
        r.setCode(0);
        r.setMsg("success");
        r.setData(data);
        return r;
    }

    public static <T> HttpR<T> fail(int code, String msg) {
        HttpR<T> r = new HttpR<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }


}
