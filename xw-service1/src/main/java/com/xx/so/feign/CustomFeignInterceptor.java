package com.xx.so.feign;


import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Response;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;


public class CustomFeignInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 在请求执行前的操作

    }


}
