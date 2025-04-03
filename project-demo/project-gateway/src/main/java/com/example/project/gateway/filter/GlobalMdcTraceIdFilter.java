package com.example.project.gateway.filter;

import com.github.f4b6a3.ulid.Ulid;
import com.ynby.byuis.common.constants.UisConstants;
import com.ynby.byuis.common.constants.UisHttpHeaderConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.ynby.byuis.gateway.constant.FilterOrderedConstant.GLOBAL_MDC_TRACE_ID_ORDER;
import static com.ynby.byuis.gateway.constant.ServerWebExchangeAttributesKeyContants.TRACE_ID;

@Component
public class GlobalMdcTraceIdFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final String traceIdInHeader = exchange.getRequest().getHeaders().getFirst(UisHttpHeaderConstants.X_TID);
        final String traceId = StringUtils.isNotBlank(traceIdInHeader) ? traceIdInHeader : Ulid.fast().toString();
        MDC.put(UisConstants.MDC_TRACE_ID, traceId);
        exchange.getAttributes().put(TRACE_ID, traceId);
        return chain.filter(exchange).doFinally((signalType)->MDC.remove(UisConstants.MDC_TRACE_ID));
    }

    @Override
    public int getOrder() {
        return GLOBAL_MDC_TRACE_ID_ORDER;
    }
}
