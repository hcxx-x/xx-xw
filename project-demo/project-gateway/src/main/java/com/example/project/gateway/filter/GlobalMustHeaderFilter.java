package com.example.project.gateway.filter;

import com.ynby.byuis.common.constants.UisHttpHeaderConstants;
import com.ynby.byuis.gateway.ex.GlobalMustHeaderException;
import com.ynby.byuis.gateway.handler.factory.MustHeaderVerifyHandlerFactory;
import com.ynby.byuis.gateway.property.ByuisGatewayProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Objects;

import static com.ynby.byuis.gateway.constant.FilterOrderedConstant.GLOBAL_MUST_HEADER_FILTER_ORDER;
import static com.ynby.byuis.gateway.constant.ServerWebExchangeAttributesKeyContants.*;

@ConditionalOnProperty(prefix = "byuis.gateway.verify.must-header", name = "enabled", havingValue = "true", matchIfMissing = true)
@Component
public class GlobalMustHeaderFilter implements GlobalFilter, Ordered {
    @Resource
    private ByuisGatewayProperties properties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if(exchange.getAttributeOrDefault(IS_ALL_NOT_NEED_VERIFY, false)){
            return chain.filter(exchange);
        }
        if(exchange.getAttributeOrDefault(IS_NEED_VERIFY_MUST_HEADER, true)){
            final String protocolVersion = exchange.getRequest().getHeaders().getFirst(UisHttpHeaderConstants.X_P_VERSION);
            if(Objects.isNull(protocolVersion)){
                throw new GlobalMustHeaderException(UisHttpHeaderConstants.X_P_VERSION);
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
