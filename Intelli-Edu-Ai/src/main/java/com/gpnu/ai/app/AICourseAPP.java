package com.gpnu.ai.app;

import com.gpnu.ai.advisor.MyLoggerAdvisor;
import com.gpnu.ai.chatMemory.HybridChatMemory;
import com.gpnu.ai.model.dto.ChatRequest;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;



@Slf4j
@Service
public class AICourseAPP {




    @Resource(name = "hybridChatMemory")
    private HybridChatMemory hybridChatMemory;


    @Resource(name = "redisVectorStore")
    private RedisVectorStore redisVectorStore;

    @Resource
    private ToolCallback[] allTools;

    @Resource(name = "aiCourseChatClient")
    private  ChatClient chatClient;




    public String doChat(String message, String chatId){


        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(
                        //混合持久化的Advisor
//                        MessageChatMemoryAdvisor.builder(hybridChatMemory).conversationId(chatId).build()
                )
                .call()
                .chatResponse();
        Integer totalTokens = chatResponse.getMetadata().getUsage().getTotalTokens();
        chatResponse.getMetadata().getUsage().getCompletionTokens();

        String text = chatResponse.getResult().getOutput().getText();
        log.info("totalTokens = "+totalTokens);
        log.info("content: {}",text);
        return text;
    }

    public Flux<String> doChatByStream(String message, String chatId){
        Flux<String> result = chatClient
                .prompt()
                .user(message)
                .advisors(
                        new MyLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(hybridChatMemory).conversationId(chatId).build()
                )
                .stream()
                .content();
        return result;
    }



    public String doChatRag(String message, String chatId){
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(QuestionAnswerAdvisor.builder(redisVectorStore).build())
                .call()
                .chatResponse();
        String text = chatResponse.getResult().getOutput().getText();
        log.info("content: {}",text);
        return text;
    }

    public Flux<String> doChatWithRagByStream(String message ,String chatId){

        Flux<String> result = chatClient
                .prompt()
                .user(message)
                .advisors(
                        new QuestionAnswerAdvisor(redisVectorStore),
                        MessageChatMemoryAdvisor.builder(hybridChatMemory).conversationId(chatId).build()
                )
                .stream()
                .content();
        return result;
    }

    public String doChatWithTools(String message , String chatId){
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(
                        new QuestionAnswerAdvisor(redisVectorStore),
                        MessageChatMemoryAdvisor.builder(hybridChatMemory).conversationId(chatId).build()
                )
                .toolCallbacks(allTools)
                .call()
                .chatResponse();
        String result = response.getResult().getOutput().getText();
        log.info("content: {}",result);
        return result;
    }


    public Flux<String> doChatWithToolsByStream(ChatRequest chatRequest){
        Flux<String> result = chatClient
                .prompt()
                .user(chatRequest.getUserPrompt())
                .advisors(
                        new QuestionAnswerAdvisor(redisVectorStore),
                        MessageChatMemoryAdvisor.builder(hybridChatMemory).conversationId(chatRequest.getConversationId()).build()
                )
                .toolCallbacks(allTools)
                .stream()
                .content();
        return result;
    }

//    public Flux<String> performRagQuery(String message,String chatId){
//            //  1.构建基于用户 ID 的过滤表达式
//        Filter.Expression userIdFilter = new Filter.Expression(
//                Filter.ExpressionType.EQ,
//                new Filter.Key("user_id"),
//                //先用chatId作为userId
//                new Filter.Value(chatId)
//        );
//
//        // 2.执行查询
//        SearchRequest searchRequest = SearchRequest.builder()
//                .query(message)
//                .topK(5)
//                .filterExpression(userIdFilter) // 添加过滤条件
//                .build();
//        // 3. 调用新的 similaritySearch
//        List<Document> result = redisVectorStore.similaritySearch(searchRequest);
//
//        if(result.isEmpty()){
//            log.info("用户" + chatId + "没有相关文档");
//        }
//        String context = result.stream()
//                .map(doc -> doc.getText() + (doc.getMetadata().containsKey("original_filename") ? " (來源: " + doc.getMetadata().get("original_filename") + ")" : ""))
//                .collect(Collectors.joining("\n\n"));
//
//        if (context.trim().isEmpty()) {
//            System.out.println("未找到相關文檔，直接向 LLM 提問。");
//            return chatClient.prompt()
//                    .user(message)
//                    .advisors(
//                            new MyLoggerAdvisor(),
//                            MessageChatMemoryAdvisor.builder(hybridChatMemory).build()
//                    )
//                    .stream()
//                    .content();
//        }
//
//        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(String.valueOf(systemPromptTemplateResource));
//        Prompt prompt = systemPromptTemplate.create(Map.of("context", context, "question", message));
//
//        return chatClient.prompt()
//                .messages(prompt.getSystemMessage())
//                .advisors(
//                        new MyLoggerAdvisor(),
//                        MessageChatMemoryAdvisor.builder(hybridChatMemory).build()
//                )
//                .stream()
//                .content();
//
//
//
//    }


}
