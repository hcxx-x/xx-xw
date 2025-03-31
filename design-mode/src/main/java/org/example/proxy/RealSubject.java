package org.example.proxy;

/**
 * @author hanyangyang
 * @since 2025/3/31
 */
public class RealSubject implements ISubjectInterface {
    @Override
    public void doSomething() {
        System.out.println("interfaceImpl do something");
    }
}
