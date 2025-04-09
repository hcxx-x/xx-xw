package com.example.project.gateway.filter;

import cn.hutool.core.collection.CollUtil;

import com.example.project.gateway.property.SystemProperties;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.project.gateway.constant.FilterOrderedConstant.WHITE_URL_FILTER_ORDER;
import static com.example.project.gateway.constant.ServerWebExchangeAttributesKeyConstants.*;


/**
 * 白名单校验
 */
@Slf4j
@Component
public class WhiteUrlFilter implements GlobalFilter, Ordered {
    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private final static Set<String> problemExpressionByAnt = new HashSet<>(1024);

    private final static Set<String> problemExpressionByJDK = new HashSet<>(1024);


    @Resource
    private SystemProperties properties;

    @Override
    public int getOrder() {
        return WHITE_URL_FILTER_ORDER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 白名单url,直接放行，不做任何验证，并添加相关属性告知后面的过滤器不验证
        if(validate(properties.getWhiteUrls(), exchange.getRequest().getURI().getPath())){
            exchange.getAttributes().put(IS_ALL_NOT_NEED_VERIFY, true);
            return chain.filter(exchange);
        }
        // 判断是否需要验证重放攻击
        exchange.getAttributes().put(IS_NEED_VERIFY_REPLAY_ATACK, !validate(properties.getReplayAttackWhiteUrls(), exchange.getRequest().getURI().getPath()));
        // 判断是否需要验证token
        exchange.getAttributes().put(IS_NEED_VERIFY_TOKEN, !validate(properties.getTokenWhiteUrls(), exchange.getRequest().getURI().getPath()));
        // 判断是否需要验证签名
        exchange.getAttributes().put(IS_NEED_VERIFY_SIGN_CONSISTENCY, !validate(properties.getSignWhiteUrls(), exchange.getRequest().getURI().getPath()));
        return chain.filter(exchange);
    }

    private boolean validate(List<String> extraUrls, String uri) {
        if(CollUtil.isEmpty(extraUrls)){
            return false;
        }
        for (String extraUrl : extraUrls) {
            if (isMatch(uri, extraUrl)) {
                if (log.isInfoEnabled()) {
                    log.info("request uri:[{}] conform to the rules:[{}], not intercept", uri, extraUrl);
                }
                return true;
            }
        }
        return false;
    }

    private static boolean isMatch(String uri, String extraUrlExpression) {
        return uri.contains(extraUrlExpression) || isMatchedByAntPathMatcher(uri, extraUrlExpression) || isMatchesByJdkPatternMatcher(uri, extraUrlExpression);
    }

    private static boolean isMatchesByJdkPatternMatcher(String uri, String extraUrlExpression) {
        if(problemExpressionByJDK.contains(extraUrlExpression)){
            // 当前表达式异常，不符合jdk pattern,继续下一个表达式匹配
            log.warn("The URL expression {} is erroneous and does not match the JDK pattern rules. Moving to the next expression for matching.", extraUrlExpression);
            return false;
        }
        try {
            return uri.matches(extraUrlExpression);
        }catch (Throwable e){
            log.warn("The URL expression {} is erroneous and does not match the JDK pattern rules. err: {}.", extraUrlExpression, e.getMessage());
            problemExpressionByJDK.add(extraUrlExpression);
            return false;
        }
    }

    /**
     * 检查指定URI是否与给定的Ant风格路径表达式匹配
     *
     * @param uri 需要匹配的请求URI路径
     * @param extraUrlExpression Ant风格的路径匹配表达式
     * @return true表示匹配成功，false表示不匹配或表达式异常
     */
    private static boolean isMatchedByAntPathMatcher(String uri, String extraUrlExpression) {
        // 处理已知无效表达式，避免重复计算和日志记录
        if(problemExpressionByAnt.contains(extraUrlExpression)){
            // 当前表达式异常，不符合ant pattern,继续下一个表达式匹配
            log.warn("The URL expression {} is erroneous and does not match the Ant pattern rules. Moving to the next expression for matching.", extraUrlExpression);
            return false;
        }

        // 尝试进行Ant表达式匹配，捕获可能由无效表达式引发的异常
        try {
            return antPathMatcher.match(extraUrlExpression, uri);
        }catch (Throwable e){
            log.warn("The URL expression {} is erroneous and does not match the Ant pattern rules. err: {}.", extraUrlExpression, e.getMessage());
            problemExpressionByAnt.add(extraUrlExpression);
            return false;
        }
    }

}
