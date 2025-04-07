package com.example.project.gateway.enums;

/**
 * 错误码枚举
 */
public enum ErrorCodeEnum {

    USER_NOT_EXIST(12101,"账号不存在"),

    UNKNOWN_ERROR(12000,"请求失败, 请稍后重试"),
    USER_INFO_WRONG(12001,"账号或者密码错误"),
    TOKEN_EXPIRE(12002,"token过期"),
    TOKEN_SIGN(12003,"token签名不合法"),
    CHANNEL_TYPE_ILLEGAL(12006,"渠道机构非法"),
    USER_NO_RELATE_FACTORY(12007,"用户未关联到工厂"),
    DEALER_EMPLOYEE_ALREADY_EXIST(12008,"用户之前已经注册，请直接登录"),

    APP_INFO_NOT_EXIST(12109,"App应用不存在"),
    USER_INFO_CONFLICT(12112, "用户基本信息冲突"),

    ADD_USER_ACTION_TYPE_ERR(12113, "添加用户识别，行为类型非法，请联系管理员"),
    USER_NOT_RELATE_USER_IDENTITY(12114, "当前用户未分配目标账号类型"),

    USER_EFFECT_FAILED(12115, "启用账户失败，请先配置角色"),
    USER_STATUS_FORBIDDEN(12116, "账号被禁用，请联系管理员"),
    TENANT_SOURCE_CODE_NOT_EXIST(12117, "目标来源code未关联租户"),
    PARENT_TENANT_CODE_NOT_EXIST(12118, "父租户不存在"),

    USER_NOT_ANY_ROLE(12117, "账号未配置任何权限，请先配置角色"),

    ;

    private int errorCode;

    private String desc;

    public int getErrorCode() {
        return errorCode;
    }

    public String getDesc() {
        return desc;
    }

    ErrorCodeEnum(int errorCode, String desc){
        this.errorCode = errorCode;
        this.desc = desc;
    }


}
