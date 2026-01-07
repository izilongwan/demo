package com.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.service.ApiLogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api-log")
@RequiredArgsConstructor
public class ApiLogController {
    final private ApiLogService apiLogService;

    @GetMapping("save")
    public void saveLog() {
        apiLogService.saveLog();
    }
}
