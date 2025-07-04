package com.xx.log.config.p6spy;

import com.p6spy.engine.common.P6Util;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author hanyangyang
 * @since 2023/3/29
 */
public class MyCustomP6SpyLogger implements MessageFormattingStrategy {
    /**
     * 输出正常执行的sql,执行错误的sql不通过这个方法输出
     * @Desc: 重写日志格式方法
     * now:当前时间
     * elapsed:执行耗时
     * category：执行分组
     * prepared：预编译sql语句
     * sql:执行的真实SQL语句，已替换占位
     *
     *  sql.replaceAll("\\s{2,}," ") 匹配大于2个空白字符串，并将其转成一个空白字符，和P6Util.singleLine(sql) 的作用一样
     *  简单来说就是吧多个空格，或者空格+换行等等之类的替换成一个空格，目的就是让sql在一行输出
     */
    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        return !"".equals(sql.trim()) ? "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS")) + "] --- | 耗时："
                + String.format("%-7s", elapsed+"ms")+"| " + category + " | connection " + connectionId + " | "
                + P6Util.singleLine(sql) + ";" : "";
    }
}

