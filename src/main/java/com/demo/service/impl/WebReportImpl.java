package com.demo.service.impl;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.domain.entity.WebReport;
import com.demo.mapper.WebReportMapper;
import com.demo.service.WebReportService;

@Service
public class WebReportImpl extends
                ServiceImpl<WebReportMapper, WebReport> implements WebReportService {

        public Page<WebReport> getList(Integer pageNum, Integer pageSize, WebReport webReport) {
                Page<WebReport> page = new Page<WebReport>(pageNum, pageSize);
                return this.lambdaQuery()
                                .select(
                                                WebReport::getId, WebReport::getReportName, WebReport::getReportType,
                                                WebReport::getReportFilename, WebReport::getReportMessage,
                                                WebReport::getReportError,
                                                WebReport::getCreateTime,
                                                WebReport::getUpdateTime)
                                .like(Objects.nonNull(webReport.getReportName()),
                                                WebReport::getReportName, webReport.getReportName())
                                .like(Objects.nonNull(webReport.getCreateTime()),
                                                WebReport::getCreateTime, webReport.getCreateTime())
                                .like(Objects.nonNull(webReport.getId()),
                                                WebReport::getId, webReport.getId())
                                .page(page);
        }
}
