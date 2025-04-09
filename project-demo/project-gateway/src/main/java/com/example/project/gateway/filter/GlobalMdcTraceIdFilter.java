package com.example.project.gateway.filter;

import cn.hutool.core.util.StrUtil;
import com.example.project.gateway.constant.FilterOrderedConstant;
import com.example.project.gateway.constant.ServerWebExchangeAttributesKeyConstants;
import com.example.project.gateway.constant.ProjectConstants;
import com.example.project.gateway.constant.HttpHeaderConstants;
import com.github.f4b6a3.ulid.Ulid;

import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 添加上下文trace_id,便于根据trace_id跟踪日志
 */
@Component
public class GlobalMdcTraceIdFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 从请求头获取
        final String traceIdInHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaderConstants.X_TID);
        // 如果请求头没有，则生成一个
        final String traceId = StrUtil.isNotBlank(traceIdInHeader) ? traceIdInHeader : Ulid.fast().toString();
        // 放到MDC中
        MDC.put(ProjectConstants.MDC_TRACE_ID, traceId);
        exchange.getAttributes().put(ServerWebExchangeAttributesKeyConstants.TRACE_ID, traceId);
        // 执行过滤器链，并在最后清空MDC
        return chain.filter(exchange).doFinally((signalType)->MDC.remove(ProjectConstants.MDC_TRACE_ID));
    }

    @Override
    public int getOrder() {
        /**
         * Order 最小，最先执行
         */
        return FilterOrderedConstant.GLOBAL_MDC_TRACE_ID_ORDER;
    }
}
