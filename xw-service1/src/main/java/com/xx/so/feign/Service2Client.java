package com.xx.so.feign;

import com.xx.core.http.HttpR;
import com.xx.so.feign.fallback.factory.Service2FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author hanyangyang
 * @since 2024/4/11
 */
@FeignClient(value = "service2",path = "/service2",fallbackFactory = Service2FallbackFactory.class,configuration = FeignClientConfiguration.class)
public interface Service2Client {

    @RequestMapping("/api1")
    void api1() throws Throwable;

    @RequestMapping("/api2")
    String api2() throws Throwable;

    @RequestMapping("/api3")
    String api3() throws Throwable;
}
