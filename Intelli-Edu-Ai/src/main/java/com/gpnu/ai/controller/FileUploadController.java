package com.gpnu.ai.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
public class FileUploadController {

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "请选择一个文件进行上传。";
        }
        try {
            // 在这里处理文件，可以将其保存到服务器或直接传递给Spring AI进行解析
            // 例如：保存到临时文件或获取InputStream
            byte[] bytes = file.getBytes();
            // ... 进行文件解析
            return "文件上传成功！文件名：" + file.getOriginalFilename();
        } catch (IOException e) {
            e.printStackTrace();
            return "文件上传失败：" + e.getMessage();
        }
    }
}