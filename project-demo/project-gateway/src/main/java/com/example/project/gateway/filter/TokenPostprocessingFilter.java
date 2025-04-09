package com.example.project.gateway.filter;

import cn.hutool.core.text.CharSequenceUtil;

import com.example.project.gateway.constant.HttpHeaderConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

import static com.example.project.gateway.constant.FilterOrderedConstant.TOKEN_POSTPROCESSING_FILTER_ORDER;

/**
 * token后处理
 **/
@Slf4j
@Component
public class TokenPostprocessingFilter implements GlobalFilter, Ordered {

    @Override
    public int getOrder() {
        return TOKEN_POSTPROCESSING_FILTER_ORDER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        if(headers.containsKey(HttpHeaderConstants.X_TENANT_CODE)){
            String childTenantCode = headers.getFirst(HttpHeaderConstants.X_TENANT_CODE);
            String tenantCode = headers.getFirst(HttpHeaderConstants.X_TENANT_CODE);
            // 父子租户一致
            if(CharSequenceUtil.equals(childTenantCode, tenantCode)){
                //不做处理
                return chain.filter(exchange);
            }
            Consumer<HttpHeaders> httpHeadersConsumer = httpHeaders -> {
                httpHeaders.set(HttpHeaderConstants.X_TENANT_CODE, childTenantCode);
                httpHeaders.set(HttpHeaderConstants.X_TENANT_CODE, tenantCode);

            };
            ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate().headers(httpHeadersConsumer).build();
            ServerWebExchange build = exchange.mutate().request(serverHttpRequest).build();
            return chain.filter(build);
        }
        return chain.filter(exchange);
    }
}
