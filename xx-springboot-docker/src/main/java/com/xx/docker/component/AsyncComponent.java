package com.xx.docker.component;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author hanyangyang
 * @since 2023/3/8
 */
@Component
public class AsyncComponent {

    @Async
    public void asyncExecutorTask(){
        System.out.println("name:"+Thread.currentThread().getName()+"id:"+Thread.currentThread().getId());
    }
}
