/**
 * 通过aop切面拦截被cacheable注解的方法，获取方法返回值，将从缓存中获取的数据反序列化为对应的类型
 * 解决缓存需要_class属性的问题
 */
package com.example.springbootredis.plan1;