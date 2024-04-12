package com.xx.so.feign;

import feign.codec.Decoder;
import feign.optionals.OptionalDecoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfiguration {

   /* @Bean
    public CustomErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }*/

    /*@Bean
    public CustomFeignInterceptor customFeignInterceptor() {
        return new CustomFeignInterceptor();
    }*/
    @Bean
    public Decoder feignDecoder(ObjectProvider<HttpMessageConverters> messageConverters) {
        return new OptionalDecoder((new ResponseEntityDecoder(new ResponseDecoder(new SpringDecoder(messageConverters)))));
    }

}
