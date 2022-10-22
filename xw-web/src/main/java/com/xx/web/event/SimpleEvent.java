package com.xx.web.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author hanyangyang
 */
public class SimpleEvent extends ApplicationEvent {
    public SimpleEvent(Object source) {
        super(source);
    }
}
