package com.demo.schedule;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.demo.service.ApiLogService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ApiLogSchedule {
    private final ApiLogService apiLogService;

    // 每小时执行
    @Scheduled(cron = "0 0 * * * ?")
    public void saveLog() {
        apiLogService.saveLog();
    }
}
