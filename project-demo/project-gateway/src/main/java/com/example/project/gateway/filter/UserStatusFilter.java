package com.example.project.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.example.project.gateway.constant.FilterOrderedConstant.USER_STATUS_FILTER_ORDER;
import static com.example.project.gateway.constant.ServerWebExchangeAttributesKeyConstants.*;


@Component
public class UserStatusFilter implements GlobalFilter, Ordered {

    @Override
    public int getOrder() {
        // 设置拦截器的优先级，数值越小优先级越高
        return USER_STATUS_FILTER_ORDER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 确认需要校验token，并成功(该fiter)
        if(exchange.getAttributeOrDefault(IS_NEED_VERIFY_TOKEN, true)){
            //TODO 校验用户状态
        }

        // Token校验成功，继续处理请求
        return chain.filter(exchange);
    }
}
