package com.xx.docker.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author hanyangyang
 * @since 2023/3/8
 */

@Slf4j
@Configuration
@EnableAsync
public class ThreadPoolConfig {

    @Value("${async.executor.thread.core_pool_size}")
    private int corePoolSize;
    @Value("${async.executor.thread.max_pool_size}")
    private int maxPoolSize;
    @Value("${async.executor.thread.queue_capacity}")
    private int queueCapacity;
    @Value("${async.executor.thread.name.prefix}")
    private String namePrefix;

    /**
     * TODO: 此方法名称为asyncPromiseExecutor，即在spring中注入了一个名字为asyncPromiseExecutor的bean
     * 方法名只要在项目中唯一性，可以适当任意取（最好遵循一定的规则）
     * 使用方法：在需要加入线程池的方法上增加注解@Async("asyncPromiseExecutor")就可以加入此线程池异步执行
     *
     * @return
     * @throws
     * @author zhaoxi
     * @time 2018/11/16 14:36
     * @params
     */
    //@Bean(name = "asyncServiceExecutor")
    public Executor asyncServiceExecutor() {
        log.info("start asyncServiceExecutor");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(corePoolSize);
        //配置最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        //配置队列大小
        executor.setQueueCapacity(queueCapacity);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix(namePrefix);

        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
        return executor;
    }


    @Bean(name = "asyncServiceExecutor2")
    public Executor asyncServiceExecutor2() {
        log.info("start asyncServiceExecutor");
        ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize,maxPoolSize,60, TimeUnit.SECONDS,new ArrayBlockingQueue<>(queueCapacity),new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
}