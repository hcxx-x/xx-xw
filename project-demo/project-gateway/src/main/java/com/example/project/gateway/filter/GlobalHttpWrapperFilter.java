package com.example.project.gateway.filter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONFactory;
import com.alibaba.fastjson2.JSONWriter;
import com.example.project.gateway.constant.ProjectConstants;
import com.example.project.gateway.constant.HttpHeaderConstants;
import com.example.project.gateway.filter.decorator.ServerHttpRequestDecorator;
import com.example.project.gateway.filter.decorator.ServerHttpResponseDecorator;
import com.example.project.gateway.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.project.gateway.constant.FilterOrderedConstant.GLOBAL_HTTP_WRAPPER;
import static com.example.project.gateway.constant.ServerWebExchangeAttributesKeyContants.*;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

/**
 * 全局统一的httpWrapper
 * <br/>
 * 1: 解决body只能读取一次的问题
 * 2: 记录请求及响应日志
 */
@Slf4j
@Component
public class GlobalHttpWrapperFilter implements GlobalFilter, Ordered {
    private final List<HttpMessageReader<?>> messageReaders = HandlerStrategies.withDefaults().messageReaders();
    // 静态文件
    private static final List<String> STATICTIS_URI_SUFFIX = CollUtil.newArrayList("/favicon.ico",
            ".html",
            ".css",
            ".js",
            ".png",
            ".jpg",
            ".jpeg",
            ".gif",
            ".svg"
    );
    @Override
    public int getOrder() {
        return GLOBAL_HTTP_WRAPPER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final MediaType mediaType = exchange.getRequest().getHeaders().getContentType();
        Mono<Void> result;
        if(isStaticResourceRequest(exchange)){
            // 静态文件，放行
            result = chain.filter(exchange);
        } else if(Objects.nonNull(mediaType) && mediaType.toString().contains("boundary=")){
            // 边界请求，一般用于文件上传之类的
            exchange.getAttributes().put(IS_BOUNDARY, true);
            result = chain.filter(exchange);
        } else if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(mediaType) || (MediaType.APPLICATION_JSON.isCompatibleWith(mediaType) && !exchange.getRequest().getMethod().equals(HttpMethod.GET))) {
            // 表单请求或者json请求，并且不是GET请求，则需要包装请求体，解决body只能读取一次的问题
            result = wrapperRequestBody(exchange, chain);
        }else{
            result = wrapperBasicLog(exchange, chain);
        }
        if(log.isInfoEnabled()){
            return result.then(Mono.fromRunnable(()-> {
                if(exchange.getAttributes().containsKey(GATEWAY_HTTP_RESPONSE_CACHE_KEY)){
                    log.info("route to: 【{}】, response body: {}", getRouteTo(exchange), exchange.getAttribute(GATEWAY_HTTP_RESPONSE_CACHE_KEY));
                }else {
                    log.info("route to: 【{}】", getRouteTo(exchange));
                }
            }));
        }
        return result;
    }

    public boolean isStaticResourceRequest(ServerWebExchange exchange) {
        String uri = exchange.getRequest().getPath().value();
        return RequestMethod.GET.name().equalsIgnoreCase(exchange.getRequest().getMethodValue()) && STATICTIS_URI_SUFFIX.stream().anyMatch(uri::endsWith);
    }

    private static String getRouteTo(ServerWebExchange exchange) {
        Object object = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        return Objects.nonNull(object) ? object.toString() : "";
    }

    /**
     * 解决 request body 只能读取一次问题，
     * 参考: org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory
     *
     * @param exchange
     * @param chain
     * @return
     */
    private Mono<Void> wrapperRequestBody(ServerWebExchange exchange, GatewayFilterChain chain) {
        final ServerRequest serverRequest = ServerRequest.create(exchange, messageReaders);
        final Mono<String> modifiedBody = serverRequest.bodyToMono(String.class).flatMap(body->{
            if(log.isInfoEnabled()){
                Object object = body;
                try {
                    object = JSON.parseObject(body);
                }catch (Throwable e){}
                if(log.isDebugEnabled()){
                    log.info(constructRequestInfo(exchange, object, true));
                }else {
                    log.info(constructRequestInfo(exchange, object, log.isDebugEnabled()));
                }
            }
            return Mono.just(body);
        });
        // 通过 BodyInserter 插入 body(支持修改body), 避免 request body 只能获取一次
        final BodyInserter<Mono<String>, ReactiveHttpOutputMessage> bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
        final HttpHeaders headers = new HttpHeaders();
        headers.putAll(exchange.getRequest().getHeaders());
        // the new content type will be computed by bodyInserter
        // and then set in the request decorator
        headers.remove(HttpHeaders.CONTENT_LENGTH);

        final CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);

        return bodyInserter.insert(outputMessage, new BodyInserterContext())
                .then(Mono.defer(() -> {
                    // 重新封装请求
                    ServerHttpRequestDecorator decoratedRequest = new ServerHttpRequestDecorator(exchange.getRequest(), headers, outputMessage);
                    // 记录响应日志
                    org.springframework.http.server.reactive.ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(exchange);
                    decoratedResponse.getHeaders().add(HttpHeaderConstants.X_TID, MDC.get(ProjectConstants.MDC_TRACE_ID));
                    // 记录普通的
                    ServerWebExchange.Builder mutate = exchange.mutate();
                    mutate.request(decoratedRequest);
                    if(log.isInfoEnabled()){
                        mutate.response(new ServerHttpResponseDecorator(exchange));
                    }
                    return chain.filter(mutate.build());
                }));
    }

    private Mono<Void> wrapperBasicLog(ServerWebExchange exchange, GatewayFilterChain chain) {
        if(log.isInfoEnabled()){
            final StringBuilder builder = new StringBuilder();
            if(exchange.getRequest().getMethod().equals(HttpMethod.GET)){
                builder.append(exchange.getRequest().getURI().getRawQuery());
            }else {
                MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
                for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
                    builder.append(entry.getKey()).append("=").append(StrUtil.join("&",entry.getValue()));
                }
            }
            if(log.isDebugEnabled()){
                log.info(constructRequestInfo(exchange, builder.toString(), true));
            }else {
                log.info(constructRequestInfo(exchange, builder.toString(), log.isDebugEnabled()));
            }
        }
        //获取响应体
        ServerWebExchange.Builder mutate = exchange.mutate();
        if(log.isInfoEnabled()){
            // 记录响应日志
            org.springframework.http.server.reactive.ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(exchange);
            decoratedResponse.getHeaders().add(HttpHeaderConstants.X_TID, MDC.get(ProjectConstants.MDC_TRACE_ID));
            mutate.response(decoratedResponse);
        }
        return chain.filter(mutate.build());
    }

    private String constructRequestInfo(ServerWebExchange exchange, Object param, boolean format){
        final String clientHost = String.format("%s:%s", HttpUtils.getIpAddress(exchange.getRequest()), HttpUtils.getPort(exchange.getRequest()));
        final ServerHttpRequest request = exchange.getRequest();
        Map<Object, Object> requestMap = MapUtil.builder()
                .put("url", request.getURI())
                .put("method", request.getMethodValue())
                .put("clientHost", clientHost)
                .put("headers", request.getHeaders().toSingleValueMap())
                .put("param", param)
                .build();
        List<JSONWriter.Feature> features = new ArrayList<>();
        if (format) {
            features.add(JSONWriter.Feature.PrettyFormat);
        }
        return JSON.toJSONString(requestMap, features.toArray(new JSONWriter.Feature[0]));
    }
}