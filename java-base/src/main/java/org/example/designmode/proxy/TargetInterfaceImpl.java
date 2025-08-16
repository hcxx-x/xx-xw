package org.example.designmode.proxy;

/**
 * 目标类
 * @author hanyangyang
 * @since 2025/8/16
 **/
public class TargetInterfaceImpl implements ITargetInterface{
    @Override
    public void doSomething() {
        System.out.println("目标方法输出");
    }
}
