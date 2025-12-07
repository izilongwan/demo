package com.demo.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.demo.service.FileService;

@ControllerAdvice
@RequestMapping("file")
@CrossOrigin
public class FileController {
    @Resource
    FileService fileService;

    @GetMapping("/stream/download/{path}")
    public void downloadStream(@PathVariable String path, HttpServletResponse response) {
        fileService.downloadStream(path, response);
    }
}
