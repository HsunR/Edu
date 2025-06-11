//package com.gpnu.ai.rag;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.ai.document.Document;
//import org.springframework.ai.vectorstore.SearchRequest;
//import org.springframework.ai.vectorstore.VectorStore;
//import org.springframework.ai.vectorstore.filter.Filter;
//import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//
//@Component
//@Slf4j
//public class VectorStoreIngestor implements CommandLineRunner {
//
//    private static final int MAX_BATCH_SIZE = 25;
//
//    private final AICourseDocumentLoader documentLoader;
//    private final VectorStore vectorStore;
//    private final MyTokenTextSplitter myTokenTextSplitter;
//
//    public VectorStoreIngestor(AICourseDocumentLoader documentLoader,
//                               @Qualifier("redisVectorStore") VectorStore redisVectorStore,
//                               MyTokenTextSplitter myTokenTextSplitter) {
//        this.documentLoader = documentLoader;
//        this.vectorStore = redisVectorStore;
//        this.myTokenTextSplitter = myTokenTextSplitter;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        log.info("开始检查并向向量数据库中加载新文档...");
//
//        // 加载并切分文档
//        List<Document> documents = documentLoader.loadMarkdowns();
//        List<Document> documentsFromFiles = myTokenTextSplitter.splitCustomized(documents);
//
//        if (documentsFromFiles.isEmpty()) {
//            log.info("在指定路径下未找到任何文档文件。");
//            return;
//        }
//
//        // 过滤已存在的文档
//        List<Document> newDocuments = documentsFromFiles.stream()
//                .filter(doc -> !this.isDocumentExists(doc))
//                .collect(Collectors.toList());
//
//        if (!newDocuments.isEmpty()) {
//            log.info("发现 {} 篇新文档，准备进行嵌入和存储...", newDocuments.size());
//            newDocuments.forEach(doc -> log.info("  - 新文档: {}", doc.getMetadata().get("filename")));
//            addDocumentsInBatches(newDocuments);
//            log.info("新文档已成功添加至向量数据库。");
//        } else {
//            log.info("所有文档均已存在于向量数据库中，无需执行添加操作。");
//        }
//    }
//
//    /**
//     * 将文档分批添加到向量数据库，避免超过 DashScope 限制
//     */
//    private void addDocumentsInBatches(List<Document> documents) {
//        for (int i = 0; i < documents.size(); i += MAX_BATCH_SIZE) {
//            int end = Math.min(i + MAX_BATCH_SIZE, documents.size());
//            List<Document> batch = new ArrayList<>(documents.subList(i, end));
//            try {
//                log.info("向量数据库添加文档批次：第 {} ~ {} 条", i + 1, end);
//                vectorStore.add(batch);
//            } catch (Exception e) {
//                log.error("添加文档批次失败（{}~{}），跳过本批次。", i + 1, end, e);
//            }
//        }
//    }
//
//    /**
//     * 检查单个文档是否已经存在于 VectorStore 中
//     */
//
//
//    private boolean isDocumentExists(Document document) {
//        String fileName = (String) document.getMetadata().get("filename");
//        if (fileName == null || fileName.isBlank()) {
//            log.warn("文档缺少 'filename' 元数据，将视为新文档。");
//            return false;
//        }
//
//        log.info("正在为文件 '{}' 构建程序化过滤器...", fileName);
//
//        try {
//            // ！！！ 核心修正：使用正确的顶级字段名 "filename" ！！！
//            Filter.Expression expression = new FilterExpressionBuilder()
//                    .eq("filename", fileName)
//                    .build();
//
//            SearchRequest request = SearchRequest.builder()
//                    .query("*")
//                    .topK(1)
//                    .filterExpression(expression)
//                    .build();
//
//            List<Document> result = vectorStore.similaritySearch(request);
//            log.info("数据库查询结果：文件 '{}' 找到 {} 条记录。", fileName, result.size());
//            return !result.isEmpty();
//
//        } catch (Exception e) {
//            log.error("查询文档 '{}' 是否存在时发生错误。", fileName, e);
//            return false;
//        }
//    }
//}
//
//
//
