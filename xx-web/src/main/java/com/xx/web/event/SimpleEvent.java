package com.xx.web.event;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

/**
 * @author hanyangyang
 */
@Data
public class SimpleEvent extends ApplicationEvent {
    public SimpleEvent(Object source) {
        super(source);
    }
}
