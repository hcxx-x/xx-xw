package org.example.designmode.proxy;

import com.sun.source.doctree.InlineTagTree;

/**
 * 静态代理：需要注入目标对象，然后对目标对象进行包装并添加自己的处理逻辑进行代理
 * 如果代理对象有多个行为，那静态代理对象也需要对应的实现不同的方法去代理不同的行为
 * 如果目标对象增加或者删除行为，那么代理对象需要做对应的改动
 * @author hanyangyang
 * @since 2025/8/16
 **/
public class StaticProxy {
    private ITargetInterface target;

    public StaticProxy(ITargetInterface target) {
        this.target = target;
    }

    public void doSomething(){
        System.out.println("静态代理doSomething");
        target.doSomething();
    }

    public static void main(String[] args) {
        ITargetInterface target = new TargetInterfaceImpl();
        StaticProxy staticProxy = new StaticProxy(target);
        staticProxy.doSomething();
    }
}
