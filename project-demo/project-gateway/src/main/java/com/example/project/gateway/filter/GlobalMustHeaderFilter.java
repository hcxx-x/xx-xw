package com.example.project.gateway.filter;


import com.example.project.gateway.constant.HttpHeaderConstants;
import com.example.project.gateway.exception.GlobalMustHeaderException;
import com.example.project.gateway.factory.MustHeaderVerifyHandlerFactory;
import com.example.project.gateway.property.GatewayProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Objects;

import static com.example.project.gateway.constant.FilterOrderedConstant.GLOBAL_MUST_HEADER_FILTER_ORDER;
import static com.example.project.gateway.constant.ServerWebExchangeAttributesKeyContants.*;

/**
 * 是否验证请求头
 */
@ConditionalOnProperty(prefix = "byuis.gateway.verify.must-header", name = "enabled", havingValue = "true", matchIfMissing = true)
@Component
public class GlobalMustHeaderFilter implements GlobalFilter, Ordered {
    @Resource
    private GatewayProperties properties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if(exchange.getAttributeOrDefault(IS_ALL_NOT_NEED_VERIFY, false)){
            return chain.filter(exchange);
        }
        // 读取属性值，根据属性值判断是否需要验证请求头
        if(exchange.getAttributeOrDefault(IS_NEED_VERIFY_MUST_HEADER, true)){
            final String protocolVersion = exchange.getRequest().getHeaders().getFirst(HttpHeaderConstants.X_P_VERSION);
            if(Objects.isNull(protocolVersion)){
                throw new GlobalMustHeaderException(HttpHeaderConstants.X_P_VERSION);
            }
            // 根据协议版本号校验header头
            MustHeaderVerifyHandlerFactory.instance(protocolVersion).verify(properties, exchange);
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return GLOBAL_MUST_HEADER_FILTER_ORDER;
    }
}
