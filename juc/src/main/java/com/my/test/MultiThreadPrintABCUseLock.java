package com.my.test;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author hanyangyang
 * @since 2023/12/21
 */
public class MultiThreadPrintABCUseLock {
    private int times; // 控制打印次数
    private int state;   // 当前状态值：保证三个线程之间交替打印
    private Lock lock = new ReentrantLock();

    public MultiThreadPrintABCUseLock(int times) {
        this.times = times;
    }

    private void printLetter(String name, int targetNum) {
        for (int i = 0; i < times; ) {
            // 这里使用lock和synchronized的操作是一样的，原理上都是让锁中间的代码串行执行，而在执行过程中如果state不符合条件则会释放锁，
            // 开启下一轮的锁竞争以达到轮询打印的效果
            lock.lock();
            //synchronized (this){
                if (state % 3 == targetNum) {
                    state++;
                    i++;
                    System.out.println(Thread.currentThread().getName()+":"+name);
                }
            //}
            lock.unlock();
        }
    }

    public static void a(String[] args) {
        // 创建一个公共资源，多个线程对同一个资源操作，资源内锁生效
        MultiThreadPrintABCUseLock loopThread = new MultiThreadPrintABCUseLock(100);
        new Thread(() -> {
            loopThread.printLetter("A", 0);
        }, "A").start();

        new Thread(() -> {
            loopThread.printLetter("B", 1);
        }, "B").start();

        new Thread(() -> {
            loopThread.printLetter("C", 2);
        }, "C").start();
    }

    public static void main(String[] args) throws InterruptedException {
        Object o = new Object();
        synchronized (o){
            o.wait();
        }


    }
}
