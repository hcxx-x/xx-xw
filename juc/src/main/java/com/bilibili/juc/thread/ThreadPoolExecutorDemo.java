package com.bilibili.juc.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorDemo {
    public static void main(String[] args) throws InterruptedException {
        // 创建一个容量固定的线程池，corePoolSize和maximumPoolSize相等，但是阻塞队列的容量却是Integer.max_value,在线程数量非常大的时候容易oom
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        threadPoolExecutor.execute(new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getId());
            }
        }));
        TimeUnit.SECONDS.sleep(5);
        threadPoolExecutor.execute(new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getId());
            }
        }));

        /**
         * 线程池中的数量小于核心线程数时创建一个新的工作线程
         */
        threadPoolExecutor.prestartCoreThread();
        /**
         * 将核心线程全部创建出来
         */
        threadPoolExecutor.prestartAllCoreThreads();

        threadPoolExecutor.allowCoreThreadTimeOut();

        threadPoolExecutor.shutdown();
    }
}
