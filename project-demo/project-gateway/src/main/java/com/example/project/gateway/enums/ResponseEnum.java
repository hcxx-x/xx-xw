package com.example.project.gateway.enums;

/**
 * @author hanyangyang
 * @since 2025/4/7
 */
public class ResponseEnum {
    public static final int ERROR_NEED_LOGIN = 401;
    public static final int ERROR_TOKEN_EXPIRED = 402;
    public static final int ERROR_SIGN = 403;
    public static final int ERROR_ACCOUNT = 404;
    public static final int WX_TOKEN_EXPIRED_OR_INVALID = 405;
    public static final int SERVER_ERR = 500;
    public static final int METHOD_NOT_ALLOWED = 501;
    public static final int REQUEST_ERR = 502;
    public static final int REQUEST_PARAM_CHECK_WARN = 503;
    public static final int REQUEST_PARAM_CHECK_ERR = 504;
    public static final int REQUEST_PARAM_BIND_ERR = 505;
    private final Integer code;
    private final String message;
    public static final Integer SUCCESS = 0;
    public static final Integer FAIL = -1;
    public static final Integer LIMIT_ERROR = 503999;

    public ResponseEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
