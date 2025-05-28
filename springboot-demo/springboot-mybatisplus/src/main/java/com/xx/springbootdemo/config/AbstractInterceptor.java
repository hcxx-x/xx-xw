/**
 * 
 */
package com.xx.springbootdemo.config;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author lidongfu
 * @title AbstractInterceptor.java
 * @date 2021年3月24日
 * AbstractInterceptor
 * 拦截器父类
 */
public abstract class AbstractInterceptor implements MethodInterceptor {
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		// 原生方法直接调用
		if (invocation.getMethod().getDeclaringClass().equals(Object.class)) {
			return invocation.proceed();
		}
		return doIntercept(invocation);
	}
	
	public abstract Object doIntercept(MethodInvocation invocation) throws Throwable;
}
