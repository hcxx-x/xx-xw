package com.xx.web.context;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hanyangyang
 */
@Slf4j
public class ForestThreadLocalContext {
    private static final ThreadLocal<Map<String, String>> context = new ThreadLocal<Map<String, String>>() {
        @Override
        protected Map<String, String> initialValue() {
            return new HashMap<String, String>(16);
        }
    };

    public static void setAttribute(String key, String value) {
        log.info("context设置属性，当前线程id：{},value:{}",Thread.currentThread().getId(), value);
        context.get().put(key, value);
    }

    public static String getAttribute(String key) {
        log.info("context获取属性，当前线程id：{}",Thread.currentThread().getId());
        return context.get().get(key);
    }
}
