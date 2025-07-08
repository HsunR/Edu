package com.gpnu.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpnu.ai.model.dto.chatList.ChatListAddRequest;
import com.gpnu.ai.model.dto.chatList.ChatListUpdateRequest;
import com.gpnu.ai.model.entity.ChatList;
import com.gpnu.ai.model.entity.ChatMessage;
import com.gpnu.ai.model.vo.ChatListVO;
import com.gpnu.ai.service.ChatListService;
import com.gpnu.ai.mapper.ChatListMapper;
import com.gpnu.ai.service.ChatMessageService;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.exception.ThrowUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @author Chenxingdong
* @description 针对表【chat_list(聊天会话表)】的数据库操作Service实现
* @createDate 2025-06-13 16:12:38
*/
@Service
public class ChatListServiceImpl extends ServiceImpl<ChatListMapper, ChatList>
    implements ChatListService{

    @Resource
    private ChatMessageService chatMessageService;


    @Override
    public boolean createChatList(ChatListAddRequest addRequest) {
        ThrowUtils.throwIf(addRequest.getConversationId() == null, ErrorCode.PARAMS_ERROR,"会话ID不能为空");
        ThrowUtils.throwIf(addRequest.getUserId() == null,ErrorCode.PARAMS_ERROR,"用户ID不能为空");
        ChatList chatList = new ChatList();
        BeanUtils.copyProperties(addRequest, chatList);
        return this.save(chatList);
    }

    @Override
    public ChatListVO getChatListByConversationId(String conversationId) {
        List<ChatMessage> chatMessages = chatMessageService.list(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getConversationId, conversationId)
                .orderByAsc(ChatMessage::getCreateTime)
                .eq(ChatMessage::getIsDelete, 0)
        );
        ChatList chatList = this.getOne(new LambdaQueryWrapper<ChatList>()
                .eq(ChatList::getConversationId, conversationId)
                .eq(ChatList::getIsDelete, 0));
        ThrowUtils.throwIf(chatList == null, ErrorCode.NOT_FOUND_ERROR, "会话不存在");
        ChatListVO chatListVO = new ChatListVO();
        BeanUtils.copyProperties(chatList, chatListVO);
        chatListVO.setChatMessages(chatMessages);
        return chatListVO;
    }

    @Override
    public List<ChatListVO> getChatListByUserId(String userId) {
        // 查询当前用户的所有聊天会话
        List<ChatList> chatLists = this.list(new LambdaQueryWrapper<ChatList>()
                .eq(ChatList::getUserId, userId)
                .eq(ChatList::getIsDelete, 0)
                .orderByDesc(ChatList::getUpdateTime));
        // 根据每个聊天会话查询对应聊天窗口的消息
        if (chatLists != null && !chatLists.isEmpty()) {
            return chatLists.stream().map(chatList -> {
                ChatListVO chatListVO = new ChatListVO();
                BeanUtils.copyProperties(chatList, chatListVO);
                List<ChatMessage> chatMessages = chatMessageService.list(new LambdaQueryWrapper<ChatMessage>()
                        .eq(ChatMessage::getConversationId, chatList.getConversationId())
                        .eq(ChatMessage::getIsDelete, 0)
                        .orderByAsc(ChatMessage::getCreateTime));
                chatListVO.setChatMessages(chatMessages);
                return chatListVO;
            }).toList();
        }
        return new ArrayList<>();
    }


    @Override
    public boolean updateChatList(ChatListUpdateRequest updateRequest) {
        ChatList chatList = new ChatList();
        BeanUtils.copyProperties(updateRequest, chatList);
        return this.updateById(chatList);
    }
}




