package com.demo.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.domain.entity.WebReport;
import com.demo.service.WebReportService;

@RequestMapping("webreport")
@RestController
public class WebReportController {
    @Resource
    WebReportService webReportService;

    @PostMapping("save")
    public boolean save(@RequestBody WebReport webReport) {
        return webReportService.save(webReport);
    }

    @PostMapping("list/{pageNum}/{pageSize}")
    public Page<WebReport> getList(@PathVariable Integer pageNum, @PathVariable Integer pageSize,
            @RequestBody WebReport webReport) {
        return webReportService.getList(pageNum, pageSize, webReport);
    }

    @GetMapping("detail/{id}")
    public WebReport detail(@PathVariable String id) {
        return webReportService.getById(id);
    }

}
