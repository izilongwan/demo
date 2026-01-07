package com.demo.config;

import javax.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.demo.domain.property.CorsProperty;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Resource
    CorsProperty corsPropertyConfig;

    // 这里可以根据需要重写 WebMvcConfigurer 的方法来配置 CORS 策略
    @Override
    public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
        registry.addMapping(corsPropertyConfig.getAllowedMappings()) // 允许的路径映射
                .allowedOrigins(corsPropertyConfig.getAllowedOrigins()) // 允许所有来源
                .allowedMethods(corsPropertyConfig.getAllowedMethods()) // 允许的HTTP方法
                .allowedHeaders(corsPropertyConfig.getAllowedHeaders()) // 允许所有请求头
                .allowCredentials(corsPropertyConfig.isAllowCredentials()) // 是否允许携带凭证
                .maxAge(corsPropertyConfig.getMaxAge()); // 预检请求的缓存时间（秒）
    }
}
