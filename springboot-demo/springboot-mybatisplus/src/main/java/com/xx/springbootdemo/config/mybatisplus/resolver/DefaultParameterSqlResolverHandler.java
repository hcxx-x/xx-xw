package com.xx.springbootdemo.config.mybatisplus.resolver;


import com.xx.springbootdemo.util.SqlParamerUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.type.JdbcType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 缺省sql解析器
 *
 * @Author zcchu
 * @Date 2022/10/14 15:15
 */
@Slf4j
public class DefaultParameterSqlResolverHandler implements ISqlResolverHandler {
    private static final String PREFIX = "__frch_";
    private static final String ITEM_SUFFIX = "Item";

    private final BoundSql boundSql;

    public DefaultParameterSqlResolverHandler(BoundSql boundSql) {
        this.boundSql = boundSql;
    }

    @Override
    public String resolve() {
        String sql = getBoundSql();
        final List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (Objects.isNull(parameterMappings)) {
            return sql;
        }
        if (parameterMappings.size() > paramSizeOfNotPrintSql && !log.isDebugEnabled()) {
            return SqlParamerUtil.shortSql(sql);
        }

        final MetaObject metaObject = MetaObject.forObject(boundSql.getParameterObject(), new DefaultObjectFactory(), new DefaultObjectWrapperFactory(), new DefaultReflectorFactory());
        final Map<String, Integer> nameMinMap = new HashMap<>();
        boolean isPageFirst = parameterMappings.stream().noneMatch(m -> "mybatis_plus_second".equals(m.getProperty()));

        for (ParameterMapping mapping : parameterMappings) {
            String propertyName = mapping.getProperty();
            Object value = null;

            if (!isPageFirst && "mybatis_plus_second".equals(propertyName)) {
                sql = parsePage(metaObject, sql);
                continue;
            }

            if (!isPageFirst && "mybatis_plus_first".equals(propertyName)) {
                continue;
            }

            if (propertyName.startsWith(PREFIX)) {
                value = handlePrefixedParameters(propertyName, metaObject, nameMinMap);
            } else if ("mybatis_plus_first".equals(propertyName)) {
                value = handleMyBatisPlusFirstParameter(boundSql, metaObject);
            } else {
                value = handleOtherParameters(propertyName, boundSql, metaObject);
            }

            sql = replacePlaceholderInSql(sql, value, mapping);
        }
        return sql;
    }

    private Object handlePrefixedParameters(String propertyName, MetaObject metaObject, Map<String, Integer> nameMinMap) {
        String[] str = propertyName.substring(PREFIX.length()).split("_");
        String name = str[0].replace(ITEM_SUFFIX, "");
        String index = str[1];
        if (nameMinMap.get(name) == null) {
            nameMinMap.put(name, Integer.parseInt(index));
            return metaObject.getValue(name + "[" + 0 + "]");
        } else {
            return metaObject.getValue(name + "[" + (Integer.parseInt(index) - nameMinMap.get(name)) + "]");
        }
    }

    private Object handleMyBatisPlusFirstParameter(BoundSql boundSql, MetaObject metaObject) {
        return boundSql.hasAdditionalParameter("mybatis_plus_first") ? boundSql.getAdditionalParameter("mybatis_plus_first") : metaObject.getValue("page.size");
    }

    private Object handleOtherParameters(String propertyName, BoundSql boundSql, MetaObject metaObject) {
        if (metaObject.hasGetter(propertyName)) {
            return metaObject.getValue(propertyName);
        } else if (boundSql.hasAdditionalParameter(propertyName)) {
            return boundSql.getAdditionalParameter(propertyName);
        } else {
            return "解析器错误, 值可能是: " + boundSql.getParameterObject().toString();
        }
    }

    private String replacePlaceholderInSql(String sql, Object value, ParameterMapping mapping) {
        return sql.replaceFirst("\\?", SqlParamerUtil.quoteReplacement(SqlParamerUtil.getParameterValue(value, mapping.getJdbcType())));
    }

    @Override
    public String sqlSource() {
        return "";
    }


    private String parsePage(MetaObject metaObject, String sql, String argsName) {
        long start = (Long) metaObject.getValue(argsName + ".current");
        start = start > 1 ? (start - 1) * (Long) metaObject.getValue(argsName + ".size") : (Long) metaObject.getValue(argsName + ".size");
        return sql.replaceFirst("\\?", SqlParamerUtil.quoteReplacement(SqlParamerUtil.getParameterValue(start, JdbcType.INTEGER)))
                .replaceFirst("\\?", SqlParamerUtil.quoteReplacement(SqlParamerUtil.getParameterValue(metaObject.getValue(argsName + ".size"), JdbcType.INTEGER)));
    }

    private String parsePage(MetaObject metaObject, String sql) {
        String[] argsNames = {"arg0", "param1", "param2"};
        for (String argsName: argsNames) {
            try {
                return parsePage(metaObject, sql, argsName);
            } catch (Throwable e) {}
        }
        return sql.replaceFirst("\\?", "解析错误").replaceFirst("\\?", "解析错误");
    }

    @Override
    public String getBoundSql() {
        return boundSql.getSql().replaceAll("[\\s]+", " ").replace("\n", "");
    }
}
