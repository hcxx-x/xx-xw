package org.example.designmode.single;

/**
 * 绝对线程安全，防止  反射攻击，防止序列化破坏
 * 使用方式SingletonByEnum.INSTANCE.doSomething
 * @author hanyangyang
 * @since 2025/8/14
 **/
public enum SingletonByEnum {
    INSTANCE;
    public void doSomething(){
        System.out.println("doSomething");
    }
}
