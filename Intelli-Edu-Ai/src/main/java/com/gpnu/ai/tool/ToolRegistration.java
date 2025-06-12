package com.gpnu.ai.tool;


import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 注册工具类
 */
@Configuration
public class ToolRegistration {

    @Value("${search-api.api-key}")
    private String searchApiKey;

    @Bean
    public ToolCallback[] allTools() {
       //网页搜索工具
        WebSearchTool webSearchTool = new WebSearchTool(searchApiKey);
        PDFGenerationTool pdfGenerationTool = new PDFGenerationTool();
        ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool();
        WebScrapingTool webScrapingTool = new WebScrapingTool();

        return ToolCallbacks.from(
                webSearchTool,
                 pdfGenerationTool,
                resourceDownloadTool,
                webScrapingTool
        );
    }

}
