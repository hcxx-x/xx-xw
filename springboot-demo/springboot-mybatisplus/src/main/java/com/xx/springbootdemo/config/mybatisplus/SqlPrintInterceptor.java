package com.xx.springbootdemo.config.mybatisplus;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ClassUtil;
import com.baomidou.mybatisplus.core.MybatisParameterHandler;

import com.xx.springbootdemo.config.mybatisplus.resolver.DefaultParameterSqlResolverHandler;
import com.xx.springbootdemo.config.mybatisplus.resolver.ISqlResolverHandler;
import com.xx.springbootdemo.config.mybatisplus.resolver.MybatisParameterSqlResolverHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Statement;
import java.util.*;

/**
 * MyBatis拦截器打印不带问号的完整sql语句
 * <br>
 * 注：日志级别高于INFO时，不会执行具体解析记录逻辑，不影响性能
 * @see SqlPrintInterceptor
 * @since
 */
@Slf4j
@Intercepts({@Signature(type = StatementHandler.class, method = "parameterize", args = {Statement.class})})
public class  SqlPrintInterceptor implements Interceptor {

    private Method peekCurrDataSource;
    private static final String DEFAULT_DATASOURCE = "default";

    public SqlPrintInterceptor(){
        try {
            init();
        }catch (Throwable e){
            log.warn("初始化数据源名称失败");
        }
    }

    private void init() throws NoSuchMethodException {
        Class<Object> clzOfDynamicDataSourceContextHolder = ClassUtil.loadClass("com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder");
        if(Objects.nonNull(clzOfDynamicDataSourceContextHolder)){
            peekCurrDataSource = clzOfDynamicDataSourceContextHolder.getMethod("peek");
        } else {
            log.warn("未找到DynamicDataSourceContextHolder类");
        }
    }

    @Setter
    private Map<String, List<String>> classMethodsMap = MapUtil.<String, List<String>>builder().map();

    @Override
    public Object intercept(Invocation invocation)throws Throwable {
        final ISqlResolverHandler sqlResolverHandler = sqlResolverHandler(invocation);
        final String sqlSource = sqlResolverHandler.sqlSource();
        final String[] split = sqlSource.split("\\.");
        // mapper接口的名字
        final String mapperClassName = split[split.length - 2];
        // 接口的方法名字
        final String methodName = split[split.length - 1];

        if(!isPrint(mapperClassName, methodName)){
            return invocation.proceed();
        }

        try {
            final String dataSourceName = printDataSourceName();
            long start = System.currentTimeMillis();
            String sql = sqlResolverHandler.resolve();
            if (log.isInfoEnabled()) {
                log.info("==>[DS:{} {}]: [{}] [拼接耗时:{}ms]", dataSourceName, sqlSource, sql, System.currentTimeMillis() - start);
            }
        } catch (Throwable e) {
            log.warn("==>[{}]: 尝试获取完整 sql 失败:{}, 注：此报错不影响sql执行", sqlSource, e.getMessage(), e);
        }
        return invocation.proceed();
    }

    private String printDataSourceName(){
        if(Objects.nonNull(peekCurrDataSource)){
            try {
                final Object datasourceName = peekCurrDataSource.invoke(null);
                if(log.isDebugEnabled()){
                    log.debug("==>当前数据源：{}", datasourceName);
                }
                return datasourceName != null ? datasourceName.toString() : DEFAULT_DATASOURCE;
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error("获取当前数据源失败", e);
                return DEFAULT_DATASOURCE;
            }
        }
        return DEFAULT_DATASOURCE;
    }

    private boolean isPrint(String mapperClassName, String methodName){
        if(!log.isInfoEnabled()){
            // 不记录日志
            return false;
        }
        return CharSequenceUtil.isNotEmpty(mapperClassName) && CharSequenceUtil.isNotEmpty(methodName) &&
               !classMethodsMap.containsKey(mapperClassName) && CollUtil.isEmpty(classMethodsMap.getOrDefault(mapperClassName, new ArrayList<>()));
    }

    /**
     * 获取sql解析器
     * 注意：mybatisplus 默认使用 MybatisParameterHandler（参见com.baomidou.mybatisplus.core.MybatisConfiguration#MybatisConfiguration()）
     * @param invocation
     * @return
     */
    private ISqlResolverHandler sqlResolverHandler(Invocation invocation) {
        StatementHandler stateMentHandler = (StatementHandler) invocation.getTarget();
        if (stateMentHandler.getParameterHandler() instanceof MybatisParameterHandler){
            return new MybatisParameterSqlResolverHandler(stateMentHandler.getBoundSql(), (MybatisParameterHandler)stateMentHandler.getParameterHandler());
        }else {
            return new DefaultParameterSqlResolverHandler(stateMentHandler.getBoundSql());
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) { /*  document why this method is empty */ }
}