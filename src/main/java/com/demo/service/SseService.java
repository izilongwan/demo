package com.demo.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseService {
    SseEmitter connect(String roomId, String userId);

    boolean disconnect(String roomId, String userId);

    boolean sendMessage(String roomId, String userId, Object message);

    boolean sendMessage(String roomId, Object message);

}
