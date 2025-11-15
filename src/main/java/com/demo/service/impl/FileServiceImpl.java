package com.demo.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;

import com.demo.service.FileService;

@Service
public class FileServiceImpl implements FileService {
    public void donwloadStream(String path, HttpServletResponse response) throws IOException {
        long length = new File("src/main/resources/" + path).length();
        response.setContentType("video/*");
        response.setContentLength((int) length);
        response.setHeader("Content-Disposition", "attachment; filename=aqgy.mp4");
        try (InputStream in = getClass().getResourceAsStream("/" + path);
                ServletOutputStream out = response.getOutputStream()) {
            // Apache Commons IO 工具类
            IOUtils.copy(in, out);
            out.flush();
        }
    }
}
