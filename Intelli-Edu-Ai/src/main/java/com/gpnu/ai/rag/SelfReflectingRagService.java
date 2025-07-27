package com.gpnu.ai.rag;

import com.gpnu.ai.chatMemory.HybridChatMemory;
import com.gpnu.ai.model.dto.ChatRequest;
import com.gpnu.ai.model.dto.Critique;
import com.gpnu.common.utils.contextHolder.UserContextHolder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
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

    public String ask(ChatRequest chatRequest){
        String currentAnswer = "";
        Critique currentCritique = null;

        String query = chatRequest.getUserPrompt();
//        Long userId = UserContextHolder.getUserId();
//        Filter.Expression expression = new FilterExpressionBuilder()
//                .or(new FilterExpressionBuilder().eq("conversationId", chatRequest.getConversationId()),
//                        new FilterExpressionBuilder().
//                                and(new FilterExpressionBuilder().eq("conversationId", chatRequest.getConversationId()),
//                                        new FilterExpressionBuilder().eq("userId",userId )))
//                .build();
        int iteration = 0;

        while (iteration < MAX_ITERATION){
            log.info("---Iteration " + (iteration+1 + " ---"));
            List<Document> relevantDocuments = redisVectorStore.doSimilaritySearch(SearchRequest.builder()
                    .query(query)
                    .topK(5)
//                    .filterExpression( expression)
                    .similarityThreshold(0.7)
                    .build());

            String context = relevantDocuments.stream()
                    .map(Document::getText)
                    .collect(Collectors.joining("\n---\n"));

            //构建初始的userPrompt
            String userPrompt;
            String systemPrompt;

            if (iteration == 0 ){
                systemPrompt = """
                        你是一个专业且准确的问答AI通识教育助手。
                        请根据提供的上下文信息回答问题。如果上下文没有提供足够信息，请说明。
                        上下文:
                        {context}
                        """;
                userPrompt = "问题: " + chatRequest.getUserPrompt();
            }else{
                //迭代修正
                log.info("当前答案"+currentAnswer);
                log.info("批判报告: " + currentCritique.summary() + " (自信度: " + currentCritique.confidence() + ")");
                log.info("建议: " + currentCritique.suggestedImprovements());
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
                userPrompt = "原始问题: " + query;
                systemPrompt = systemPrompt
                        .replace("{previousAnswer}", currentAnswer)
                        .replace("{critiqueSummary}", currentCritique.summary())
                        .replace("{confidenceLevel}", currentCritique.confidence().toString())
                        .replace("{suggestedImprovements}", currentCritique.suggestedImprovements());
                // 如果批判报告表明需要更多信息，且不是第一次迭代，我们可以基于建议调整 query
                if (currentCritique != null && currentCritique.needsMoreInfo()) {
                    log.info("需要更多信息，尝试根据建议重新检索...");
                    // 实际应用中，这里可以构建更复杂的查询重写逻辑
                    query = query + " " + currentCritique.suggestedImprovements();
                    relevantDocuments = redisVectorStore.similaritySearch(query); // 重新检索
                    context = relevantDocuments.stream()
                            .map(Document::getText)
                            .collect(Collectors.joining("\n---\n"));
                    log.info("新检索上下文:\n" + context);
                }
            }

            //生成答案

            ChatResponse generationResponse = chatClient.prompt()
                    .messages(
                            new SystemMessage(systemPrompt.replace("{context}", context)),
                            new UserMessage(userPrompt)
                    )
                    .call()
                    .chatResponse();
            currentAnswer = generationResponse.getResult().getOutput().getText();
            log.info("生成答案: " + currentAnswer);
            // 步骤 2: 自我评估/批判
            String critiquePrompt = critiqueOutputConverter.getFormat(); // 获取期望的 JSON 格式
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
                    .replace("{format}", critiquePrompt)
                    .replace("{answer}", currentAnswer)
                    .replace("{originalQuery}", query)
                    .replace("{context}", context);

            ChatResponse critiqueResponse = chatClient.prompt()
                    .messages(new SystemMessage(critiqueSystemMessage))
                    .call()
                    .chatResponse();

            String critiqueJson = critiqueResponse.getResult().getOutput().getText();
            log.info("原始批判JSON: " + critiqueJson);

            try {
                currentCritique = critiqueOutputConverter.convert(critiqueJson);
                log.info("解析批判: " + currentCritique);
            } catch (Exception e) {
                log.error("解析批判JSON失败，尝试重新生成或跳过批判：" + e.getMessage());
                // 如果解析失败，可以决定跳过批判或重试
                currentCritique = new Critique("Critique parsing failed, assuming needs more info.", Critique.ConfidenceLevel.LOW, true, "Could not parse critique, re-evaluate answer.");
            }
            // 步骤 3: 决策
            if (currentCritique.confidence() == Critique.ConfidenceLevel.HIGH && !currentCritique.needsMoreInfo()) {
                log.info("答案质量高，无需进一步迭代。");
                return currentAnswer; // 答案满意，返回
            }

            // 如果达到最大迭代次数，返回当前最佳答案
            if (iteration == MAX_ITERATION - 1) {
                log.info("达到最大迭代次数，返回当前最佳答案。");
                return currentAnswer + "\n\n(注意：此答案可能仍需改进，根据最大迭代次数限制返回)";
            }
            iteration++;


        }
        return currentAnswer;

    }

    public Flux<String> askStream(ChatRequest chatRequest) {
        String originalQuery = chatRequest.getUserPrompt();
        return Flux.defer(() -> doAskByStream(originalQuery, chatRequest, 0, null, ""));
    }

    private Flux<String> doAskByStream(String query, ChatRequest chatRequest, int iteration, Critique lastCritique, String lastAnswer) {
        if (iteration >= MAX_ITERATION) {
            return Flux.just(lastAnswer + "\n\n(注意：此答案可能仍需改进，根据最大迭代次数限制返回)");
        }

        return Flux.defer(() -> {
            log.info("---Iteration " + (iteration + 1) + " ---");
        Long userId = UserContextHolder.getUserId();
        Filter.Expression expression = new FilterExpressionBuilder()
                .or(new FilterExpressionBuilder().eq("conversationId", chatRequest.getConversationId()),
                        new FilterExpressionBuilder().
                                and(new FilterExpressionBuilder().eq("conversationId", chatRequest.getConversationId()),
                                        new FilterExpressionBuilder().eq("userId",userId )))
                .build();

            List<Document> relevantDocs = redisVectorStore.doSimilaritySearch(SearchRequest.builder()
                    .query(query)
                    .topK(5)
                    .similarityThreshold(0.7)
//                    .filterExpression(expression)
                    .build());

            String context = relevantDocs.stream().map(Document::getText).collect(Collectors.joining("\n---\n"));
            boolean hasContext = !relevantDocs.isEmpty() && !context.isBlank();

            String systemPrompt;
            String userPrompt;

            if (iteration == 0) {
                if(hasContext){
                    systemPrompt = """
                你是一个专业且准确的问答AI通识教育助手。
                请根据提供的上下文信息回答问题。如果上下文没有提供足够信息，请基于你已有的知识推理并说明。
                上下文:
                {context}
                """.replace("{context}", context.isEmpty() ? "（无相关上下文）" : context);
                }else{
                    systemPrompt = """
                    你是一个专业且准确的问答AI通识教育助手。
                    请根据提供的上下文信息回答问题。如果上下文没有提供足够信息，请基于你已有的知识推理并说明。
                    上下文:
                    {context}
                    """.replace("{context}", context);
                }
                userPrompt = "问题: " + query;
            } else {
                systemPrompt = """
                你是一个AI通识教育问答助手。
                上一次的回答：'{previousAnswer}'
                受到如下批判：
                批判摘要: {critiqueSummary}
                自信度: {confidenceLevel}
                改进建议: {suggestedImprovements}

                请基于原始问题和新上下文进行更优回答：
                上下文:
                {context}
                """
                        .replace("{previousAnswer}", lastAnswer)
                        .replace("{critiqueSummary}", lastCritique.summary())
                        .replace("{confidenceLevel}", lastCritique.confidence().toString())
                        .replace("{suggestedImprovements}", lastCritique.suggestedImprovements())
                        .replace("{context}", context.isEmpty() ? "（无相关上下文）" : context);
                userPrompt = "原始问题: " + query;
            }

            // 流式生成回答
            Flux<String> responseFlux = chatClient.prompt()
                    .messages(
                            new SystemMessage(systemPrompt),
                            new UserMessage(userPrompt)
                    )
                    .advisors(MessageChatMemoryAdvisor.builder(hybridChatMemory).conversationId(chatRequest.getConversationId()).build())
                    .stream()
                    .content();

            return responseFlux.collectList().flatMapMany(allChunks -> {
                String answer = String.join("", allChunks);
                log.info("生成答案: " + answer);

                // 构造批判请求
                String critiquePrompt = critiqueOutputConverter.getFormat();
                String critiqueSystemPrompt = """
                请根据以下规则严格评估给定的答案。
                输出必须是 JSON 格式。
                {format}

                答案: '{answer}'
                原始问题: '{originalQuery}'
                提供的上下文: '{context}'

                请评估答案的相关性、准确性、完整性和自信度，并给出改进建议。
                如果答案不完整或上下文不足，请将 'needsMoreInfo' 设置为 true。
                """
                        .replace("{format}", critiquePrompt)
                        .replace("{answer}", answer)
                        .replace("{originalQuery}", query)
                        .replace("{context}", context.isEmpty() ? "（无相关上下文）" : context);

                ChatResponse critiqueResponse = chatClient.prompt()
                        .messages(new SystemMessage(critiqueSystemPrompt))
                        .advisors(MessageChatMemoryAdvisor.builder(hybridChatMemory).conversationId(chatRequest.getConversationId()).build())
                        .call()
                        .chatResponse();

                String critiqueJson = critiqueResponse.getResult().getOutput().getText();
                Critique critique;
                try {
                    critique = critiqueOutputConverter.convert(critiqueJson);
                } catch (Exception e) {
                    log.warn("批判解析失败: " + e.getMessage());
                    critique = new Critique("解析失败，默认低置信", Critique.ConfidenceLevel.LOW, true, "尝试优化答案结构");
                }

                if (critique.confidence() == Critique.ConfidenceLevel.HIGH && !critique.needsMoreInfo()) {
                    // 终止返回最终结果
                    return Flux.fromIterable(allChunks);
                } else {
                    // 进入下一轮，不拼接当前结果
                    String newQuery = critique.needsMoreInfo() ? query + " " + critique.suggestedImprovements() : query;
                    return doAskByStream(newQuery, chatRequest, iteration + 1, critique, answer);
                }
            });
        });
    }








}
