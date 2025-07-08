package com.gpnu.ai.controller;


import com.gpnu.ai.app.AICourseAPP;
import com.gpnu.ai.rag.MyTokenTextSplitter;
import com.gpnu.ai.service.ChatListService;
import com.gpnu.ai.utils.DocumentParsingUtil;
import com.gpnu.common.common.BaseResponse;
import com.gpnu.common.common.ResultUtils;
import com.gpnu.common.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;

import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/aiCourse")
public class AiCourseController {

    @Resource
    private AICourseAPP aiCourseAPP;

    @Resource
    private ChatListService chatListService;

    @Resource(name = "redisVectorStore")
    private RedisVectorStore redisVectorStore;

    @Operation(summary = "基础的聊天接口仅提供记忆功能")
    @GetMapping("/doChatByStream")
    public Flux<BaseResponse<String>> doChatByStream(@RequestParam("userPrompt") String userPrompt ,
                                       @RequestParam("chatId")  String chatId){
        return aiCourseAPP.doChatByStream(userPrompt, chatId)
                .map(ResultUtils::success)
                .onErrorResume(e -> Flux.just(new BaseResponse<>(ErrorCode.OPERATION_ERROR.getCode(), null, "聊天失败: " + e.getMessage())));
    }


    @Operation(summary = "开启了RAG的聊天接口")
    @GetMapping("/doChatWithRagByStream")
    public Flux<BaseResponse<String>> doChatWithRagByStream(@RequestParam("userPrompt") String userPrompt ,
                                       @RequestParam("chatId")  String chatId){
        return aiCourseAPP.doChatWithRagByStream(userPrompt, chatId)
                .map(ResultUtils::success)
                .onErrorResume(e -> Flux.just(new BaseResponse<>(ErrorCode.OPERATION_ERROR.getCode(), null, "聊天失败: " + e.getMessage())));
    }

    @GetMapping("/doChatByStreamMono")
    public Mono<BaseResponse<Flux<String>>> doChatByStreamMono(@RequestParam("userPrompt") String userPrompt ,
                                       @RequestParam("chatId")  String chatId){
        return Mono.just(ResultUtils.success(aiCourseAPP.doChatByStream(userPrompt, chatId)));
    }


    @Operation(summary = "开启了工具调用和RAG的聊天接口(多模态响应)")
    @GetMapping("/doChatWithToolAndRagByStream")
    public Flux<BaseResponse<String>> doChatWithToolAndRag(
            @RequestParam("userPrompt") String userPrompt,
            @RequestParam( "chatId") String chatId) {
        return aiCourseAPP.doChatWithToolsByStream(userPrompt, chatId)
                .map(ResultUtils::success)
                .onErrorResume(e -> Flux.just(new BaseResponse<>(ErrorCode.OPERATION_ERROR.getCode(), null, "聊天失败: " + e.getMessage())));

    }

    @PostMapping("/uploadChat")
    public Flux<BaseResponse<String>> uploadChat(@RequestParam("userPrompt")String userPrompt,
                                                 @RequestParam("chatId") String chatId,
                                                 @RequestPart(value = "multipartFile",required = false) MultipartFile multipartFile
                                                 ) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            List<Document> documents = DocumentParsingUtil.parse(multipartFile);
            MyTokenTextSplitter myTokenTextSplitter = new MyTokenTextSplitter();
            List<Document> splitDocuments = myTokenTextSplitter.splitDocuments(documents);
            redisVectorStore.add(splitDocuments);

        }
        return aiCourseAPP.doChatWithRagByStream(userPrompt, chatId)
                .map(ResultUtils::success)
                .onErrorResume(e -> Flux.just(new BaseResponse<>(ErrorCode.OPERATION_ERROR.getCode(), null, "上传聊天失败: " + e.getMessage())));



    }




}
