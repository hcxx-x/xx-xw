package com.example.springbootredis.aop;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.SortParameters;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
/**
 * AOP 切面：拦截缓存方法，在命中缓存后进行类型还原处理。
 * 用于解决使用通用 Jackson 序列化时对象被还原为 LinkedHashMap 的问题。
 * 使用此切面可以将不包含@class属性的对象反序列化为目标类型。目标类型从aop拦截的方法返回值中获取
 */
@Slf4j
@Aspect
@Component
@Order(-1)
public class CacheResultFixAspect {

    private final ObjectMapper objectMapper;

    public CacheResultFixAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Pointcut("@annotation(org.springframework.cache.annotation.Cacheable)")
    public void pointcut() {
    }


    /**
     * 拦截所有带有 @Cacheable 注解的方法。
     * @param joinPoint 切点
     * @return 正确类型的返回对象
     * @throws Throwable 异常
     */
    @Around("pointcut()")
    public Object aroundCacheable(ProceedingJoinPoint joinPoint) throws Throwable {
        // 执行原始方法
        Object result = joinPoint.proceed();

        // 缓存未命中，记录日志
        if (result == null) {
            log.info("[Cache MISS] Method: {}", joinPoint.getSignature());
            return null;
        }

        // 缓存命中但不需要转换（已经是目标类型）
        if (!(result instanceof LinkedHashMap || result instanceof List)) {
            return result;
        }

        // 获取方法返回类型
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Type returnType = method.getGenericReturnType();

        try {
            // 将结果转为 JSON 字符串，再用目标类型反序列化回来
            String json = objectMapper.writeValueAsString(result);

            // 如果返回类型是 List<T>
            if (returnType.getTypeName().startsWith("java.util.List")) {
                // 提取泛型中的 T
                Type[] actualTypes = ((java.lang.reflect.ParameterizedType) returnType).getActualTypeArguments();
                if (actualTypes.length > 0 && actualTypes[0] instanceof Class) {
                    Class<?> itemType = (Class<?>) actualTypes[0];
                    return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, itemType));
                }
            } else if (returnType instanceof Class<?>) {
                // 单对象直接转换
                return objectMapper.readValue(json, (Class<?>) returnType);
            }
        } catch (Exception e) {
            log.error("[Cache Deserialize ERROR] Method: {}, Type: {}, Error: {}", joinPoint.getSignature(), returnType, e.getMessage());
        }

        // 出错则返回原始结果（LinkedHashMap）
        return result;
    }
}

