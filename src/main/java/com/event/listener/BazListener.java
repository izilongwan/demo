package com.event.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.event.BazEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BazListener {
    @EventListener({ BazEvent.class })
    public void listener(BazEvent event) {
        Object source = event.getSource();
        log.info("{}", source);
    }
}
