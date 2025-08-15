package org.example.designmode;

/**
 * 饿汉式，线程安全通过JVM类加载机制保证
 * 如果一直用不到会造成资源浪费
 * @author hanyangyang
 * @since 2025/8/14
 **/
public class Singleton {
    public static final Singleton instance = new Singleton();

    private Singleton(){}

    public Singleton getInstance(){
        return instance;
    }
}
