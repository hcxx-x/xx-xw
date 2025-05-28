package com.xx.springbootdemo.config.mybatisplus;



import com.alibaba.fastjson.JSON;
import com.xx.springbootdemo.config.LogInterceptor;
import com.xx.springbootdemo.config.mybatisplus.resolver.ISqlResolverHandler;
import com.xx.springbootdemo.util.SqlParamerUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.slf4j.MDC;

import java.sql.Statement;
import java.util.Collection;

/**
 * sql执行耗时及结果打印拦截器
 * <br>
 * 注：日志级别高于INFO时，不会执行具体解析记录逻辑，不影响性能
 *
 * @Author zcchu
 * @Date 2023/7/6 17:43
 */
@Slf4j
@Intercepts(value = {
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})
})
public class SqlExecInterceptor implements Interceptor {
    /**
     * 拦截方法调用，记录执行时间和结果日志。
     *
     * @param invocation 被拦截的方法调用对象
     * @return 方法调用的结果
     * @throws Throwable 如果方法调用过程中发生异常
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 如果日志级别不是INFO，则直接执行方法并返回结果
        if (!log.isInfoEnabled()) {
            return invocation.proceed();
        }

        // 记录开始时间
        long start = System.currentTimeMillis();
        // 执行被拦截的方法
        Object result = invocation.proceed();
        // 计算执行时间
        long times = System.currentTimeMillis() - start;

        // 获取SQL解析处理器
        ISqlResolverHandler sqlResolverHandler = SqlParamerUtil.getSqlResolverHandler(invocation);
        // 获取SQL源字符串
        String sqlSource = sqlResolverHandler.sqlSource();
        String boundSql = sqlResolverHandler.getBoundSql();

        // 记录执行时间日志
        logExecutionTime(sqlSource, boundSql, times);
        // 记录结果和执行时间日志
        logResult(sqlSource, result, times);

        // 返回方法调用结果
        return result;
    }

    /**
     * 记录方法执行时间的日志
     *
     * @param boundSql
     * @param times    方法执行时间（毫秒）
     */
    private void logExecutionTime(String sqlSource, String boundSql, long times) {
        // 如果执行时间超过5000毫秒，则记录日志
        if (times > 5000) {
            log.warn("慢SQL: [{}] - [{}] - 【{}】 - 【{}】毫秒",
                    MDC.get(LogInterceptor.RID),
                    sqlSource,
                    boundSql,
                    times);
        }
    }

    /**
     * 记录方法执行结果的日志信息。
     *
     * @param sqlSource      SQL源字符串
     * @param result          方法执行的结果
     * @param times           方法执行的时间（毫秒）
     */
    private void logResult(String sqlSource, Object result, long times) {
        // 如果调试级别日志已启用
        if (log.isDebugEnabled()) {
            // 如果结果是集合类型
            if (result instanceof Collection) {
                // 获取集合的大小
                int size = ((Collection<?>) result).size();
                // 如果集合大小大于16，记录详细日志信息
                if (size > 16) {
                    log.debug("<==[{}]: 查询到数量: {}, 耗时:{}ms", sqlSource, size, times);
                }
            }
            // 记录执行结果和耗时的调试日志信息
            log.debug("<==[{}]: 执行结果: {}, 耗时:{}ms", sqlSource, JSON.toJSONString(result), times);
        } else if (log.isInfoEnabled()) { // 如果信息级别日志已启用
            // 如果结果是集合类型
            if (result instanceof Collection) {
                // 获取集合的大小
                int size = ((Collection<?>) result).size();
                // 如果集合大小为1，记录单条记录的信息日志
                if (size == 1) {
                    log.info("<==[{}]: 查询到结果:{} , 执行耗时:{}ms", sqlSource, JSON.toJSONString(((Collection<?>) result).toArray()[0]), times);
                } else { // 如果集合大小不为1，记录记录数的信息日志
                    log.info("<==[{}]: 查询到记录:{} 条, 执行耗时:{}ms", sqlSource, size, times);
                }
            } else { // 如果结果不是集合类型，只记录执行耗时的信息日志
                log.info("<==[{}]: 执行耗时:{}ms", sqlSource, times);
            }
        }
    }
}