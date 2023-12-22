package com.my.test;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author hanyangyang
 * @since 2023/12/21
 */
public class MultiThreadPrintABCUseWaitNotify {
    private static int state = 0;
    private static Object lock = new Object();

    /**
     * 打印次数
     */
    private int time;

    /**
     * main方法中三个线程执行的代码也可以提取出来成为一个成员方法，此时可以将state以及lock变成成员变量而不是静态变量了
     *
     * @param printValue
     * @param targetState
     */
    public void print(String printValue, int targetState) {
        for (int i = 0; i < time; i++) {
            synchronized (lock) {
                if (state % 3 == targetState) {
                    System.out.println(Thread.currentThread().getName() + ":"+printValue);
                    state++;
                    lock.notifyAll();
                } else {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        }
    }

    public static void main(String[] args) {
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                synchronized (lock) {
                    if (state % 3 == 0) {
                        System.out.println(Thread.currentThread().getName() + ":A");
                        state++;
                        lock.notifyAll();
                    } else {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                synchronized (lock) {
                    if (state % 3 == 1) {
                        System.out.println(Thread.currentThread().getName() + ":B");
                        state++;
                        lock.notifyAll();
                    } else {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                synchronized (lock) {
                    if (state % 3 == 2) {
                        System.out.println(Thread.currentThread().getName() + ":C");
                        state++;
                        lock.notifyAll();
                    } else {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }
            }
        }).start();
    }
}
