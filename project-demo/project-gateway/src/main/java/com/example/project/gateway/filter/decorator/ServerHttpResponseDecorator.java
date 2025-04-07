package com.example.project.gateway.filter.decorator;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import static com.example.project.gateway.constant.ServerWebExchangeAttributesKeyContants.GATEWAY_HTTP_RESPONSE_CACHE_KEY;

/**
 * 响应装饰器
 * <br>
 * 会缓存response body至 attributes(key: com.ynby.byuis.gateway.constant.ServerWebExchangeAttributesKeyContants.GATEWAY_HTTP_RESPONSE_CACHE_KEY)
 **/
@Slf4j
public class ServerHttpResponseDecorator extends org.springframework.http.server.reactive.ServerHttpResponseDecorator {
    private final ServerWebExchange exchange;

    public ServerHttpResponseDecorator(ServerWebExchange exchange) {
        super(exchange.getResponse());
        this.exchange = exchange;
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        if (body instanceof Flux) {
            // 获取响应类型，如果是 json 就打印
            String originalResponseContentType = exchange.getAttribute(ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);
            if (ObjectUtil.equal(this.getStatusCode(), HttpStatus.OK) && StrUtil.isNotBlank(originalResponseContentType) && originalResponseContentType.contains("application/json")) {
                Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                    // 合并多个流集合，解决返回体分段传输
                    DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                    DataBuffer join = dataBufferFactory.join(dataBuffers);
                    byte[] content = new byte[join.readableByteCount()];
                    join.read(content);
                    // 释放掉内存
                    DataBufferUtils.release(join);
                    String responseResult = new String(content, StandardCharsets.UTF_8);
                    exchange.getAttributes().put(GATEWAY_HTTP_RESPONSE_CACHE_KEY, responseResult);
                    return getDelegate().bufferFactory().wrap(content);
                }));
            }
        }
        // if body is not a flux. never got there.
        return super.writeWith(body);
    }
}
