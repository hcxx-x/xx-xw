package org.example.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author hanyangyang
 * @since 2025/3/31
 */
public class ProxyObject implements InvocationHandler {

    private ISubjectInterface target;

    public ProxyObject(ISubjectInterface subjectInterface) {
        this.target = subjectInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("before");
        Object invokeResult = method.invoke(target, args);
        System.out.println("after");
        return invokeResult;
    }

    public static void main(String[] args) {
        ISubjectInterface proxy = (ISubjectInterface) Proxy.newProxyInstance(ProxyObject.class.getClassLoader(),
                new Class[]{ISubjectInterface.class}, new ProxyObject(new RealSubject()));
        proxy.doSomething();

    }
}
