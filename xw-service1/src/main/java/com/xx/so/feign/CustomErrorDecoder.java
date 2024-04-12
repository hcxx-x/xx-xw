package com.xx.so.feign;

/**
 * 需要注意的是ErrorDecorder只处理http 响应状态码为非2xx 的响应
 * @author hanyangyang
 * @since 2024/4/12
 */
import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        // 这里可以根据不同的HTTP状态码来处理异常
        if (response.status() == 404) {
            // 处理404错误
            return new RuntimeException("404");
        }

        // 如果不需要特殊处理，则交给默认的错误解码器处理
        return defaultErrorDecoder.decode(methodKey, response);
    }
}
