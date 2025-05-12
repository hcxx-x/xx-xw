package com.example.projectweb.core.pool;

import org.springframework.aop.interceptor.AsyncExecutionAspectSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author hanyangyang
 * @since 2023/8/9
 */
@Configuration
@EnableAsync
public class SpringBootAsyncConfig {
    /**
     * 指定bean名称为taskExecutor，覆盖@Async默认使用的线程池
     * @return
     */
    @Bean(AsyncExecutionAspectSupport.DEFAULT_TASK_EXECUTOR_BEAN_NAME)
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new MDCThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        // 设置最大线程数
        executor.setMaxPoolSize(20);
        //配置队列大小
        executor.setQueueCapacity(1000);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(60);
        // 设置默认线程名称
        executor.setThreadNamePrefix("boot-async-thread-");
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
        return executor;
    }
}
