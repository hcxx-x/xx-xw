package com.xx.log.config;


import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.xx.log.config.mybatis.th.ListIntegerTypeHandler;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/*
 *描述: 用于输出格式化好的SQL
 * 存在的问题：
 *  如果没有使用mybatis-plus的自动填充功能的话，可以在beforeQuery、beforeUpdate中完成sql的拼接以及组装来获取完整的sql
 *  但是如果使用mybatis-plus的自动填充功能的话就没有办法在beforeQuery、beforeUpdate方法中获取完整的sql,
 *  因为beforeQuery、beforeUpdate方法会在自动填充的代码执行前执行，这就导致属性还没有填充完成，sql就被打印了，所以不完整
 *
 * 解决：
 *  可以在重写beforePrepare方法，在这个方法中完成sql的打印，这个方法是在自动填充代码执行之后执行的。
 *  但是在这个方法中只能获取到BoundSql，获取不到Configuration以及sqlId(对应的mapper中的方法的全路径)，
 *  所以需要和beforeQuery、beforeUpdate搭配使用，在上面这两个方法中获取Configuration和sqlId，并设置成成员变量
 *  然后在beforePrepare中使用
 *
 * 可能存在的问题:
 *  1、不知道会不会打印所有的sql语句
 *  2、不知道是否存在并发问题
 *
 *
 */
public class MyBatisPlusSqlLogInterceptor implements InnerInterceptor {
    private static Logger logger = LoggerFactory.getLogger(MyBatisPlusSqlLogInterceptor.class);
    private static boolean printSQL = true;
    public static void startPrintSQL(){
        printSQL = Boolean.TRUE;
    }

    private Configuration configuration;

    private String sqlId;

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        if(printSQL) {
            MappedStatement mappedStatement = ms;
            String sqlId = mappedStatement.getId();
            this.sqlId = sqlId;
            Configuration configuration = mappedStatement.getConfiguration();
            this.configuration=configuration;

            /*
             *  如果没有使用mybatis-plus的自动填充功能的话，可以在这里直接打印sql，但是如果使用了mybatis-plus的自动填充功能的话，
             *  这里获取的sql是不完全的，因为这个方法会在自动填充之前执行。
             *  解决方法就是设置成员变量，然后在这个方法里面将获取到的configuration 和 sqlId(执行方法的全路径) 设置到成员属性中，然后在beforePrepare中打印sql,这样获取的sql是完整的
             */
            /*String sql = getSql(configuration, boundSql, sqlId);
            logger.info(sql);*/
        }
    }

    @Override
    public void beforeUpdate(Executor executor, MappedStatement ms, Object parameter) throws SQLException {

        if(printSQL) {
            MappedStatement mappedStatement = ms;
            String sqlId = mappedStatement.getId();
            this.sqlId = sqlId;
            Configuration configuration = mappedStatement.getConfiguration();
            this.configuration=configuration;
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);

            /*
             *  如果没有使用mybatis-plus的自动填充功能的话，可以在这里直接打印sql，但是如果使用了mybatis-plus的自动填充功能的话，
             *  这里获取的sql是不完全的，因为这个方法会在自动填充之前执行。
             *  解决方法就是设置成员变量，然后在这个方法里面将获取到的configuration 和 sqlId(执行方法的全路径) 设置到成员属性中，然后在beforePrepare中打印sql,这样获取的sql是完整的
             */
            /*String sql = getSql(configuration, boundSql, sqlId);
            logger.info(sql);*/
        }
    }

    public static String getSql(Configuration configuration, BoundSql boundSql, String sqlId ) {
        try {
            String sql = showSql(configuration, boundSql);
            StringBuilder str = new StringBuilder(100);
            str.append(sqlId);
            str.append(" ==> ");
            str.append(sql);
            str.append(";");
            return str.toString();
        } catch(Error e) {
            logger.error("解析 sql 异常", e);
        }
        return "";
    }
    public static String showSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (parameterMappings != null && parameterMappings.size() > 0 && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(parameterObject)));
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        TypeHandler<?> typeHandler = parameterMapping.getTypeHandler();
                        if (typeHandler instanceof ListIntegerTypeHandler){
                            List<Integer> list = (List<Integer>)metaObject.getValue(propertyName);
                            String insql = list.stream().map(e->e.toString()).collect(Collectors.joining(","));
                            sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(insql)));
                        }else{
                            sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
                        }
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
                    }
                }
            }
        }
        return sql;
    }

    private static String getParameterValue(Object obj) {
        String value = null;
        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format(obj) + "'";
        }else if(obj instanceof LocalDate){
            value = "'" + ((LocalDate) obj).format( DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'";
        }else if(obj instanceof LocalDateTime){
            value = "'" + ((LocalDateTime) obj).format( DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "'";

        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }
        }
        return value;
    }

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        logger.info("print beforePrepare");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String sql = getSql(configuration, sh.getBoundSql(),sqlId);
        logger.info(sql);
        stopWatch.stop();
        logger.info("sql 输出结束，耗时：{}ms",stopWatch.getTotalTimeMillis());

        InnerInterceptor.super.beforePrepare(sh, connection, transactionTimeout);
    }

}