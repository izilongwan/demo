package com.event;

import org.springframework.context.ApplicationEvent;

public class BazEvent extends ApplicationEvent {

    public BazEvent(Object source) {
        super(source);
    }

}
