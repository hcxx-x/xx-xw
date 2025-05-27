package com.xx.springbootdemo.config.mybatisplus.resolver;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.MybatisParameterHandler;
import com.xx.springbootdemo.util.SqlParamerUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.util.List;
import java.util.Objects;

/**
 * mybatisParameterHandler 参数解析
 *
 * @Author zcchu
 * @Date 2022/10/14 15:15
 */
@Slf4j
public class MybatisParameterSqlResolverHandler implements ISqlResolverHandler {

    private final BoundSql boundSql;
    private final TypeHandlerRegistry typeHandlerRegistry;
    private final Configuration configuration;
    private final MappedStatement mappedStatement;

    public MybatisParameterSqlResolverHandler(BoundSql boundSql, MybatisParameterHandler mybatisParameterHandler) {
        this.boundSql = boundSql;
        this.configuration = (Configuration) BeanUtil.getFieldValue(mybatisParameterHandler, "configuration");
        this.typeHandlerRegistry = (TypeHandlerRegistry) BeanUtil.getFieldValue(mybatisParameterHandler, "typeHandlerRegistry");
        this.mappedStatement = (MappedStatement) BeanUtil.getFieldValue(mybatisParameterHandler, "mappedStatement");
    }

    @Override
    public String resolve() {
        Object parameterObject = boundSql.getParameterObject();
        String sql = getBoundSql();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();

        if (Objects.isNull(parameterMappings)) {
            return sql;
        }

        if (parameterMappings.size() > paramSizeOfNotPrintSql && !log.isDebugEnabled()) {
            return SqlParamerUtil.shortSql(sql);
        }

        return replacePlaceholdersWithValues(sql, parameterMappings, parameterObject);
    }

    /**
     * 替换 SQL 语句中的占位符为实际的参数值
     *
     * @param sql               初始 SQL 语句
     * @param parameterMappings 参数映射列表
     * @param parameterObject   参数对象
     * @return 替换后的 SQL 语句
     */
    private String replacePlaceholdersWithValues(String sql, List<ParameterMapping> parameterMappings, Object parameterObject) {
        for (ParameterMapping parameterMapping : parameterMappings) {
            if (parameterMapping.getMode() != ParameterMode.OUT) {
                Object value = getParameterValue(parameterMapping, parameterObject);
                JdbcType jdbcType = parameterMapping.getJdbcType();
                if (value == null && jdbcType == null) {
                    jdbcType = this.configuration.getJdbcTypeForNull();
                }
                sql = sql.replaceFirst("\\?", SqlParamerUtil.quoteReplacement(SqlParamerUtil.getParameterValue(value, jdbcType)));
            }
        }
        return sql;
    }

    /**
     * 根据参数映射和参数对象获取参数值
     *
     * @param parameterMapping 参数映射
     * @param parameterObject  参数对象
     * @return 参数值
     */
    private Object getParameterValue(ParameterMapping parameterMapping, Object parameterObject) {
        Object value;
        String propertyName = parameterMapping.getProperty();
        if (boundSql.hasAdditionalParameter(propertyName)) { // issue #448 ask first for additional params
            value = boundSql.getAdditionalParameter(propertyName);
        } else if (parameterObject == null) {
            value = null;
        } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
            value = parameterObject;
        } else {
            MetaObject metaObject = configuration.newMetaObject(parameterObject);
            value = metaObject.getValue(propertyName);
        }
        return value;
    }

    @Override
    public String getBoundSql() {
        return boundSql.getSql().replaceAll("[\\s]+", " ").replace("\n", "");
    }

    @Override
    public String sqlSource() {
        return mappedStatement.getId();
    }
}
