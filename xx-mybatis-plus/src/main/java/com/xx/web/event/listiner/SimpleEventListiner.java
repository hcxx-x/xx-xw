package com.xx.web.event.listiner;

import cn.hutool.core.util.ObjectUtil;
import com.xx.web.client.ForestTestClient;
import com.xx.web.context.ForestThreadLocalContext;
import com.xx.web.event.SimpleEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author hanyangyang
 */
@Slf4j
@Component
public class SimpleEventListiner {
    @Autowired
    private ForestTestClient forestTestClient;

    @Async
    @Order
    @EventListener(SimpleEvent.class)
    public void notify(SimpleEvent event) {
        String value = (String) event.getSource();
        log.info("时间监听，当前线程id：{},内容：{}",Thread.currentThread().getId(),value);
        ForestThreadLocalContext.setAttribute("key", value);
        String httpValue = forestTestClient.helloForest();
        log.info("forest调用结果：{}",value);
        if (!ObjectUtil.equal(httpValue,value)){
            log.error("出现异常.......");
            throw new RuntimeException("出现并发问题");
        }
    }
}
