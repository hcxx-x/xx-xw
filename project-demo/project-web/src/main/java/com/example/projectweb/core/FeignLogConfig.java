package com.example.projectweb.core;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.example.projectweb.core.context.RequestContext;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;

import java.util.Collection;
import java.util.Map;

public class FeignLogConfig implements RequestInterceptor {
    @Bean
    Logger.Level feignLoggerLevel() {
        //这里记录所有，根据实际情况选择合适的日志level
        return Logger.Level.FULL;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        Map<String, Collection<String>> headers = requestTemplate.headers();
        if (!headers.containsKey(RequestConstant.REQUEST_ID)){
            String requestId = RequestContext.getRequestId();
            if (StrUtil.isBlank(requestId)){
                requestId = IdUtil.fastUUID();
            }
            requestTemplate.header(RequestConstant.REQUEST_ID,requestId);
        }
    }
}
