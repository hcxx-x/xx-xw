package org.example.designmode.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author hanyangyang
 * @since 2025/8/16
 **/
public class DynamicProxy {

    /**
     * 通过实现Invocationhandler 实现代理
     */
    static class MyInvocationHandler implements InvocationHandler{
        private ITargetInterface target;

        // 需要提供构造参数或者其他的方式拿到目标对象
        public MyInvocationHandler(ITargetInterface target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("代理对象开始代理，进行增强");
            method.invoke(target,args);
            return null;
        }
    }




    public static void main(String[] args) {
        ITargetInterface target = new TargetInterfaceImpl();

        MyInvocationHandler myInvocationHandler = new MyInvocationHandler(target);
        ITargetInterface proxyInst = (ITargetInterface) Proxy.newProxyInstance(DynamicProxy.class.getClassLoader(), new Class[]{ITargetInterface.class}, myInvocationHandler);
        proxyInst.doSomething();


        // 通过匿名内部类的方式实现InvocationHandler实现代理
        ITargetInterface proxy = (ITargetInterface) Proxy.newProxyInstance(DynamicProxy.class.getClassLoader(), new Class[]{ITargetInterface.class}, new InvocationHandler() {
            // 参数解析：proxy 代理对象本身，method 目标对象的方法， proxy调用哪个方法的method就代指哪个方法，arg方法入参
            // proxy 代理对象在这里可以作为参数传递给其他方法调用，或者调用其他的代理方法
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("动态代理调用");

                return method.invoke(target,args);
            }
        });
        proxy.doSomething();
    }
}
