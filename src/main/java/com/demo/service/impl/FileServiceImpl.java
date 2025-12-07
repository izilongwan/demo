package com.demo.service.impl;

import java.io.File;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;

import com.demo.service.FileService;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public void downloadStream(String path, HttpServletResponse response) {
        File file = new File("src/main/resources/" + path);
        long length = file.length();

        // 根据文件扩展名设置 Content-Type
        String fileName = file.getName();
        String contentType = getContentType(fileName);

        response.setContentType(contentType);
        response.setContentLength((int) length);
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        try (InputStream in = getClass().getResourceAsStream("/" + path);
                ServletOutputStream out = response.getOutputStream()) {
            IOUtils.copy(in, out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 根据文件类型返回对应的 Content-Type
    private String getContentType(String fileName) {
        if (fileName.endsWith(".mp4")) {
            return "video/mp4";
        } else if (fileName.endsWith(".avi")) {
            return "video/avi";
        } else if (fileName.endsWith(".mp3")) {
            return "audio/mpeg";
        } else if (fileName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".zip")) {
            return "application/zip";
        } else {
            return "application/octet-stream"; // 默认二进制
        }
    }
}
