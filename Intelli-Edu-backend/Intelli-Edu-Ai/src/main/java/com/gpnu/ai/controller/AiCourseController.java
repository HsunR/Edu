package com.gpnu.ai.controller;


import com.gpnu.ai.app.AICourseAPP;
import com.gpnu.ai.model.dto.ChatRequest;
import com.gpnu.ai.rag.AICourseDocumentLoader;
import com.gpnu.ai.rag.MyTokenTextSplitter;
import com.gpnu.ai.service.ChatListService;
import com.gpnu.ai.rag.SelfReflectingRagService;
import com.gpnu.auth.resource.context.UserContextHolder;
import com.gpnu.common.common.BaseResponse;
import com.gpnu.common.common.ResultUtils;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.exception.ThrowUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;

import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/aiCourse")
@Slf4j
public class AiCourseController {

    @Resource
    private AICourseAPP aiCourseAPP;

    @Resource
    private ChatListService chatListService;

    @Resource(name = "redisVectorStore")
    private RedisVectorStore redisVectorStore;

    @Resource
    private AICourseDocumentLoader aICourseDocumentLoader;

    @Resource
    private MyTokenTextSplitter myTokenTextSplitter;

    @Resource
    private SelfReflectingRagService selfReflectingRagService;

    @Operation(summary = "基础的聊天接口仅提供记忆功能")
    @PostMapping(value = "/doChatByStream",produces =  MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatByStream(@RequestBody ChatRequest request){
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR,"会话体不能为空");
        ThrowUtils.throwIf(request.getConversationId() == null || request.getConversationId().isEmpty(), ErrorCode.PARAMS_ERROR,"会话ID不能为空");
        ThrowUtils.throwIf(request.getUserPrompt() == null || request.getUserPrompt().isEmpty(), ErrorCode.PARAMS_ERROR,"用户输入不能为空");
        return aiCourseAPP.doChatByStream(request.getUserPrompt(), request.getConversationId());
    }


    @Operation(summary = "开启了RAG的聊天接口")
    @PostMapping(value = "/doChatWithRagByStream",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithRagByStream(@RequestBody ChatRequest request){
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR,"会话体不能为空");
        ThrowUtils.throwIf(request.getConversationId() == null || request.getConversationId().isEmpty(), ErrorCode.PARAMS_ERROR,"会话ID不能为空");
        ThrowUtils.throwIf(request.getUserPrompt() == null || request.getUserPrompt().isEmpty(), ErrorCode.PARAMS_ERROR,"用户输入不能为空");
        return selfReflectingRagService.ask(request);
    }


    @Operation(summary = "开启了工具调用和RAG的聊天接口(多模态响应)")
    @PostMapping(value="/doChatWithToolAndRagByStream" ,produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithToolAndRag(@RequestBody ChatRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR,"会话体不能为空");
        ThrowUtils.throwIf(request.getConversationId() == null || request.getConversationId().isEmpty(), ErrorCode.PARAMS_ERROR,"会话ID不能为空");
        ThrowUtils.throwIf(request.getUserPrompt() == null || request.getUserPrompt().isEmpty(), ErrorCode.PARAMS_ERROR,"用户输入不能为空");
        return aiCourseAPP.doChatWithToolsByStream(request);


    }

    @PostMapping("/uploadChat")
    public Flux<BaseResponse<String>> uploadChat(@RequestBody ChatRequest request,
                                                 @RequestPart(value = "multipartFile",required = false) MultipartFile multipartFile
                                                 ) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR,"会话体不能为空");
        ThrowUtils.throwIf(request.getConversationId() == null || request.getConversationId().isEmpty(), ErrorCode.PARAMS_ERROR,"会话ID不能为空");
        ThrowUtils.throwIf(request.getUserPrompt() == null || request.getUserPrompt().isEmpty(), ErrorCode.PARAMS_ERROR,"用户输入不能为空");
        if (multipartFile != null && !multipartFile.isEmpty()) {
            Map<String, Object> customMetadata = addMetaData(multipartFile, request);
            List<Document> originalDocument = aICourseDocumentLoader.parsePublicDocumentByTika(multipartFile, customMetadata);
            List<Document> splitDocuments = myTokenTextSplitter.splitDocuments(originalDocument);
            redisVectorStore.add(splitDocuments);
        }
        return selfReflectingRagService.ask(request)
                .map(ResultUtils::success)
                .onErrorResume(e -> Flux.just(new BaseResponse<>(ErrorCode.OPERATION_ERROR.getCode(), null, "聊天失败: " + e.getMessage())));
    }

//    @PostMapping("/testUpload")
//    public BaseResponse<Void> testUpload(@RequestPart("file") MultipartFile file) {
//        try {
//           Map<String, Object> customMetadata = new HashMap<>();
//            customMetadata.put("filename", file.getOriginalFilename());
//            customMetadata.put("accessPermission", "public");
//            customMetadata.put("tag","AI通识历史;深度学习;人工智能" );
//            List<Document> documents = aICourseDocumentLoader.loadMarkdowns();
//            List<Document> mySplitDocument = myTokenTextSplitter.splitDocuments(documents);
//
//            redisVectorStore.add(mySplitDocument);
//            log.info("解析后的文档数量: {}", mySplitDocument.size());
//            log.info("解析后的文档内容: {}", mySplitDocument);
//            return ResultUtils.success(null);
//        } catch (Exception e) {
//            return ResultUtils.error(ErrorCode.OPERATION_ERROR, "文件上传失败: " + e.getMessage());
//        }
//    }
//    @GetMapping("/testSearch")
//    public BaseResponse<List<Document>> testSearch(@RequestParam(name = "question")String question) {
//        Filter.Expression expression = new FilterExpressionBuilder()
//                .eq("accessPermission", question)
//
//                .build();
//        log.info("搜索条件: {}", expression.toString());
//
//        List<Document> results =  redisVectorStore.doSimilaritySearch(SearchRequest.builder()
//                .query("介绍下香农")
//                .topK(5)
//               .filterExpression( expression)
//                .similarityThreshold(0.7)
//
//                .build());
//        log.info("搜索结果: {}", results);
//        return ResultUtils.success(results);
//    }


    /**
     * 以下为内部类
     */
    private Map<String, Object> addMetaData(MultipartFile file,ChatRequest request){
        Map<String, Object> customMetadata = new HashMap<>();
        customMetadata.put("filename", file.getOriginalFilename());
        customMetadata.put("conversationId",request.getConversationId());
        customMetadata.put("userId", UserContextHolder.getUserId());
        return customMetadata;
    }



}
