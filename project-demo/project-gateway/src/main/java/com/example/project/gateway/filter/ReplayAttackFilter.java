package com.example.project.gateway.filter;

import com.example.project.gateway.constant.HttpHeaderConstants;
import com.example.project.gateway.constant.ServerWebExchangeAttributesKeyConstants;
import com.example.project.gateway.handler.factory.ReplayAttackVerifyHandlerFactory;
import com.example.project.gateway.property.SystemProperties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

import static com.example.project.gateway.constant.FilterOrderedConstant.REPLAY_ATTACK_FILTER_ORDER;
import static com.example.project.gateway.constant.ServerWebExchangeAttributesKeyConstants.*;


/**
 * 重放攻击拦截
 */
@ConditionalOnProperty(prefix = "system.verify.replay-attack", name = "enabled", havingValue = "true", matchIfMissing = true)
@Component
public class ReplayAttackFilter implements GlobalFilter, Ordered {
    @Resource
    private SystemProperties properties;
    @Override
    public int getOrder() {
        return REPLAY_ATTACK_FILTER_ORDER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if(exchange.getAttributeOrDefault(ServerWebExchangeAttributesKeyConstants.IS_ALL_NOT_NEED_VERIFY, false)){
            return chain.filter(exchange);
        }
        if(exchange.getAttributeOrDefault(IS_NEED_VERIFY_REPLAY_ATACK, true)){
            final String protocolVersion = exchange.getRequest().getHeaders().getFirst(HttpHeaderConstants.X_P_VERSION);
            final String nonce = exchange.getRequest().getHeaders().getFirst(HttpHeaderConstants.X_NONCE);
            final String timestamp = exchange.getRequest().getHeaders().getFirst(HttpHeaderConstants.X_TIMESTAMP);
            //重放攻击校验逻辑
            ReplayAttackVerifyHandlerFactory.instance(protocolVersion).verify(nonce, timestamp, properties.getTimestampTimeout(), exchange);
        }

        return chain.filter(exchange);
    }
}
