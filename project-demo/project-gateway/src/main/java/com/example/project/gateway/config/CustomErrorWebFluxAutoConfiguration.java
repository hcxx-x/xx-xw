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
 * 自定义异常拦截配置
 *
 * 在SpringCloud gateway中默认使用 DefaultErrorWebExceptionHandler来处理异常。这个可以通过配置类 ErrorWebFluxAutoConfiguration 得之。
 *
 * 我们可以自定义一个 CustomErrorWebExceptionHandler类用来继承 DefaultErrorWebExceptionHandler，然后修改生成前端响应数据的逻辑。
 * 再然后定义一个配置类，写法可以参考 ErrorWebFluxAutoConfiguration，简单将异常类替换成 CustomErrorWebExceptionHandler类即可。
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


    /**
     * 自定义异常处理器
     */
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


        /**
         * 重写错误属性获取方法，根据请求路径区分处理逻辑，生成定制化错误响应
         *
         * @param request 当前请求对象，用于获取请求路径和错误信息
         * @param includeStackTrace 是否包含堆栈跟踪标识，父类方法使用该参数控制错误信息详细程度
         * @return Map<String, Object> 结构化的错误响应体，包含错误码、消息等信息
         */
        @Override
        protected Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
            // 特殊路径处理：对Spring Actuator端点保持默认错误处理逻辑
            if(request.path().contains(SPRING_ACTUATOR_URI)){
                return super.getErrorAttributes(request, includeStackTrace);
            }
            // 核心错误处理逻辑：通过全局异常处理器构建统一响应格式
            Throwable throwable = super.getError(request);
            // 将响应对象转换为Map结构，适配框架错误处理格式要求
            Map<String, Object> responseBody = BeanUtil.beanToMap(globalExceptionHandler.response(throwable));

            // 记录错误日志：包含请求路径、响应体和异常概要信息
            log.error("request uri: {}, response body:{}, err: {}", request.path(), JSON.toJSONString(responseBody), Util.throwableMessage(throwable));

            return responseBody;
        }

        /**
         * 重写父类方法构建全局错误处理路由函数
         *
         * @param errorAttributes 错误属性对象，包含请求上下文中的错误信息
         * @return 配置好的路由函数，用于处理所有请求的错误响应
         *
         * 实现逻辑：
         * 1. 创建匹配所有请求的路由规则
         * 2. 对每个请求生成错误响应对象
         * 3. 检查响应头是否包含跟踪ID：
         *    - 若存在则直接返回响应
         *    - 若不存在则从请求属性中获取跟踪ID并注入响应头
         */
        @Override
        protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
            return RouterFunctions.route(RequestPredicates.all(), (request) -> {
                // 生成错误响应对象的基础Mono流
                Mono<ServerResponse> serverResponseMono = this.renderErrorResponse(request);
                // 获取响应头并处理跟踪ID逻辑
                HttpHeaders headers = request.exchange().getResponse().getHeaders();
                if(headers.containsKey(HttpHeaderConstants.X_TID)){
                    // 已存在跟踪ID时直接返回响应
                    return serverResponseMono;
                }
                // 注入新的跟踪ID到响应头
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