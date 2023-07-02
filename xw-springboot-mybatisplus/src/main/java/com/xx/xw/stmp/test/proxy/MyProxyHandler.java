package com.xx.xw.stmp.test.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MyProxyHandler implements InvocationHandler {
    private Object subject; // 这个就是我们要代理的真实对象，也就是真正执行业务逻辑的类
    public MyProxyHandler(Object subject) {// 通过构造方法传入这个被代理对象
        this.subject = subject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        System.out.println("可以在调用实际方法前做一些事情");
        System.out.println("当前调用的方法是" + method.getName());
        result = method.invoke(subject, args);// 需要指定被代理对象和传入参数
        System.out.println(method.getName() + "方法的返回值是" + result);
        System.out.println("可以在调用实际方法后做一些事情");
        System.out.println("------------------------");
        return result;// 返回method方法执行后的返回值
    }
}
