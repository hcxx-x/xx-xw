package com.example.project.gateway.config;

import cn.hutool.core.bean.BeanUtil;

import com.alibaba.fastjson2.JSON;
import com.example.project.gateway.constant.HttpHeaderConstants;
import com.example.project.gateway.handler.GlobalExceptionHandler;
import com.example.project.gateway.utils.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.reactive.result.view.ViewResolver;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.project.gateway.constant.ServerWebExchangeAttributesKeyContants.TRACE_ID;

/**
 * 异常拦截配置
 *
 * @Author zcchu
 * @Date 2021/3/12 16:01
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnClass(WebFluxConfigurer.class)
@AutoConfigureBefore(WebFluxAutoConfiguration.class)
@EnableConfigurationProperties({ServerProperties.class, WebProperties.class})
public class CustomErrorWebFluxAutoConfiguration {

    private final ServerProperties serverProperties;

    private final ApplicationContext applicationContext;

    private final WebProperties webProperties;

    private final List<ViewResolver> viewResolvers;

    private final ServerCodecConfigurer serverCodecConfigurer;

    public CustomErrorWebFluxAutoConfiguration(ServerProperties serverProperties,
                                               WebProperties webProperties,
                                               ObjectProvider<ViewResolver> viewResolversProvider,
                                               ServerCodecConfigurer serverCodecConfigurer,
                                               ApplicationContext applicationContext) {
        this.serverProperties = serverProperties;
        this.applicationContext = applicationContext;
        this.webProperties = webProperties;
        this.viewResolvers = viewResolversProvider.orderedStream().collect(Collectors.toList());
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    /**
     * 注册自定义错误Web异常处理器Bean
     *
     * @param errorAttributes 错误属性对象，包含请求的错误信息
     * @return 配置完成的JSON格式异常处理器实例
     *
     * @ConditionalOnMissingBean 确保容器中不存在其他ErrorWebExceptionHandler时才注册
     * @Order(-1) 设置处理器最高优先级，确保优先于默认处理器
     */
    @Bean
    @ConditionalOnMissingBean(value = ErrorWebExceptionHandler.class, search = SearchStrategy.CURRENT)
    @Order(-1)
    public ErrorWebExceptionHandler errorWebExceptionHandler(ErrorAttributes errorAttributes) {
        // 创建基于JSON格式的异常处理器实例，组合配置属性
        JsonErrorWebExceptionHandler exceptionHandler = new JsonErrorWebExceptionHandler(
            errorAttributes,
            webProperties,
            this.serverProperties.getError(),  // 从服务配置获取错误处理参数
            applicationContext
        );

        // 配置处理器的核心组件
        exceptionHandler.setViewResolvers(this.viewResolvers);  // 注入视图解析器集合
        exceptionHandler.setMessageWriters(this.serverCodecConfigurer.getWriters());  // 配置HTTP消息编码器
        exceptionHandler.setMessageReaders(this.serverCodecConfigurer.getReaders());  // 配置HTTP消息解码器

        return exceptionHandler;
    }


    /**
     * 注册默认错误属性处理Bean
     *
     * 当容器中不存在ErrorAttributes类型的Bean时，创建默认实现。该Bean用于处理控制器抛出的异常，
     * 收集错误信息（状态码、错误信息、堆栈跟踪等）供错误视图展示。
     *
     * @ConditionalOnMissingBean 条件注解说明：
     *   - value: 目标Bean类型为ErrorAttributes
     *   - search: 搜索策略为当前应用上下文（SearchStrategy.CURRENT）
     *
     * @return DefaultErrorAttributes 默认错误属性实现，包含基本错误信息收集功能
     *
     * 注意：当需要自定义错误处理时，可通过实现ErrorAttributes接口并注册为Bean来覆盖此默认配置
     */
    @Bean
    @ConditionalOnMissingBean(value = ErrorAttributes.class, search = SearchStrategy.CURRENT)
    public DefaultErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes();
    }



    @Slf4j
    static class JsonErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {

        private final static String SPRING_ACTUATOR_URI = "/actuator";
        private final ApplicationContext applicationContext;
        private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
        public JsonErrorWebExceptionHandler(ErrorAttributes errorAttributes,
                                            WebProperties webProperties,
                                            ErrorProperties errorProperties,
                                            ApplicationContext applicationContext) {
            super(errorAttributes, webProperties.getResources(), errorProperties, applicationContext);
            this.applicationContext = applicationContext;
        }


        @Override
        protected Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
            if(request.path().contains(SPRING_ACTUATOR_URI)){
                return super.getErrorAttributes(request, includeStackTrace);
            }
            // 这里其实可以根据异常类型进行定制化逻辑
            Throwable throwable = super.getError(request);
            Map<String, Object> responseBody = BeanUtil.beanToMap(globalExceptionHandler.response(throwable));
            log.error("request uri: {}, response body:{}, err: {}", request.path(), JSON.toJSONString(responseBody), Util.throwableMessage(throwable));
            return responseBody;
        }

        @Override
        protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
            return RouterFunctions.route(RequestPredicates.all(), (request) -> {
                Mono<ServerResponse> serverResponseMono = this.renderErrorResponse(request);
                HttpHeaders headers = request.exchange().getResponse().getHeaders();
                if(headers.containsKey(HttpHeaderConstants.X_TID)){
                    return serverResponseMono;
                }
                headers.add(HttpHeaderConstants.X_TID, request.exchange().getAttributeOrDefault(TRACE_ID,"空"));
                return serverResponseMono;
            });
        }

        @Override
        protected int getHttpStatus(Map<String, Object> errorAttributes) {
            // 这里其实可以根据errorAttributes里面的属性定制HTTP响应码
            // 统一响应200
            return HttpStatus.OK.value();
        }
    }
}