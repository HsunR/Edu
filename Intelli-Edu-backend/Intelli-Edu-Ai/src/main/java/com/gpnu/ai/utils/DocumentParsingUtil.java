package com.gpnu.ai.utils;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class DocumentParsingUtil {

    /**
     * 将 MultipartFile 转换成 Spring Resource，并解析为 Document 列表
     *
     * @param file 上传的 MultipartFile
     * @return List<Document>，若解析失败返回空列表
     */
    public static List<Document> parse(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            InputStream in = file.getInputStream();
            Resource resource = new InputStreamResource(in) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            // 使用 TikaDocumentReader 解析多格式文档内容
            return new TikaDocumentReader(resource).read();
        } catch (Exception e) {
            // 可追加日志记录 e.printStackTrace() 或使用 Logger
            return Collections.emptyList();
        }
    }
}
