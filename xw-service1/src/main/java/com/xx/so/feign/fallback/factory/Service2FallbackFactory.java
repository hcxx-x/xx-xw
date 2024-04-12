package com.xx.so.feign.fallback.factory;

import com.xx.so.feign.Service2Client;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

/**
 * @author hanyangyang
 * @since 2024/4/12
 */
@Component
@Slf4j
public class Service2FallbackFactory implements FallbackFactory<Service2Client> {
    @Override
    public Service2Client create(Throwable throwable) {
        log.error("异常原因:{}", throwable.getMessage(), throwable);
        return new Service2Client(){
            @Override
            public String api1() throws Throwable {
                throw throwable;
            }
        };
    }
}
