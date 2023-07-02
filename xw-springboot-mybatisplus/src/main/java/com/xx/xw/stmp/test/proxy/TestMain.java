package com.xx.xw.stmp.test.proxy;

import java.lang.reflect.Proxy;

public class TestMain {
    public static void main(String[] args) {
        MyInterfaceImpl myInterface = new MyInterfaceImpl();
        MyInterface proxy = (MyInterface) Proxy.newProxyInstance(myInterface.getClass().getClassLoader(), new Class[]{MyInterface.class}, new MyProxyHandler(myInterface));
        proxy.myMethod();

    }
}
