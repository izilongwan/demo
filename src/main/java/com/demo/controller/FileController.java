package com.demo.controller;

import java.io.IOException;

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

    @GetMapping("/stream/{path}")
    public void donwloadStream(@PathVariable String path, HttpServletResponse response) throws IOException {
        fileService.donwloadStream(path, response);
    }
}
