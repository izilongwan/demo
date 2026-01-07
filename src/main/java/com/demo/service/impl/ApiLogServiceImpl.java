package com.demo.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.domain.entity.ApiLog;
import com.demo.domain.property.ApiLogProperty;
import com.demo.mapper.ApiLogMapper;
import com.demo.service.ApiLogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiLogServiceImpl extends ServiceImpl<ApiLogMapper, ApiLog> implements ApiLogService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final ApiLogProperty apiLogProperty;

    @Override
    public void saveLog() {
        String createTime = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String prevKey = apiLogProperty.getRedisKeyPrefix() + createTime;
        Set<String> keys = redisTemplate.keys(prevKey + "*");

        if (keys.isEmpty()) {
            return;
        }

        int batchSize = apiLogProperty.getBatchSize();

        ListOperations<String, Object> opsForList = redisTemplate.opsForList();
        List<ApiLog> allResults = new ArrayList<>();
        keys.forEach(key -> {
            Object obj;
            while (Objects.nonNull(obj = opsForList.leftPop(key))) {
                ApiLog apiLog = JSON.parseObject((String) obj, ApiLog.class);
                allResults.add(apiLog);

                if (allResults.size() >= batchSize) {
                    boolean saveBatchResult = super.saveBatch(allResults);
                    log.debug("[saveBatch {}] Saved batch of api logs, size: {}", saveBatchResult, allResults.size());
                    allResults.clear();
                }
            }

        });

        if (!allResults.isEmpty()) {
            boolean saveBatchResult = super.saveBatch(allResults);
            log.debug("[saveBatch {}] Saved remaining api logs, size: {}", saveBatchResult, allResults.size());
        }
    }
}
