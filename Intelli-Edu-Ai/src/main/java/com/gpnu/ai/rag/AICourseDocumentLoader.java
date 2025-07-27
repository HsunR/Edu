package com.gpnu.ai.rag;


import com.gpnu.ai.wrapper.CustomTikaDocumentReader;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.exception.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class AICourseDocumentLoader {

    // 资源加载器
    private final   ResourcePatternResolver resourcePatternResolver;

    public AICourseDocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }


    /**
     * 加载本地的markdown文档（用来读取公共文档），不再使用
     * @return
     */
    public List<Document> loadMarkdowns(){
        List<Document> allDocuments = new ArrayList<>();
        //加载多篇markdown文件
        try{
            Resource[] resources = resourcePatternResolver.getResources("classpath:document/*.md");
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                //提前文档倒数第三个和第二个字作为标签
                String status = "AI通识历史";
                String tag = "AI通识历史;深度学习;人工智能";
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeCodeBlock(false)
                        .withIncludeBlockquote(false)
                        .withAdditionalMetadata("filename", filename)
                        .withAdditionalMetadata("status", status)
                        .withAdditionalMetadata("tag", tag)
                        .withAdditionalMetadata("accessPermission","public")
                        .build();
                MarkdownDocumentReader markdownDocumentReader = new MarkdownDocumentReader(resource, config);
                allDocuments.addAll(markdownDocumentReader.get());
            }
        }catch (IOException e){
            log.error("Markdown 文档加载失败",e);
        }
        return allDocuments;
    }

    public List<Document> parsePublicDocumentByTika(MultipartFile multipartFile, Map<String, Object> customMetadata) {
         List<Document> documents = new ArrayList<>();
         Resource resource = multipartFile.getResource();
         ThrowUtils.throwIf(!resource.exists(), ErrorCode.OPERATION_ERROR,"上传文件不存在");
         CustomTikaDocumentReader tikaDocumentReader = new CustomTikaDocumentReader(resource, customMetadata);
         documents.addAll(tikaDocumentReader.get());
        return documents;

    }


}
