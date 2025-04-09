package com.example.project.gateway.filter;


import com.example.project.gateway.constant.HttpHeaderConstants;
import com.example.project.gateway.handler.factory.TokenVerifyHandlerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.example.project.gateway.constant.FilterOrderedConstant.TOKEN_FILTER_ORDER;
import static com.example.project.gateway.constant.ServerWebExchangeAttributesKeyConstants.*;
/**
 * token拦截器
 */
@Component
public class TokenFilter implements GlobalFilter, Ordered {

    @Override
    public int getOrder() {
        // 设置拦截器的优先级，数值越小优先级越高
        return TOKEN_FILTER_ORDER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if(exchange.getAttributeOrDefault(IS_ALL_NOT_NEED_VERIFY, false)){
            return chain.filter(exchange);
        }
        HttpHeaders headers = exchange.getRequest().getHeaders();
        if(exchange.getAttributeOrDefault(IS_NEED_VERIFY_TOKEN, true)){
            // 从请求头中获取Token
            String token = headers.getFirst(HttpHeaderConstants.X_TOKEN);
            final String protocolVersion = headers.getFirst(HttpHeaderConstants.X_P_VERSION);
            TokenVerifyHandlerFactory.instance(protocolVersion).verify(token, exchange);
        }else if(headers.containsKey(HttpHeaderConstants.X_TOKEN) && headers.containsKey(HttpHeaderConstants.X_P_VERSION)){
            String token = headers.getFirst(HttpHeaderConstants.X_TOKEN);
            final String protocolVersion = headers.getFirst(HttpHeaderConstants.X_P_VERSION);
            try {
                TokenVerifyHandlerFactory.instance(protocolVersion).verify(token, exchange);
            }catch (Throwable ignored){}
        }

        // Token校验成功，继续处理请求
        return chain.filter(exchange);
    }
}
