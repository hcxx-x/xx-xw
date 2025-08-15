package org.example;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * @author hanyangyang
 * @since 2025/8/13
 **/
public class ThreadDemo {
    public static void main(String[] args) {
        new Thread(()->{
            try {
                Thread.sleep(3000);
                System.out.println("子线程结束");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }).start();
        System.out.println("main 线程结束");
    }

    public static void createThread(){
        Thread thread = new Thread(){
            @Override
            public void run() {
                System.out.println("继承Thread 创建线程");
            }
        };
    }

    public static void createThreadCallable() throws ExecutionException, InterruptedException {
        Callable callable = new Callable<String>(){

            @Override
            public String call() throws Exception {
                return "callable 线程运行";
            }
        };
        FutureTask futureTask = new FutureTask<>(callable);
        new Thread(futureTask).start();
        System.out.println(futureTask.get());

    }
}
