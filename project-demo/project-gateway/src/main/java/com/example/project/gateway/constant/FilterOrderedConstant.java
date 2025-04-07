package com.example.project.gateway.constant;

import org.springframework.core.Ordered;

public interface FilterOrderedConstant extends Ordered {
    /**
     * global trace id
     */
    int GLOBAL_MDC_TRACE_ID_ORDER = Ordered.HIGHEST_PRECEDENCE;

    /**
     * http wrapper
     */
    int GLOBAL_HTTP_WRAPPER = GLOBAL_MDC_TRACE_ID_ORDER + 1;

    /**
     * white url filter
     */
    int WHITE_URL_FILTER_ORDER = GLOBAL_HTTP_WRAPPER + 1;

    /**
     * global must header filter
     */
    int GLOBAL_MUST_HEADER_FILTER_ORDER = WHITE_URL_FILTER_ORDER + 2;


    /**
     * replay attack filter
     */
    int REPLAY_ATTACK_FILTER_ORDER = WHITE_URL_FILTER_ORDER + 3;


    /**
     * sign consistency filter
     */
    int SIGN_CONSISTENCY_FILTER_ORDER = WHITE_URL_FILTER_ORDER + 4;

    /**
     * token filter
     */
    int TOKEN_FILTER_ORDER = WHITE_URL_FILTER_ORDER + 5;


    /**
     * user status filter
     */
    int USER_STATUS_FILTER_ORDER = TOKEN_FILTER_ORDER + 1;

    /**
     * 系统链路header处理
     */
    int UIS_CHAIN_FILTER_ORDER = TOKEN_FILTER_ORDER + 10;

    /**
     * 系统链路header处理
     */
    int TOKEN_POSTPROCESSING_FILTER_ORDER = TOKEN_FILTER_ORDER + 100;

}
