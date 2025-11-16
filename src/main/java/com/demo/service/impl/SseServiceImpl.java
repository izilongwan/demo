package com.demo.service.impl;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.demo.service.SseService;
import com.demo.util.SseRoom;

@Service
public class SseServiceImpl implements SseService {
    private static Map<String, SseRoom> sseRoomMap = new ConcurrentHashMap<>();

    private SseRoom getSseRoom(String roomId) {
        return this.getSseRoom(roomId, null);
    }

    private SseRoom getSseRoom(String roomId, String userId) {
        SseRoom room = null;
        if (sseRoomMap.containsKey(roomId)) {
            room = sseRoomMap.get(roomId);
        } else if (Objects.nonNull(userId)) {
            SseRoom sseRoom = new SseRoom();
            sseRoom.connect(userId);
            sseRoomMap.put(roomId, sseRoom);
        }
        return room;
    }

    @Override
    public SseEmitter connect(String roomId, String userId) {
        return Optional.of(this.getSseRoom(roomId, userId))
                .map(SseRoom::getRoom)
                .map(o -> o.get(userId))
                .orElse(null);
    }

    @Override
    public boolean disconnect(String roomId, String userId) {
        return Optional.of(this.getSseRoom(roomId))
                .map(room -> room.disconnect(userId))
                .map(Objects::nonNull)
                .orElse(false);
    }

    @Override
    public boolean sendMessage(String roomId, String userId, Object message) {
        return Optional.of(this.getSseRoom(roomId, userId))
                .map(room -> {
                    room.sendMessage(userId, message);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public boolean sendMessage(String roomId, Object message) {
        return Optional.of(this.getSseRoom(roomId))
                .map(room -> {
                    room.sendBatchMessage(message);
                    return true;
                })
                .orElse(false);
    }

}
