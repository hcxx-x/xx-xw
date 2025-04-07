package com.example.project.gateway.constant;

public class ServerWebExchangeAttributesKeyContants {
    public static final String IS_BOUNDARY = "is_boundary";
    public static final String IS_ALL_NOT_NEED_VERIFY = "is_all_not_need_verify";
    public static final String IS_NEED_VERIFY_MUST_HEADER = "is_need_verify_must_header";

    public static final String IS_NEED_VERIFY_REPLAY_ATACK = "is_need_verify_replay_attack";

    public static final String IS_NEED_VERIFY_TOKEN = "is_need_verify_token";

    public static final String IS_NEED_VERIFY_SIGN_CONSISTENCY = "is_need_verify_sign_consistency";

    /**
     * 是否已经校验了timestamp
     */
    public static final String IS_VERIFIED_TIMESTAMP = "is_verified_timestamp";

    public static final String TRACE_ID = "trace_id";

    /**
     * JWT INFO
     */
    public static final String JWT_INFO = "jwt_info";

    public static final String SIGN_OF_APP_INFO = "sign_of_app_info";


    public static final String GATEWAY_HTTP_RESPONSE_CACHE_KEY = "gateway_http_response_cache_key";

}
