package com.gpnu.ai.rag;

import com.gpnu.ai.advisor.FilterChatMemoryAdvisor;
import com.gpnu.ai.chatMemory.HybridChatMemory;
import com.gpnu.ai.model.dto.ChatRequest;
import com.gpnu.ai.model.dto.Critique;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 自省式RAG实现案例
 */
@Slf4j
@Service
public class SelfReflectingRagService {

    @Resource(name = "aiCourseChatClient")
    private ChatClient chatClient;

    @Resource(name = "hybridChatMemory")
    private HybridChatMemory hybridChatMemory;


    @Resource(name = "redisVectorStore")
    private RedisVectorStore redisVectorStore;

    private final BeanOutputConverter<Critique> critiqueOutputConverter;

    public SelfReflectingRagService() {
        this.critiqueOutputConverter = new BeanOutputConverter<>(Critique.class);
    }

    //最大迭代次数
    private static final int MAX_ITERATION = 3;


    public Flux<String> ask(ChatRequest chatRequest) {

        final String originalQuery = chatRequest.getUserPrompt();
        //第一轮手动插入数据
        hybridChatMemory.add(chatRequest.getConversationId(),List.of(UserMessage.builder().text(chatRequest.getUserPrompt()).build()));


        return Flux.defer(() -> {
            String currentQuery = originalQuery; // 用于检索的当前查询，可能在迭代中被修改
            String currentAnswer = ""; // 存储上一次迭代生成的完整答案
            Critique currentCritique = null; // 存储上一次迭代的批判结果
            String finalAnswer = ""; // 存储最终确定要返回的答案

            //        Long userId = UserContextHolder.getUserId();
            //        Filter.Expression expression = new FilterExpressionBuilder()
            //                .or(new FilterExpressionBuilder().eq("conversationId", chatRequest.getConversationId()),
            //                        new FilterExpressionBuilder().
            //                                and(new FilterExpressionBuilder().eq("conversationId", chatRequest.getConversationId()),
            //                                        new FilterExpressionBuilder().eq("userId",userId )))
            //                .build();

            // --- 检索阶段 ---
            List<Document> relevantDocuments = redisVectorStore.similaritySearch(SearchRequest.builder()
                    .query(currentQuery)
                    .topK(5) // 获取最相关的5个文档
                    .similarityThreshold(0.7) // 相似度阈值
                   // .filterExpression(expression)
                    .build());

            // 迭代循环，执行 RAG 的生成、批判、决策过程
            for (int i = 0; i < MAX_ITERATION; i++) {
                log.info("--- Iteration " + (i + 1) + " ---");

                // 将检索到的文档内容拼接成上下文
                String context = relevantDocuments.stream()
                        .map(Document::getText)
                        .collect(Collectors.joining("\n---\n"));

                // 判断是否检索到有效上下文
                boolean hasContext = !relevantDocuments.isEmpty() && !context.isBlank();

                // --- Prompt 构建阶段 ---
                String userPrompt;
                String systemPrompt;

                if (i == 0) { // 第一次迭代：初始生成
                    if (hasContext) {
                        // 有上下文：基于上下文回答
                        systemPrompt = """
                                你是一个专业且准确的问答AI通识教育助手。
                                请根据提供的上下文信息回答问题。如果上下文没有提供足够信息，请基于你已有的知识推理并说明。
                                上下文:
                                {context}
                                """;
                        userPrompt = "问题: " + originalQuery;
                    } else {
                        // 无上下文：回退到 AI 模型的原有知识回答
                        log.warn("知识库未检索到相关内容，将从AI原有知识库回答。");
                        systemPrompt = """
                                你是一个专业且准确的问答AI通识教育助手。
                                没有找到相关的知识库信息。请根据你已有的知识回答问题。
                                """;
                        userPrompt = "问题: " + originalQuery;
                    }
                } else {
                    // 后续迭代：基于批判和新上下文进行修正
                    log.info("当前答案: " + currentAnswer);
                    log.info("批判报告: " + currentCritique.summary() + " (自信度: " + currentCritique.confidence() + ")");
                    log.info("建议: " + currentCritique.suggestedImprovements());

                    if (hasContext) {
                        // 有上下文：基于批判和新上下文修正
                        systemPrompt = """
                                你是一个AI通识教育助手问答助手。
                                你之前的回答：'{previousAnswer}' 得到了如下批判：
                                批判摘要: {critiqueSummary}
                                自信度: {confidenceLevel}
                                建议改进: {suggestedImprovements}
                                
                                请根据以下新的上下文和原始问题，修正或改进你的回答。如果上下文没有提供足够信息，请说明。
                                上下文:
                                {context}
                 
                                如果需要，你可以根据原始问题重新检索信息。
                                """;
                    } else {
                        // 无上下文：基于批判和 AI 原有知识修正
                        log.warn("知识库未检索到相关内容，将从AI原有知识库回答。");
                        systemPrompt = """
                                你是一个AI通识教育助手问答助手。
                                你之前的回答：'{previousAnswer}' 得到了如下批判：
                                批判摘要: {critiqueSummary}
                                自信度: {confidenceLevel}
                                建议改进: {suggestedImprovements}
                                
                                没有找到相关的知识库信息。请根据你已有的知识，并结合之前的批判和建议，修正或改进你的回答。
                                """;
                    }

                    userPrompt = "原始问题: " + originalQuery;
                    systemPrompt = systemPrompt
                            .replace("{previousAnswer}", currentAnswer)
                            .replace("{critiqueSummary}", currentCritique.summary())
                            .replace("{confidenceLevel}", currentCritique.confidence().toString())
                            .replace("{suggestedImprovements}", currentCritique.suggestedImprovements());

                    // 如果批判报告表明需要更多信息，并且当前有上下文，尝试调整查询以便下一轮检索
                    if (currentCritique != null && currentCritique.needsMoreInfo() && hasContext) {
                        log.info("需要更多信息，尝试根据建议调整查询...");
                        // todo 以后这里可以构建更复制的查询逻辑
                        currentQuery = originalQuery + " " + currentCritique.suggestedImprovements();
                        log.info("调整后的查询：" + currentQuery);
                    }
                }

                // 替换系统 Prompt 中的 {context} 占位符（如果存在）
                if (systemPrompt.contains("{context}")) {
                    systemPrompt = systemPrompt.replace("{context}", context);
                }

                // --- 答案生成阶段  ---
                ChatResponse response = chatClient.prompt()
                        .messages(
                                new SystemMessage(systemPrompt),
                                new UserMessage(userPrompt)
                        )
                        .advisors( MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().maxMessages(15).build()).conversationId(chatRequest.getConversationId()).build())
                        .call()
                        .chatResponse();


                currentAnswer = response.getResult().getOutput().getText();
                System.out.println("生成答案 (完整): " + currentAnswer);

                // --- 自我评估/批判阶段 ---
                String critiquePromptFormat = critiqueOutputConverter.getFormat(); // 获取期望的 JSON 格式
                String critiqueSystemMessage = """
                        请根据以下规则严格评估给定的答案。
                        输出必须是 JSON 格式。
                        {format}
                        
                        答案: '{answer}'
                        原始问题: '{originalQuery}'
                        提供的上下文: '{context}'
                        
                        请评估答案的相关性、准确性、完整性和自信度，并给出改进建议。
                        如果答案不完整或上下文不足，请将 'needsMoreInfo' 设置为 true。
                        """;

                critiqueSystemMessage = critiqueSystemMessage
                        .replace("{format}", critiquePromptFormat)
                        .replace("{answer}", currentAnswer)
                        .replace("{originalQuery}", originalQuery) // 批判时使用原始问题作为上下文
                        .replace("{context}", context);

                ChatResponse critiqueResponse = chatClient.prompt()
                        .messages(new SystemMessage(critiqueSystemMessage))
                        .advisors( MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().maxMessages(15).build()).conversationId(chatRequest.getConversationId()).build())
                        .call()
                        .chatResponse();

                String critiqueJson = critiqueResponse.getResult().getOutput().getText();
                log.info("原始批判JSON: " + critiqueJson);

                try {
                    currentCritique = critiqueOutputConverter.convert(critiqueJson);
                    log.info("解析批判: " + currentCritique);
                } catch (Exception e) {
                    log.error("解析批判JSON失败，尝试重新生成或跳过批判：", e);
                    // 如果解析失败，默认认为需要更多信息，以便继续迭代或重新评估
                    currentCritique = new Critique("Critique parsing failed, assuming needs more info.", Critique.ConfidenceLevel.LOW, true, "Could not parse critique, re-evaluate answer.");
                }

                // --- 决策阶段 ---
                if (currentCritique.confidence() == Critique.ConfidenceLevel.HIGH && !currentCritique.needsMoreInfo()) {
                    log.info("答案质量高，无需进一步迭代。");
                    finalAnswer = currentAnswer; // 确定最终答案
                    break; // 退出迭代循环
                }

                // 如果达到最大迭代次数，返回当前最佳答案
                if (i == MAX_ITERATION - 1) {
                    log.info("达到最大迭代次数，返回当前最佳答案。");
                    finalAnswer = currentAnswer + "\n\n(注意：此答案可能仍需改进，根据最大迭代次数限制返回)";
                }
            }
            // 将最终答案包装成 Flux<String> 返回
            hybridChatMemory.add(chatRequest.getConversationId(),List.of(new AssistantMessage(finalAnswer)));
            return Flux.just(finalAnswer);
        });
    }

}
