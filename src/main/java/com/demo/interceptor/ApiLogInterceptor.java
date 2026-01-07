package com.demo.interceptor;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.alibaba.fastjson.JSON;
import com.demo.domain.entity.ApiLog;
import com.demo.domain.property.ApiLogProperty;

@Component
public class ApiLogInterceptor implements HandlerInterceptor {
    @Resource
    RedisTemplate<String, Object> redisTemplate;

    @Resource
    private ApiLogProperty apiLogProperty;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Principal userPrincipal = request.getUserPrincipal();
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String requestURI = request.getRequestURI();
        ApiLog apiLog = ApiLog.builder()
                .user(userPrincipal != null ? userPrincipal.getName() : null)
                .method(request.getMethod())
                .uri(requestURI)
                .createTime(createTime)
                .ip(request.getRemoteAddr())
                .build();

        String key = String.format("%s%s%s", apiLogProperty.getRedisKeyPrefix(), createTime.split(" ")[0], requestURI);
        redisTemplate.opsForList().rightPush(
                key,
                JSON.toJSONString(apiLog));

        // 设置过期时间（24小时）
        redisTemplate.expire(key, apiLogProperty.getRedisKeyExpireSeconds(), TimeUnit.SECONDS);
        return true;
    }
}
