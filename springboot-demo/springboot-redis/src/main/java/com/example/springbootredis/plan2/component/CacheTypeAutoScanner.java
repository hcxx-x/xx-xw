package com.example.springbootredis.plan2.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PreDestroy;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CacheTypeAutoScanner implements BeanPostProcessor, Ordered {

    @Autowired
    private ApplicationContext applicationContext;

    @EventListener(ApplicationReadyEvent.class)
    public void scanAndRegisterCacheTypes() {
        log.info("开始扫描被cacheable注解的方法，注册缓存Key和方法返回值类型");
        // 获取所有Bean名称
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        Map<String, Class<?>> cacheTypeMap = new HashMap<>();

        Arrays.stream(beanNames).forEach(beanName -> {
            Object bean = applicationContext.getBean(beanName);
            // 遍历Bean的所有方法
            Method[] methods = bean.getClass().getDeclaredMethods();
            Arrays.stream(methods).forEach(method -> {
                // 查找@Cacheable注解
                Cacheable cacheable = AnnotationUtils.findAnnotation(method, Cacheable.class);
                if (cacheable != null) {
                    // 提取缓存名称（支持多个名称）
                    String[] cacheNames = cacheable.value();
                    if (cacheNames.length == 0) return;

                    // 获取方法返回类型（支持泛型）
                    Class<?> returnType = method.getReturnType();
                    if (returnType.equals(void.class) || returnType.equals(Void.class)) return;

                    // 处理集合/泛型：默认使用原始类型（如List.class），用户可自定义解析
                    Class<?> targetType = resolveGenericTypeIfNeeded(method, returnType);

                    // 注册到全局映射
                    Arrays.stream(cacheNames).forEach(cacheName ->
                            cacheTypeMap.put(cacheName, targetType)
                    );
                }
            });
        });

        // 将扫描结果注册到缓存配置
        CacheTypeRegistry.registerAll(cacheTypeMap);
        log.info("完成扫描被cacheable注解的方法，缓存Key和方法返回值类型注册完成");
    }

    // 解析泛型实际类型（示例：返回List<User>时提取User.class）
    private Class<?> resolveGenericTypeIfNeeded(Method method, Class<?> returnType) {
        // 若需处理泛型，可在此处扩展（例如使用ResolvableType）
        return returnType; // 默认返回原始类型
    }


    // 创建工作窃取线程池，充分利用多核CPU
    private final ExecutorService scanExecutor =
            Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors());

    /**
     * Bean初始化后的处理入口
     * 将扫描任务提交到线程池异步执行
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        // 异步提交扫描任务，避免阻塞主线程
        scanExecutor.submit(() -> processBean(bean));
        return bean; // 返回原始Bean实例
    }

    /**
     * 处理单个Bean的方法扫描
     */
    private void processBean(Object bean) {
        // 获取目标类（处理代理类的情况）
        Class<?> targetClass = AopUtils.getTargetClass(bean);

        // 扫描类中所有声明的方法
        ReflectionUtils.doWithMethods(targetClass, method -> {
            // 查找合并后的@Cacheable注解（支持元注解）
            Cacheable cacheable = AnnotatedElementUtils.findMergedAnnotation(method, Cacheable.class);

            if (cacheable != null && !method.getReturnType().equals(void.class)) {
                // 处理每个缓存名称
                Arrays.stream(cacheable.value()).forEach(cacheName -> {
                    // 获取方法返回类型（支持泛型）
                    Class<?> returnType = method.getReturnType();

                    // 处理集合/泛型：默认使用原始类型（如List.class），用户可自定义解析
                    Class<?> targetType = resolveGenericTypeIfNeeded(method, returnType);

                    // 注册到全局映射  需要定义全局变量
                    //cacheTypeMap.put(cacheName, targetType);

                    log.debug("注册缓存映射: {} => {}", cacheName, method.getGenericReturnType());
                });
            }
        });
    }

    /**
     * 应用关闭时优雅关闭线程池
     */
    @PreDestroy
    public void shutdown() {
        scanExecutor.shutdown();
        try {
            if (!scanExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                log.warn("缓存扫描线程池未及时关闭，强制终止");
                scanExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 设置最高优先级，确保在其他Bean初始化前完成扫描
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}