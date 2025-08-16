package org.example.finaldemo;

/**
 * final 修饰的实例变量要么在声明的时候赋值，要么在构造函数中赋值，没有其他可以修改的地方
 * @author hanyangyang
 * @since 2025/8/15
 **/
public class FinalDemo {
    private final Object object;

    static{
        
    }

    public FinalDemo(Object object) {
        this.object = object;
    }
}
