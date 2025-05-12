package com.example.projectweb.core.pool;

import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * Copyright © 2020
 *
 * @author : liuzhao
 * @Project: 药企平台项目
 * <p>线程池工厂类</p>
 * @date : 2020-12-08 11:13
 **/
@Component
public class ThreadPoolFactory {

    /**
     * 核心线程池
     */
    public static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();

    /**
     * 最大线程数量
     */
    public static final int MAX_POOL_SIZE = 20;

    /**
     * 任务队列大小初始化
     */
    public static final int QUEUE_CAPACITY = 1000;

    private static ExecutorService pool = null;

    private ThreadPoolFactory(){
    }

    static {
        initCustomThreadPool();
    }

    /**
     * 初始化线程池
     */
    public static void initCustomThreadPool(){
        RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
        ThreadFactory threadFactory = new CustomizableThreadFactory("ThreadPoolFactory-");
        pool =
                new MDCThreadPoolExecutor(
                        CORE_POOL_SIZE,
                        MAX_POOL_SIZE,
                        10L,
                        TimeUnit.SECONDS,
                        new ArrayBlockingQueue<>(QUEUE_CAPACITY),
                        threadFactory,
                        rejectedExecutionHandler);
    }

    /**
     * poolName不再使用，为兼容以前写法，入参保留，但无实际意义
     *
     * 创建线程池
     *
     * @param poolName
     * @return
     */
    @Deprecated
    public static ExecutorService getThreadPoolFactory(String poolName) {
        if (pool == null || pool.isShutdown()) {
            synchronized (ThreadPoolFactory.class) {
                if (pool == null||pool.isShutdown()) {
                    initCustomThreadPool();
                }
            }
        }
        return pool;
    }

    /**
     * 获取线程池
     * @return
     */
    public static ExecutorService getThreadPoolFactory() {
        return getThreadPoolFactory("");
    }
}
