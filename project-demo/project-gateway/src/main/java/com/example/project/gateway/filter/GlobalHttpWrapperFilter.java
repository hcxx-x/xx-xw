package com.example.project.gateway.filter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
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
import static com.example.project.gateway.constant.ServerWebExchangeAttributesKeyConstants.*;
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
     * 解决 request body 只能读取一次问题，通过缓存和重新封装请求实现多次访问请求体
     * 参考实现: org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory
     *
     * @param exchange 当前请求的服务器Web交换对象，包含请求和响应信息
     * @param chain 网关过滤器链，用于继续执行后续过滤器
     * @return Mono<Void> 异步处理结果，表示过滤操作的完成状态
     */
    private Mono<Void> wrapperRequestBody(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 构建可重复读取的请求对象
        final ServerRequest serverRequest = ServerRequest.create(exchange, messageReaders);

        // 处理请求体并记录日志：解析JSON结构，保留原始body用于日志输出
        final Mono<String> modifiedBody = serverRequest.bodyToMono(String.class).flatMap(body->{
            if(log.isInfoEnabled()){
                Object object = body;
                try {
                    object = JSON.parseObject(body); // 尝试解析JSON结构提升日志可读性
                }catch (Throwable e){} // 解析失败时仍使用原始body记录
                if(log.isDebugEnabled()){
                    log.info(constructRequestInfo(exchange, object, true));
                }else {
                    log.info(constructRequestInfo(exchange, object, log.isDebugEnabled()));
                }
            }
            return Mono.just(body); // 返回原始body保持请求体不变
        });

        // 构建可重复插入的请求体处理器：通过BodyInserter支持多次读取
        final BodyInserter<Mono<String>, ReactiveHttpOutputMessage> bodyInserter =
            BodyInserters.fromPublisher(modifiedBody, String.class);

        // 准备新请求头：移除Content-Length以便自动计算新长度
        final HttpHeaders headers = new HttpHeaders();
        headers.putAll(exchange.getRequest().getHeaders());
        headers.remove(HttpHeaders.CONTENT_LENGTH);

        // 创建带缓存的输出消息对象
        final CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);

        // 插入请求体并处理后续过滤逻辑
        return bodyInserter.insert(outputMessage, new BodyInserterContext())
                .then(Mono.defer(() -> {
                    // 构建装饰后的请求对象：携带缓存body和新headers
                    ServerHttpRequestDecorator decoratedRequest =
                        new ServerHttpRequestDecorator(exchange.getRequest(), headers, outputMessage);

                    // 装饰响应对象：添加分布式追踪ID到响应头
                    ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(exchange);
                    decoratedResponse.getHeaders().add(
                        HttpHeaderConstants.X_TID,
                        MDC.get(ProjectConstants.MDC_TRACE_ID)
                    );

                    // 构建新的交换对象并继续过滤器链
                    ServerWebExchange.Builder mutate = exchange.mutate();
                    mutate.request(decoratedRequest);
                    if(log.isInfoEnabled()){
                        mutate.response(new ServerHttpResponseDecorator(exchange));
                    }
                    return chain.filter(mutate.build());
                }));
    }


    /**
     * 包装基础日志记录逻辑（请求/响应信息）
     *
     * @param exchange 服务端网络交换对象，包含HTTP请求/响应信息及上下文属性
     * @param chain 网关过滤器链，用于继续执行后续过滤器
     * @return Mono<Void> 表示异步处理结果
     */
    private Mono<Void> wrapperBasicLog(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 处理请求日志记录
        if(log.isInfoEnabled()){
            final StringBuilder builder = new StringBuilder();
            // GET请求直接获取原始查询参数，其他方法解析结构化参数
            if(exchange.getRequest().getMethod().equals(HttpMethod.GET)){
                builder.append(exchange.getRequest().getURI().getRawQuery());
            }else {
                MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
                for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
                    builder.append(entry.getKey()).append("=").append(StrUtil.join("&",entry.getValue()));
                }
            }
            // 根据调试级别决定日志详细程度
            if(log.isDebugEnabled()){
                log.info(constructRequestInfo(exchange, builder.toString(), true));
            }else {
                log.info(constructRequestInfo(exchange, builder.toString(), log.isDebugEnabled()));
            }
        }

        // 构建响应装饰器
        ServerWebExchange.Builder mutate = exchange.mutate();
        if(log.isInfoEnabled()){
            // 装饰响应对象用于记录响应日志，添加分布式追踪ID
            ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(exchange);
            decoratedResponse.getHeaders().add(HttpHeaderConstants.X_TID, MDC.get(ProjectConstants.MDC_TRACE_ID));
            mutate.response(decoratedResponse);
        }

        // 继续执行过滤器链
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