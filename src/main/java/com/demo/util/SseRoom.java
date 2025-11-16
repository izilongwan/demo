package com.demo.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SseRoom {

    /**
     * 当前连接数
     */
    private AtomicInteger count = new AtomicInteger(0);

    /**
     * 使用map对象，便于根据userId来获取对应的SseEmitter，或者放redis里面
     */
    private Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    public Map<String, SseEmitter> getRoom() {
        return this.sseEmitterMap;
    }

    /**
     * 创建用户连接并返回 SseEmitter
     *
     * @param userId 用户ID
     * @return SseEmitter
     */
    public SseEmitter connect(String userId) {

        if (sseEmitterMap.containsKey(userId)) {
            return sseEmitterMap.get(userId);
        }

        try {
            /**
             * 设置超时时间，0表示不过期。默认30秒
             */
            SseEmitter sseEmitter = new SseEmitter(0L);
            /**
             * 注册回调
             */
            sseEmitter.onCompletion(completionCallBack(userId));
            sseEmitter.onError(errorCallBack(userId));
            sseEmitter.onTimeout(timeoutCallBack(userId));
            sseEmitterMap.put(userId, sseEmitter);
            /**
             * 数量+1
             */
            count.getAndIncrement();

            return sseEmitter;
        } catch (Exception e) {
            log.info("创建新的sse连接异常，当前用户：{}", userId);
        }

        return null;
    }

    /**
     * 给指定用户发送消息
     *
     * @auther: izilongwan
     */
    public void sendMessage(String userId, Object message) {
        if (!sseEmitterMap.containsKey(userId)) {
            return;
        }

        sendMessage(userId, sseEmitterMap.get(userId), message);
    }

    private void sendMessage(String userId, SseEmitter sse, Object message) {
        try {
            sse.send(message, MediaType.APPLICATION_JSON);
        } catch (IOException e) {
            log.error("用户[{}]推送异常:{}", userId, e.getMessage());
            disconnect(userId);
        }
    }

    /**
     * 向同组人发布消息 （要求userId+groupId）
     *
     * @auther: izilongwan
     */
    public void sendGroupMessage(String groupId, Object message) {
        if (sseEmitterMap.size() <= 0) {
            return;
        }

        sseEmitterMap.forEach((k, v) -> {
            if (k.startsWith(groupId)) {
                sendMessage(k, v, message);
            }
        });
    }

    /**
     * 广播群发消息
     *
     * @auther: izilongwan
     */
    public void sendBatchMessage(Object message) {
        sseEmitterMap.forEach((k, v) -> sendMessage(k, v, message));
    }

    /**
     * 群发消息
     *
     * @auther: izilongwan
     */
    public void batchSendMessage(Object message, Set<String> ids) {
        ids.forEach(userId -> sendMessage(userId, message));
    }

    /**
     * 移除用户连接
     */
    public SseEmitter disconnect(String userId) {
        SseEmitter emitter = sseEmitterMap.remove(userId);
        count.getAndDecrement();
        log.info("移除用户：{}", userId);
        return emitter;
    }

    /**
     * 获取当前连接信息
     */
    public List<String> getIds() {
        return new ArrayList<>(sseEmitterMap.keySet());
    }

    /**
     * 获取当前连接数量
     */
    public int getUserCount() {
        return count.intValue();
    }

    private Runnable completionCallBack(String userId) {
        return () -> {
            log.info("结束连接：{}", userId);
            disconnect(userId);
        };
    }

    private Runnable timeoutCallBack(String userId) {
        return () -> {
            log.info("连接超时：{}", userId);
            disconnect(userId);
        };
    }

    private Consumer<Throwable> errorCallBack(String userId) {
        return throwable -> {
            log.info("连接异常：{}", userId);
            disconnect(userId);
        };
    }
}
