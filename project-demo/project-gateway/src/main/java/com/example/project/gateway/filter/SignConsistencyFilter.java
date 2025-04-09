package com.example.project.gateway.filter;

import com.example.project.gateway.constant.HttpHeaderConstants;
import com.example.project.gateway.handler.factory.SignConsistencyVerifyHandlerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.example.project.gateway.constant.FilterOrderedConstant.SIGN_CONSISTENCY_FILTER_ORDER;
import static com.example.project.gateway.constant.ServerWebExchangeAttributesKeyConstants.*;

/**
 * 签名一致性校验
 */
@Slf4j
@ConditionalOnProperty(prefix = "system.verify.sign-consistency", name = "enabled", havingValue = "true", matchIfMissing = true)
@Component
public class SignConsistencyFilter implements GlobalFilter, Ordered {
    

    @Override
    public int getOrder() {
        return SIGN_CONSISTENCY_FILTER_ORDER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if(exchange.getAttributeOrDefault(IS_ALL_NOT_NEED_VERIFY, false) || exchange.getAttributeOrDefault(IS_BOUNDARY, false)){
            return chain.filter(exchange);
        }
        if(exchange.getAttributeOrDefault(IS_NEED_VERIFY_SIGN_CONSISTENCY, true)){
            final String protocolVersion = exchange.getRequest().getHeaders().getFirst(HttpHeaderConstants.X_P_VERSION);
            final String sign = exchange.getRequest().getHeaders().getFirst(HttpHeaderConstants.X_SIGN);
            final String accessKey = exchange.getRequest().getHeaders().getFirst(HttpHeaderConstants.X_ACCESS_KEY);
            final String timestamp = exchange.getRequest().getHeaders().getFirst(HttpHeaderConstants.X_TIMESTAMP);

            exchange.getAttributes().put(SIGN_OF_APP_INFO, "");
            SignConsistencyVerifyHandlerFactory.instance(protocolVersion).verify(sign, timestamp, accessKey, exchange);
        }

        return chain.filter(exchange);
    }
}
