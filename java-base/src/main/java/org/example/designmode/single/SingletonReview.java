package org.example.designmode.single;

/**
 * 单利模式复习
 * @author hanyangyang
 * @since 2025/8/16
 **/
public enum SingletonReview {

    INSTANCE;

   void doSomething(){
       System.out.println();
   }

    public static void main(String[] args) {
        SingletonReview.INSTANCE.doSomething();
    }
}
