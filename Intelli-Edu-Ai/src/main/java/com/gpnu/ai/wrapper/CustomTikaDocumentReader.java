package com.gpnu.ai.wrapper;

import lombok.Data;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;



public class CustomTikaDocumentReader extends TikaDocumentReader {

    private final Map<String,Object> customMetadata;

    public CustomTikaDocumentReader(Resource resource, Map<String, Object> customMetadata) {
        super(resource);
        this.customMetadata = customMetadata;
    }


    @Override
    public List<Document> get() {
        List<Document> documents = super.get();
        for (Document document : documents) {
            // 将自定义元数据添加到每个文档中
            if (Objects.nonNull(document.getMetadata()) ) {
                customMetadata.forEach(document.getMetadata()::put);
            }
        }
        return documents;
    }

}
