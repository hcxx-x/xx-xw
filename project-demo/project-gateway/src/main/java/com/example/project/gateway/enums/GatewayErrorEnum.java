package com.example.project.gateway.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;

@AllArgsConstructor
@Getter
@FieldNameConstants
public enum GatewayErrorEnum {

    TOKEN_EXPIRED(10401, "抱歉, 当前票据已过期"),

    TOKEN_LOST(10402, "抱歉, 找不到可用票据"),

    TOKEN_ILLEGAL(10403, "危险, 请求信息安全校验非法!"),

    /**
     * 请求地址不存在，即请求不合法
     */
    REQUEST_ILLEGAL(10404, "非法的请求目标地址"),

    /**
     * 服务端异常
     */
    SERVER_UIS_GATEWAY_ERROR(10999, "抱歉, 服务开小差啦"),

    /**
     * 同http code：503 Service Unavailable
     */
    SERVER_UIS_SERVICE_UNAVAILABLE_ERROR(10503, "抱歉, 网络开小差啦, 请稍后重试"),

    /**
     * 网络连接超时
     */
    SERVER_UIS_GATEWAY_TIMEOUT_ERROR(10504, "抱歉, 网络开小差啦, 请稍后重试"),

    /**
     * 签名校验异常
     */
    SIGN_CONSISTENCY_VERIFT_ERROR(10501, "危险, 请求信息安全校验失败!"),


    /**
     * 重放攻击校验异常
     */
    REPLAY_ATTACK_VERIFT_ERROR(10501, "危险, 请求被系统安全机制拦截!"),

    /**
     * 签名、token、用户租户不一致
     */
    SIGN_TOKEN_USER_TENANT_CODE_NOT_EQUEAL_ERROR(10501, "危险, 请求载荷校验不一致!"),

    /**
     * header头字段校验
     */
    MUST_HEADER_VERIFT_ERROR(10501, "危险, 请求头信息安全校验异常!"),


    ;

    private int code;

    private String msg;
}
