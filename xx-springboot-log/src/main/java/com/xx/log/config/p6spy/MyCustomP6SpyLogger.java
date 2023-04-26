package com.xx.log.config.p6spy;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author hanyangyang
 * @since 2023/3/29
 */
public class MyCustomP6SpyLogger implements MessageFormattingStrategy {
    /**
     * @Desc: 重写日志格式方法
     * now:当前时间
     * elapsed:执行耗时
     * category：执行分组
     * prepared：预编译sql语句
     * sql:执行的真实SQL语句，已替换占位
     */
    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        return !"".equals(sql.trim()) ? "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS")) + "] --- | 耗时："
                + String.format("%-7s", elapsed+"ms")+"| " + category + " | connection " + connectionId + " | "
                + sql + ";" : "";
    }
}

