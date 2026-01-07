package com.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.domain.entity.ApiLog;

public interface ApiLogService extends IService<ApiLog> {

    void saveLog();

}
