package com.gpnu.ai.app;

import com.gpnu.ai.chatMemory.HybridChatMemory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;


@Slf4j
@Service
public class AICourseAPP {




    @Resource(name = "hybridChatMemory")
    private HybridChatMemory hybridChatMemory;

  //这里是注入自己的自定义向量库
//    @Resource(name = "aiCourseVectorStore")
//    private VectorStore aiCourseVectorStore;

    @Resource(name = "redisVectorStore")
    private RedisVectorStore redisVectorStore;

    @Resource
    private ToolCallback[] allTools;

    private  final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "你是AI通识课的助教，" +
            "请根据学生的提问，给出简洁明了的回答，" +
            "如果学生提问不清晰，请引导学生进行更清晰的提问。";




    public AICourseAPP(OpenAiChatModel openAiChatModel) {
        this.chatClient =  ChatClient.
                builder(openAiChatModel).
                //  设置系统提示语
                defaultSystem(SYSTEM_PROMPT).
                // 设置默认Advisor
                defaultAdvisors(
                //    new SimpleLoggerAdvisor()
                ).
                build();
    }


    public String doChat(String message, String chatId){

        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(
                        //混合持久化的Advisor
                        MessageChatMemoryAdvisor.builder(hybridChatMemory).build()
                )
                .call()
                .chatResponse();
        String text = chatResponse.getResult().getOutput().getText();
        log.info("content: {}",text);
        return text;
    }

    public Flux<String> doChatByStream(String message, String chatId){
        Flux<String> result = chatClient
                .prompt()
                .user(message)
                .stream()
                .content();
        return result;
    }



    public String doChatRag(String message, String chatId){
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(
                        new QuestionAnswerAdvisor(redisVectorStore)
                )
                .call()
                .chatResponse();
        String text = chatResponse.getResult().getOutput().getText();
        log.info("content: {}",text);
        return text;
    }

    public String doChatWithTools(String message , String chatId){
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(
                        new SimpleLoggerAdvisor(),
                        new QuestionAnswerAdvisor(redisVectorStore)
                )
                .tools(allTools)
                .call()
                .chatResponse();
        String result = response.getResult().getOutput().getText();
        log.info("content: {}",result);
        return result;
    }



}
