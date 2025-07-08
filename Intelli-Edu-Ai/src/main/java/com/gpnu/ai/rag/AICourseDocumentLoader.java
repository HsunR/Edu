package com.gpnu.ai.rag;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.core.io.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class AICourseDocumentLoader {

    // 资源加载器
    private final   ResourcePatternResolver resourcePatternResolver;

    public AICourseDocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }


    public List<Document> loadPdfByTika(){
        List<Document> documents = new ArrayList<>();
        //加载pdf文件
        try{
           Resource [] resources = resourcePatternResolver.getResources("classpath:document/*.pdf");
            for(Resource resource:resources){
                String filename = resource.getFilename();
                TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
                log.info(tikaDocumentReader .get().toString());
                documents.addAll(tikaDocumentReader.get());
            }
        }catch (IOException e){
            log.error("加载pdf文件失败",e);
        }
        return documents;
    }

    public List<Document> loadPdfByPdfReader(){
        List<Document> documents = new ArrayList<>();
        //加载pdf文件
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath:document/*.pdf");
            for (Resource resource : resources){
                String filename = resource.getFilename();
                PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resource);
                documents.addAll(pdfReader.get());
            }
        }catch (IOException e){
            log.error("加载pdf文件失败",e);
        }
        return documents;
    }
    /**
     * 加载本地的markdown文档
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
                String tag = "AI通识历史,深度学习，人工智能";
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeCodeBlock(false)
                        .withIncludeBlockquote(false)
                        .withAdditionalMetadata("filename", filename)
                        .withAdditionalMetadata("status", status)
                        .withAdditionalMetadata("tag", tag)
                        .build();
                MarkdownDocumentReader markdownDocumentReader = new MarkdownDocumentReader(resource, config);
                allDocuments.addAll(markdownDocumentReader.get());
            }
        }catch (IOException e){
            log.error("Markdown 文档加载失败",e);
        }
        return allDocuments;
    }

}
