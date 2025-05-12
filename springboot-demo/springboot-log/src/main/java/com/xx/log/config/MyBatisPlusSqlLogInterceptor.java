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
 * 存在的问题：
 * 1、如果sql中存在大量动态参数，那么这里输出sql语句会耗时 （动态参数数量将近2万 耗时 12s), 在下面的组建sql语句的方法中提供了一个优化的思路
 *   但是不能使用，会存在类型转换的问题，而且H2会直接报错，mysql虽然不会报错，但是查询出来的结果和预期不一致，仅仅提供了一个思路
 *   如果动态参数不多，还是建议将下面的优化还原为以前的方式，然后再进行使用
 *   如果存在动态参数很多的场景，建议去了解一下p6spy， 也可以将完整的sql语句输出，但是需要修改数据库驱动，它是通过拦截mysql驱动来获取sql语句的，所以输出的语句是完整且符合预期的
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
                // 如果sql中的查询条件存在in, 那么mybatis会将传入的集合或者数组拆分成多个参数，这里在输出sql的时候就会遍历去拼接sql
                // 这样一来的话，就会拖慢程序的执行速度 （in 里面放了将近两万个数据，通过这里输出sql的时候用了12秒左右）
                // 下面是优化改造后的方案，但是还不能真正的使用，只能算是提供了一个思路
                // 思路：让in 后面的条件变成一个，即将一个list中的条件手动拼成一个in的查询条件，比如一个list中包含 1，2,3 三个元素，
                // 那么在查询之前手动将这三个元素拼成一个条件 即 1,2,3 但是这也要求不能使用mybatis plus中的queryWrapper进行查询了
                // 必须自己在xml中手动编写sql，并且如果存在in查询条件的话，不要使用foreach 而是直接写如下的sql语句 in (#{param,typeHandler=""})
                // 注意这里的typeHandler要和下面使用的一样，否则还是不会进入优化后的if去执行
                // 上面的思想主要是利用了mybatis中的typeHandler去处理参数，将list的多个参数整合成一个
                // 后来想想，这里没必要这样搞，直接在入参的时候将list 转成一个String 去查不是一样的么？
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        // 原本组织sql的语句
                        //sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));

                        // 优化后的，根据typeHandler将list的参数整合成一个，然后这里再构建sql
                        TypeHandler<?> typeHandler = parameterMapping.getTypeHandler();
                        if (typeHandler instanceof ListIntegerTypeHandler){
                            List<Integer> list = (List<Integer>)metaObject.getValue(propertyName);
                            String insql = list.stream().map(e->e.toString()).collect(Collectors.joining(","));
                            sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(insql));
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