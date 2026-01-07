package com.demo.domain.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.Builder.Default;

@Data
@Configuration
@ConfigurationProperties(prefix = "api-log")
public class ApiLogProperty {
    private String redisKeyPrefix = "api_logs:";
    private int redisKeyExpireSeconds = 86400;
    private int batchSize = 500;
}
