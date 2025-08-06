package org.example.juc;

/**
 * @author hanyangyang
 * @since 2025/8/2
 **/
public class ThreadLocalDemo {
    public static void main(String[] args) {
        ThreadLocal<Integer> threadLocal = new ThreadLocal<>();
        new Thread(){
            @Override
            public void run() {
                threadLocal.set(1);
            }
        };
    }
}
