package com.demo.service;

import javax.servlet.http.HttpServletResponse;

public interface FileService {
    void downloadStream(String path, HttpServletResponse response);
}
