package com.xx.springbootdemo.config.mybatisplus.resolver;

/**
 * sql解析器
 *
 * @Author zcchu
 * @Date 2022/10/14 18:13
 */
public interface ISqlResolverHandler {
    int paramSizeOfNotPrintSql = 128;

    String resolve();

    String getBoundSql();

    String sqlSource();
}
