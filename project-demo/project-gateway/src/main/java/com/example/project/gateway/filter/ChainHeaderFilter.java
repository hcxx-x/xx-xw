package com.example.project.gateway.filter;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;

import com.example.project.gateway.constant.HttpHeaderConstants;
import com.example.project.gateway.constant.ProjectConstants;
import com.example.project.gateway.constant.ServerWebExchangeAttributesKeyConstants;
import com.example.project.gateway.utils.HttpUtils;
import com.example.project.gateway.utils.JwtUserVO;
import com.example.project.gateway.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Consumer;

import static com.example.project.gateway.constant.FilterOrderedConstant.UIS_CHAIN_FILTER_ORDER;

/**
 * 日志链路追踪
 */
@Slf4j
@Configuration
public class ChainHeaderFilter implements GlobalFilter, Ordered {

    @Override
    public int getOrder() {
        return UIS_CHAIN_FILTER_ORDER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final String traceId = getTraceId(exchange);

        final JwtUserVO jwtUser = exchange.getAttribute(ServerWebExchangeAttributesKeyConstants.JWT_INFO);
        
        Consumer<HttpHeaders> httpHeadersConsumer = httpHeaders -> {
            if(Objects.nonNull(jwtUser)){
                if(Objects.nonNull(jwtUser.getTenantCode())){
                    httpHeaders.set(HttpHeaderConstants.X_TENANT_CODE, jwtUser.getTenantCode());
                }
                httpHeaders.set(HttpHeaderConstants.X_USER_ID, jwtUser.getId());
                if(Objects.nonNull(jwtUser.getUserType())){
                    httpHeaders.set(HttpHeaderConstants.X_USER_TYPE, jwtUser.getUserType());
                }
            }
            httpHeaders.set(HttpHeaderConstants.X_TID, traceId);
            httpHeaders.set(HttpHeaderConstants.X_CLIENT_IP, HttpUtils.getIpAddress(exchange.getRequest()));

        };
        ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate().headers(httpHeadersConsumer).build();
        ServerWebExchange build = exchange.mutate().request(serverHttpRequest).build();

        return chain.filter(build);
    }


    private static String getTraceId(ServerWebExchange exchange) {
        String traceId = MDC.get(ProjectConstants.MDC_TRACE_ID);
        if(Objects.isNull(traceId)){
            final String traceIdInHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaderConstants.X_TID);
            traceId = StrUtil.isNotBlank(traceIdInHeader) ? traceIdInHeader : IdUtil.fastSimpleUUID();
            MDC.put(ProjectConstants.MDC_TRACE_ID, traceId);
        }
        return traceId;
    }

    public static void main(String[] args) {
        JwtUserVO userCenterUserInfo = JwtUtils.getUserCenterUserInfo(JwtUtils.decodedJWT("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiXCLmtYvor5XlsI_lj7dcIiIsIm1vYmlsZSI6IlwiMTgxKioqKjk1MDdcIiIsImlkIjoiNSIsInRlbmFudENvZGUiOiIxMDAwMSIsImV4cCI6MTcyMTk4ODcyNn0.GzqlJ3m-ytUmCsC0k97ua9qHLJcX4sirhXGQKkRP03I"));
        userCenterUserInfo.getTenantCode();
    }
}
