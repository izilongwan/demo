package com.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(name = "test.value", havingValue = "true")
@ConditionalOnExpression
public class TestConfig {
    @Value("${test.value}")
    boolean testIntValue;

    @Bean
    @ConditionalOnMissingBean
    public String testValue() {
        return "testValue";
    }
}
