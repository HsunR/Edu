package com.gpnu.ai.service.impl;

import com.gpnu.ai.rag.MyTokenTextSplitter;
import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter; // 确保导入 Filter 类
import org.springframework.ai.vectorstore.filter.Filter.ExpressionType; // **新增：导入 ExpressionType**
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DocumentIngestionService {


    @Resource(name = "redisVectorStore")
    private  VectorStore vectorStore;

    private  final MyTokenTextSplitter myTextSplitter = new MyTokenTextSplitter();


    /**
     * 處理上傳的 MultipartFile，解析其內容，並將其儲存到向量資料庫。
     * 支援多種文件格式，透過 TikaDocumentReader 自動識別和解析。
     *
     * @param file 上傳的 MultipartFile
     * @param userId 上傳文件的用戶 ID，用於文件歸屬和查詢隔離
     * @return 包含文件 ID 和元資料的回應映射
     * @throws IOException 如果讀取文件失敗
     */
    public Map<String, Object> processAndStoreDocument(MultipartFile file, String userId) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上傳文件不能為空。");
        }

        String fileId = UUID.randomUUID().toString();
        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();
        long fileSize = file.getSize();

        Map<String, Object> responseMetadata = new HashMap<>();
        responseMetadata.put("fileId", fileId);
        responseMetadata.put("originalFilename", originalFilename);
        responseMetadata.put("contentType", contentType);
        responseMetadata.put("fileSize", fileSize);
        responseMetadata.put("uploadBy", userId); // 紀錄上傳者

        try {
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return originalFilename; // 提供文件名給 Tika 內部使用
                }
            };

            TikaDocumentReader tikaReader = new TikaDocumentReader(resource);
            List<Document> documents = tikaReader.get();

            if (documents.isEmpty()) {
                throw new IOException("無法從文件中提取任何內容。這可能是因為文件損壞或不受支持的格式。");
            }

            for (Document doc : documents) {
                doc.getMetadata().put("file_id", fileId);
                doc.getMetadata().put("original_filename", originalFilename);
                doc.getMetadata().put("content_type", contentType);
                doc.getMetadata().put("user_id", userId); // **關鍵：用於用戶隔離**
            }

            // 使用您的自定義文本分割器
            List<Document> chunks = myTextSplitter.splitCustomized(documents);

            vectorStore.add(chunks);
            responseMetadata.put("chunkCount", chunks.size());
            System.out.println("文件 '" + originalFilename + "' (ID: " + fileId + ", 用戶: " + userId + ") 已解析並存儲到向量資料庫。總計 " + chunks.size() + " 個 chunks。");

        } catch (Exception e) {
            System.err.println("處理文件失敗: " + e.getMessage());
            throw new IOException("文件處理失敗", e);
        }
        return responseMetadata;
    }

    /**
     * 刪除指定文件 ID 的所有相關文檔。
     * 實際調用 RedisVectorStore 的按過濾條件刪除方法。
     *
     * @param fileId 要刪除的文件 ID
     * @return 是否成功刪除 (目前 Spring AI 的 delete by filter 不直接返回布爾值，而是拋出異常)
     */
    public boolean deleteDocumentsByFileId(String fileId) {
        if (fileId == null || fileId.trim().isEmpty()) {
            throw new IllegalArgumentException("文件 ID 不能為空。");
        }
        try {
            Filter.Expression filterExpression = new Filter.Expression(
                    ExpressionType.EQ, // 使用 ExpressionType.EQ
                    new Filter.Key("file_id"),
                    new Filter.Value(fileId)
            );

            vectorStore.delete(filterExpression);

            System.out.println("已嘗試從向量資料庫中刪除文件 ID: " + fileId + " 的所有相關文檔。");
            return true; // 如果沒有拋出異常則認為成功
        } catch (Exception e) {
            System.err.println("刪除文件 ID: " + fileId + " 失敗: " + e.getMessage());
            throw new RuntimeException("刪除文件失敗。", e);
        }
    }
}