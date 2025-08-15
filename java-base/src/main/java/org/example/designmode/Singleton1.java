package org.example.designmode;

/**
 * 懒汉式，需要通过锁来保证多线程的单例性
 * @author hanyangyang
 * @since 2025/8/14
 **/
public class Singleton1 {
    /**
     *
     * 对象实例化的非原子操作：instance = new Singleton()在JVM中分为三步：
     * 1、分配对象内存空间
     * 2、调用构造函数初始化对象
     * 3、将引用指向分配的内存地址
     * 编译器和处理器可能对步骤  和 3 重排序，导致顺序变为  → 3 → 2
     * 需要使用volatile 禁止指令重排主要防止第一次检查的时候因为指令重排导致导致返回引用不为空，但是未完全实例化的对象
     * 对于内存可见性个人觉得synchronized关键字就已经保证了
     */
    public static volatile Singleton1 instance;
    private Singleton1() {
    }
    public Singleton1 getInstance(){
        if (instance==null){// volatile 关键字禁止指令重排，防止
            synchronized (Singleton1.class){
                if (instance==null){
                    instance = new Singleton1();
                }
            }
        }
        return instance;
    }


}
