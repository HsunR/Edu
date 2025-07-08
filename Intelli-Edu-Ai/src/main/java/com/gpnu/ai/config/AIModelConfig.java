package com.gpnu.ai.config;

import com.gpnu.ai.advisor.MyLoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class AIModelConfig {

    @Value("classpath:/prompts/system-message.st")
    private Resource systemPromptTemplateResource;

    @Bean
    public ChatClient aiCourseChatClient(DeepSeekChatModel deepSeekChatModel){
       return ChatClient.builder(deepSeekChatModel)
               .defaultSystem(systemPromptTemplateResource)
               .defaultAdvisors(
                       new MyLoggerAdvisor()
               )
               .build();
    }
}
