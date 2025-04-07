package com.example.project.gateway.filter.decorator;

import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Flux;

/**
 * 请求装饰器
 */
public class ServerHttpRequestDecorator extends org.springframework.http.server.reactive.ServerHttpRequestDecorator {
    private HttpHeaders headers;

    private CachedBodyOutputMessage outputMessage;

    public ServerHttpRequestDecorator(ServerHttpRequest delegate, HttpHeaders httpHeaders, CachedBodyOutputMessage outputMessage){
        super(delegate);
        this.headers = httpHeaders;
        this.outputMessage = outputMessage;
    }

    @Override
    public HttpHeaders getHeaders() {
        long contentLength = headers.getContentLength();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.putAll(super.getHeaders());
        if (contentLength > 0) {
            httpHeaders.setContentLength(contentLength);
        } else {
            // TODO: this causes a 'HTTP/1.1 411 Length Required'
            httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
        }
        return httpHeaders;
    }

    @Override
    public Flux<DataBuffer> getBody() {
        return outputMessage.getBody();
    }
}
