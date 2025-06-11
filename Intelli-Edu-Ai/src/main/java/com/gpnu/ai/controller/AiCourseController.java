package com.gpnu.ai.controller;

import com.gpnu.ai.app.AICourseAPP;
import com.gpnu.common.common.BaseResponse;
import com.gpnu.common.common.ResultUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/aiCourse")
public class AiCourseController {

    @Resource
    private AICourseAPP aiCourseAPP;

    @GetMapping("/doChatWithSse")
    public BaseResponse<Flux<String>> doChatAiCourseWithSSE(@RequestParam("userPrompt") String userPrompt ,@RequestParam("chatId")  String chatId){
        return ResultUtils.success(aiCourseAPP.doChatByStream(userPrompt, chatId));
    }

    @GetMapping("/doChatByStream")
    public Flux<String> doChatByStream(@RequestParam("userPrompt") String userPrompt ,
                                       @RequestParam("chatId")  String chatId){
        return aiCourseAPP.doChatByStream(userPrompt, chatId);
    }

}
