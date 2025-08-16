package org.example.designmode;

/**
 * 内部类方式，访问方法的时候才加载之类，加载之类的时候初始化常量，jvm 保证线程安全
 * @author hanyangyang
 * @since 2025/8/14
 **/
public class Singleton2 {
    private Singleton2(){}

    private static class Holder{
        private static final Singleton2 instance = new Singleton2();
    }
    public static Singleton2 getInstance(){
        return Holder.instance;
    }
}
