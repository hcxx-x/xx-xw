package com.example.core.pool;


import org.slf4j.MDC;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 自定义线程池执行器，用于定义springboot线程池，在每次执行任务的时候从调用线程中获取MDC,以实现调用线程和子线程的日志追踪
 * @author hanyangyang
 * @since 2023/8/9
 */
public class MDCThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {

    public MDCThreadPoolTaskExecutor() {
        super();
    }

    @Override
    public void execute(Runnable task) {
        super.execute(ThreadWrapperUtil.wrap(task, MDC.getCopyOfContextMap()));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(ThreadWrapperUtil.wrap(task, MDC.getCopyOfContextMap()));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(ThreadWrapperUtil.wrap(task, MDC.getCopyOfContextMap()));
    }

    @Override
    public ListenableFuture<?> submitListenable(Runnable task) {
        return super.submitListenable(ThreadWrapperUtil.wrap(task, MDC.getCopyOfContextMap()));
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        return super.submitListenable(ThreadWrapperUtil.wrap(task, MDC.getCopyOfContextMap()));
    }

    @Override
    protected void cancelRemainingTask(Runnable task) {
        super.cancelRemainingTask(ThreadWrapperUtil.wrap(task, MDC.getCopyOfContextMap()));
    }
}
