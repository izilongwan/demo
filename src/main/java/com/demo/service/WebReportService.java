package com.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.domain.entity.WebReport;

public interface WebReportService extends IService<WebReport> {
    Page<WebReport> getList(Integer pageNum, Integer pageSize, WebReport webReport);
}
