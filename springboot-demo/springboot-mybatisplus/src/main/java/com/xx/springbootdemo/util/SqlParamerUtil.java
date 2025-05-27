package com.xx.springbootdemo.util;

import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.core.MybatisParameterHandler;
import com.fasterxml.jackson.annotation.JsonValue;

import com.xx.springbootdemo.config.mybatisplus.resolver.DefaultParameterSqlResolverHandler;
import com.xx.springbootdemo.config.mybatisplus.resolver.ISqlResolverHandler;
import com.xx.springbootdemo.config.mybatisplus.resolver.MybatisParameterSqlResolverHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.type.JdbcType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author zcchu
 * @Date 2022/9/13 15:54
 */
@Slf4j
public class SqlParamerUtil {
    private static final DateFormat DATE_FORMATTER = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
    private static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private SqlParamerUtil(){}
    /**
     * 获取SQL解析处理器
     *
     * @param invocation 调用对象，包含目标对象和参数等信息
     * @return ISqlResolverHandler SQL解析处理器实例
     */
    public static ISqlResolverHandler getSqlResolverHandler(Invocation invocation) {
        // 从调用对象中获取目标对象，并将其转换为StatementHandler类型
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();

        // 判断参数处理器是否为MybatisParameterHandler的实例
        if (statementHandler.getParameterHandler() instanceof MybatisParameterHandler) {
            // 如果是MybatisParameterHandler的实例，则返回MybatisParameterSqlResolverHandler实例
            return new MybatisParameterSqlResolverHandler(statementHandler.getBoundSql(), (MybatisParameterHandler) statementHandler.getParameterHandler());
        } else {
            // 如果不是MybatisParameterHandler的实例，则返回DefaultParameterSqlResolverHandler实例
            return new DefaultParameterSqlResolverHandler(statementHandler.getBoundSql());
        }
    }

    public static String quoteReplacement(String s) {
        if (Objects.isNull(s)) {
            return "null";
        }
        return Matcher.quoteReplacement(s);

    }

    /**
     * 如果参数是String，则添加单引号，如果是日期，则转换为时间格式器并加单引号；对参数是null和不是null的情况作了处理
     *
     * @param obj      参数对象
     * @param jdbcType JDBC类型
     * @return 格式化后的字符串
     */
    public static String getParameterValue(final Object obj, final JdbcType jdbcType) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return "'" + obj + "'";
        } else if (obj instanceof Date) {
            return "'" + DATE_FORMATTER.format(new Date()) + "'";
        } else if (obj instanceof LocalDate) {
            return "'" + LOCAL_DATE_FORMATTER.format((LocalDate) obj) + "'";
        } else if (obj instanceof LocalDateTime) {
            return "'" + LOCAL_DATE_TIME_FORMATTER.format((LocalDateTime) obj) + "'";
        } else if (obj instanceof Enum) {
            Optional<Field> optionalField = getValueByEnum(obj, EnumValue.class);
            if (!optionalField.isPresent()) {
                optionalField = getValueByEnum(obj, JsonValue.class);
            }
            if (optionalField.isPresent()) {
                try {
                    optionalField.get().setAccessible(true);
                    return getParameterValue(optionalField.get().get(obj), jdbcType);
                } catch (IllegalAccessException e) {
                    // 记录日志或抛出自定义异常
                    throw new RuntimeException("Failed to access enum field", e);
                }
            }
        } else if (obj instanceof Number || obj instanceof Boolean || obj.getClass().isPrimitive()) {
            return obj.toString();
        }
        return "'" + obj + "'";
    }

    private static Optional<Field> getValueByEnum(Object obj, Class<? extends Annotation> ann) {
        return Arrays.stream(obj.getClass().getDeclaredFields()).filter(field -> Objects.nonNull(field.getAnnotation(ann))).findFirst();
    }


    public static String shortSql(String sql) {
        // 连续空格替换成一个空格，括号与括号之间规整方式，使得'...), (...'，整体压缩sql
        sql = sql.replaceAll("\\s+", " ").replaceAll("\\)\\s*,\\s*\\(", "), (");

        // 如果SQL不是以insert开头，则调用shortSqlIn进行处理
        if (!sql.trim().toLowerCase().startsWith("insert")) {
            return shortSqlIn(sql);
        }

        // 定义匹配模式，用于查找需要省略的部分
        String pattern = "[\\s]*,[\\s]*\\([\\?\\s,]*\\)";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(sql);

        // 如果找到匹配的模式，进行处理
        if (matcher.find()) {
            String matchedGroup = matcher.group();
            int groupCount = CharSequenceUtil.count(sql, matchedGroup);
            String replacement = String.format(", ...后续省略%d组( ?, ? ... ? ) ", groupCount);
            String tempSql = matcher.replaceFirst(replacement);

            // 再次匹配剩余部分，并进行替换
            Matcher childMatcher = compiledPattern.matcher(tempSql);
            if (childMatcher.find()) {
                return childMatcher.replaceAll("");
            }
            return tempSql;
        }
        return sql;
    }

    private static String shortSqlIn(String sql) {
        if (sql == null || sql.isEmpty()) {
            return sql;
        }

        final int inIndex = sql.indexOf(" IN ");
        if (inIndex >= 0) {
            final int leftParenIndex = sql.indexOf("(", inIndex);
            final int rightParenIndex = sql.indexOf(")", leftParenIndex);
            if (leftParenIndex < 0 || rightParenIndex < 0) {
                return sql; // 不完整的括号对，直接返回原SQL
            }

            final String subStr = sql.substring(leftParenIndex + 1, rightParenIndex);
            if (subStr.contains("(")) {
                return sql; // 子字符串中包含括号，直接返回原SQL
            }

            // 使用正则表达式统计问号数量
            long questionMarkCount = subStr.chars().filter(ch -> ch == '?').count();
            if (questionMarkCount <= 2) {
                return sql; // 如果问号少于等于2个，不需要替换
            }

            return sql.replace(subStr, String.format("?,?...省略%d个?", questionMarkCount - 2));
        }
        return sql;
    }

//    public static void main(String[] args) {
//        StringBuilder sql = new StringBuilder("INSERT INTO commodity_comment ( `unique_id`, `title`, `connection_name`, `product_code`, `product_id`, `spu`, `sku`, `content`, `data_level`, `aspect`, `escore`, `score`, `source_name`, `c_time`, `u_name`, `u_img`, `is_default`, `is_plus`, `url`, `pictures`, `videos`, `configs`, `category`, `brand_name`, `store_id`, `store_name`, `project_name`, `tag_name`, `detail`, `oid`, `group_id`, `parent`, `insert_timestamp`, `created_at` ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) , ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ");
//        System.out.println(shortSql(sql.toString()));
//
//        String x = "INSERT INTO commodity_comment ( `unique_id`, `title`, `connection_name`, `product_code`, `product_id`, `spu`, `sku`, `content`, `data_level`, `aspect`, `escore`, `score`, `source_name`, `c_time`, `u_name`, `u_img`, `is_default`, `is_plus`, `url`, `pictures`, `videos`, `configs`, `category`, `brand_name`, `store_id`, `store_name`, `project_name`, `tag_name`, `detail`, `oid`, `group_id`, `parent`, `insert_timestamp`, `created_at`, `create_time`, `update_time` ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ), ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ),( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ,( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
//        System.out.println(shortSql(x.toString()));
//    }
}
