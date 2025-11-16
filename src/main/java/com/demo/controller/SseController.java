package com.demo.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.demo.service.SseService;
import com.mico.app.common.aspect.anno.WebResponseAnno;

@RestController
@RequestMapping("sse")
@CrossOrigin
public class SseController {
    @Resource
    SseService sseService;

    private String getClientIP(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    @WebResponseAnno(false)
    @GetMapping(value = "connect/{roomId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@PathVariable String roomId, HttpServletRequest request) {
        return sseService.connect(roomId, getClientIP(request));
    }

    @PostMapping("sendMessage/{roomId}/{userId}")
    public boolean sendMessage(@PathVariable String roomId, @PathVariable String userId, HttpServletRequest request,
            @RequestBody Object message) {
        return sseService.sendMessage(roomId, userId, message);
    }

    @PostMapping("sendMessage/{roomId}")
    public boolean sendBatchMessage(@PathVariable String roomId, HttpServletRequest request,
            @RequestBody Object message) {
        return sseService.sendMessage(roomId, message);
    }

    @GetMapping("disconnect/{roomId}")
    public boolean disconnect(@PathVariable String roomId, HttpServletRequest request) {
        return sseService.disconnect(roomId, getClientIP(request));
    }

}
