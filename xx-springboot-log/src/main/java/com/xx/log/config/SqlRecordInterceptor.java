package com.xx.log.config;

import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 记录Sql语句的拦截器，用来显示完整的sql语句，基于mybatis的插件进行拦截
 * @author chensh
 * @date 2017年11月6日 上午9:33:39
 */
@Slf4j
public class SqlRecordInterceptor implements InnerInterceptor {
    
    @Override
    @SuppressWarnings("rawtypes")
    public void beforeQuery(Executor executor, MappedStatement mappedStatement, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        String[] split = mappedStatement.getId().split("\\.");
        // mapper接口的名字
        String mapperClassName = split[split.length - 2];
        // 接口的方法名字
        String methodName = split[split.length - 1];
        // sql语句
        String realSql = this.getSql(mappedStatement.getConfiguration(), mappedStatement.getBoundSql(parameter));
        log.info("{}.{}的SQL：{} ", mapperClassName, methodName, realSql);
    }
    
    @Override
    public void beforeUpdate(Executor executor, MappedStatement mappedStatement, Object parameter) throws SQLException {
        this.beforeQuery(executor, mappedStatement, parameter, null, null, null);
    }

    private String getSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (parameterMappings.size() > 0 && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", this.getParameterValue(parameterObject));
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", this.getParameterValue(obj));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", this.getParameterValue(obj));
                    }
                }
            }
        }
        return sql;
    }

    private String getParameterValue(Object obj) {
        String value;
        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format(obj) + "'";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }
        }
        // 处理特殊字符$, 会引发正则group引用
        if (value.contains("$")) {
            StringBuilder sb = new StringBuilder();
            char[] chs = value.toCharArray();
            for (char ch : chs) {
                if (ch == '$') {
                    sb.append("\\");
                }
                sb.append(ch);
            }
            value = sb.toString();
        }
        return value;
    }

}
