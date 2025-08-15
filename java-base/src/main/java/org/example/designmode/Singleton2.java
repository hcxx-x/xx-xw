package org.example.designmode;

/**
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
