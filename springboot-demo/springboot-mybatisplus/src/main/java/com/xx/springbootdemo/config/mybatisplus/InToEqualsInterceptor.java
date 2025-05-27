package com.xx.springbootdemo.config.mybatisplus;

import cn.hutool.core.text.CharSequenceUtil;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;

import java.sql.Connection;
import java.util.Properties;

@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class InToEqualsInterceptor implements Interceptor {
    private static final String IN_KEYWORD = "IN";
    private static final String QUESTION_MARK = "?";
    private static final String EQUALS = "=";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        String sql = statementHandler.getBoundSql().getSql();
        if (Boolean.FALSE.equals(sql.toUpperCase().contains("IN")) || CharSequenceUtil.count("?", sql) > 64) {
            return invocation.proceed();
        }
        // 将SQL语句中的多余空格替换为单个空格，并移除换行符和逗号周围的空格
        String cleanedSql = sql.replaceAll("[\\s]+", " ")
                .replace("\n", "")
                .replace(" ,", ",")
                .replace(", ", ",")
                .replace("))", ") )");

        try{
            // 调用方法进行正则匹配和处理
            String newSql = processInToEquals(cleanedSql);
            // 调用方法反射设置新的 SQL
            if(!CharSequenceUtil.equals(cleanedSql, newSql)){
                setNewSql(statementHandler, newSql);
            }
        }catch (Throwable e){}
        return invocation.proceed();
    }


    /**
     * 匹配 IN 子句的正则表达式并处理匹配结果
     *
     * @param sql 原始 SQL 语句
     * @return 处理后的 SQL 语句
     */
    private static String processInToEquals(String sql) {
        // 基本输入验证，防止SQL注入
        if (sql == null || sql.trim().isEmpty()) {
            throw new IllegalArgumentException("SQL statement cannot be null or empty");
        }

        String[] sqlKws = sql.split(" ");

        // 使用StringBuilder来构建新的SQL语句
        StringBuilder newSql = new StringBuilder();

        // 遍历SQL关键字数组
        for (int i = 0; i < sqlKws.length; i++) {
            if (!IN_KEYWORD.equalsIgnoreCase(sqlKws[i])) {
                newSql.append(sqlKws[i]).append(" ");
                continue;
            }

            // 确保"IN"后面有参数部分
            if (i + 1 >= sqlKws.length) {
                throw new IllegalArgumentException("Invalid SQL syntax: 'IN' keyword without parameters");
            }

            String inParams = sqlKws[i + 1];
            // 如果"IN"后面的参数部分不包含一个问号，直接复制到新数组中
            if (CharSequenceUtil.count(inParams, QUESTION_MARK) != 1) {
                newSql.append(sqlKws[i]).append(" ");
                continue;
            }

            // 如果"IN"后面的参数只有一个问号，将其替换为"="
            newSql.append(EQUALS).append(" ").append(QUESTION_MARK).append(" ");
            i++; // 跳过下一个关键字，因为它已经被处理过
        }

        // 返回处理后的SQL语句
        return newSql.toString().trim();
    }

    /**
     * 反射设置新的 SQL 语句
     *
     * @param statementHandler StatementHandler 对象
     * @param newSql           新的 SQL 语句
     */
    private void setNewSql(StatementHandler statementHandler, String newSql) throws IllegalAccessException {
        FieldUtils.writeField(statementHandler.getBoundSql(), "sql", newSql, true);
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 可以从配置文件中读取属性（可选）
    }

}