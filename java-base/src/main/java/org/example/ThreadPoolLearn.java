package org.example;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author hanyangyang
 * @since 2025/3/10
 */
public class ThreadPoolLearn {

  /**
   * 核心线程池大小
   * 线程池中常驻的线程数量
   * 1、默认情况下，线程池初始化的时候不会创建线程，只有当任务进来的时候才会创建线程，
   * 并且当池中数量小于核心线程数时，每次提交任务都会创建新的线程，不会复用前面创建的线程
   * 2、线程池初始化之后可以通过调用相关方法来提前创建线程
   * 3、默认情况下，只有核心线程会一直存活，当线程池中的线程数大于核心线程数时，
   * 线程会按照LRU（最近使用）的策略被回收，直到线程数=核心线程数。
   * 4、可通过调用方法allowCoreThreadTimeOut(true) 设置允许关闭核心线程
   */
  static int corePoolSize = 2;
  static int maximumPoolSize = 5;
  static int keepAliveTime = 1;

  static AtomicInteger atomicInteger = new AtomicInteger(0);

  public static ThreadPoolExecutor createThreadPool() {
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
        corePoolSize,
        maximumPoolSize,
        keepAliveTime,
        TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(10),
        new ThreadFactory() {

          @Override
          public Thread newThread(Runnable r) {
            String threadName = "pool-thread" + atomicInteger.getAndIncrement();
            Thread thread = new Thread(r, threadName);
            return thread;
          }
        },
        new ThreadPoolExecutor.CallerRunsPolicy()
    );
    return threadPoolExecutor;
  }

  public static void main(String[] args)
      throws IllegalAccessException, NoSuchFieldException, InterruptedException {
    ThreadPoolExecutor threadPool = createThreadPool();
    threadPool.allowCoreThreadTimeOut(true);
    for (int i = 0; i < 100; i++) {
      threadPool.execute(() -> {
        System.out.println(Thread.currentThread().getName());
      });
    }
    TimeUnit.SECONDS.sleep(2);
    // 通过反射获取线程池中存活的线程
    Field workersField = ThreadPoolExecutor.class.getDeclaredField("workers");
    workersField.setAccessible(true);
    Set workers = (Set) workersField.get(threadPool);
    for (Object worker : workers) {
      Field threadField = worker.getClass().getDeclaredField("thread");
      threadField.setAccessible(true);
      Thread thread = (Thread) threadField.get(worker);
      System.out.println("存活线程："+thread.getName());
    }
    // 线程池使用完要关闭
    threadPool.shutdown();
  }
}
