package com.gpnu.ai.controller;

import com.gpnu.ai.model.dto.chatList.ChatListAddRequest;
import com.gpnu.ai.model.dto.chatList.ChatListDeleteRequest;
import com.gpnu.ai.model.dto.chatList.ChatListQueryRequest;
import com.gpnu.ai.model.dto.chatList.ChatListUpdateRequest;
import com.gpnu.ai.model.vo.ChatListVO;
import com.gpnu.ai.service.ChatListService;

import com.gpnu.common.common.BaseResponse;
import com.gpnu.common.common.ResultUtils;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.exception.ThrowUtils;
import com.gpnu.common.utils.contextHolder.UserContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chatList")
public class ChatListController {

    @Resource
    private ChatListService chatListService;

    @PostMapping("/getUserChatList")
    @Operation(summary = "获取当前用户的聊天会话列表")
    public BaseResponse<List<ChatListVO>> getUserChatList(@RequestBody ChatListQueryRequest queryRequest) {
        ThrowUtils.throwIf(queryRequest == null, ErrorCode.PARAMS_ERROR,"查询请求体不能为空");
        ThrowUtils.throwIf( queryRequest.getUserId()== null  , ErrorCode.PARAMS_ERROR,"用户ID不能为空");
        List<ChatListVO> chatList = chatListService.getChatListByUserId(queryRequest.getUserId());
        return ResultUtils.success(chatList);
    }

    @PostMapping("/getChatListByConversationId")
    @Operation(summary = "根据会话ID获取聊天记录")
    public BaseResponse<ChatListVO> getChatListByConversationId(@RequestBody ChatListQueryRequest queryRequest) {
        ThrowUtils.throwIf(queryRequest == null, ErrorCode.PARAMS_ERROR,"查询请求体不能为空");
        ThrowUtils.throwIf(queryRequest.getConversationId() == null || queryRequest.getConversationId().isEmpty(), ErrorCode.PARAMS_ERROR,"会话ID不能为空");
        ChatListVO chatList = chatListService.getChatListByConversationId(queryRequest.getConversationId());
        return ResultUtils.success(chatList);
    }


    @PostMapping("/createChatList")
    @Operation(summary = "创建聊天会话")
    public BaseResponse<ChatListVO> createChatList(@RequestBody ChatListAddRequest chatListAddRequest){
        ThrowUtils.throwIf(chatListAddRequest == null, ErrorCode.PARAMS_ERROR,"请求体不能为空");
        ChatListVO vo = chatListService.createChatList(chatListAddRequest);

        return ResultUtils.success(vo);
    }

    @PostMapping("/updateChatList")
    @Operation(summary = "更新聊天会话")
    public BaseResponse<ChatListVO> updateChatList(@RequestBody ChatListUpdateRequest updateRequest ) {
        ThrowUtils.throwIf(updateRequest == null, ErrorCode.PARAMS_ERROR,"请求体不能为空");
        ThrowUtils.throwIf(updateRequest.getConversationId() == null || updateRequest.getConversationId().isEmpty(), ErrorCode.PARAMS_ERROR,"会话ID不能为空");

        ChatListVO result = chatListService.updateChatList(updateRequest);
        return ResultUtils.success(result);
    }

    @PostMapping("/deleteChatList")
    @Operation(summary = "删除聊天会话")
    public BaseResponse<Boolean> deleteChatList(@RequestBody ChatListDeleteRequest chatListDeleteRequest) {
        ThrowUtils.throwIf(chatListDeleteRequest == null , ErrorCode.PARAMS_ERROR,"会话ID不能为空");
        boolean result = chatListService.deleteChatList(chatListDeleteRequest);
        return ResultUtils.success(result);
    }

}
