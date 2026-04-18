package com.gpnu.ai.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpnu.ai.model.dto.chatList.ChatListAddRequest;
import com.gpnu.ai.model.dto.chatList.ChatListDeleteRequest;
import com.gpnu.ai.model.dto.chatList.ChatListUpdateRequest;
import com.gpnu.ai.model.entity.ChatList;
import com.gpnu.ai.model.entity.ChatMessage;
import com.gpnu.ai.model.vo.ChatListVO;
import com.gpnu.ai.model.vo.ChatMessageVO;
import com.gpnu.ai.service.ChatListService;
import com.gpnu.ai.mapper.ChatListMapper;
import com.gpnu.ai.service.ChatMessageService;
import com.gpnu.auth.resource.context.UserContextHolder;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.exception.ThrowUtils;
import com.gpnu.common.service.RedisService;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
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

    @Resource
    private ChatClient nameChatClient;





    @Override
    public ChatListVO createChatList(ChatListAddRequest addRequest) {
        ChatListVO chatListVO = new ChatListVO();
        //先调用AI生成本次标题
        ChatResponse chatResponse = nameChatClient
                .prompt().
                user(addRequest.getUserMessage()).
                call().
                chatResponse();
        //创建ChatList对象
        String title = chatResponse.getResult().getOutput().getText();
        if(title == null || title.isEmpty()) {
            title = "新会话";
        }
        ChatList chatList = new ChatList();
        chatList.setConversationTitle(title);
        //获取当前线程的用户ID
        Long userId = UserContextHolder.getUserId();
        ThrowUtils.throwIf(ObjectUtil.isNull(userId), ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        chatList.setUserId(userId);
        //存到数据库中
        boolean result = this.save(chatList);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "创建会话失败");

        BeanUtils.copyProperties(chatList, chatListVO);


        return chatListVO;
    }

    @Override
    public ChatListVO getChatListByConversationId(String conversationId) {
        //查询当前会话的所有消息
        List<ChatMessage> chatMessages = chatMessageService.list(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getConversationId, conversationId)
                .orderByAsc(ChatMessage::getCreateTime)
                .eq(ChatMessage::getIsDelete, 0)
        );
        //将其转为ChatMessageVO
        List<ChatMessageVO> chatMessageVOs = new ArrayList<>();
        if (chatMessages != null && !chatMessages.isEmpty()) {
            chatMessageVOs = chatMessages.stream().map(chatMessage -> {
                ChatMessageVO chatMessageVO = new ChatMessageVO();
                BeanUtils.copyProperties(chatMessage, chatMessageVO);
                return chatMessageVO;
            }).toList();
        }
        ChatList chatList = this.getOne(new LambdaQueryWrapper<ChatList>()
                .eq(ChatList::getConversationId, conversationId)
                .eq(ChatList::getIsDelete, 0));
        ThrowUtils.throwIf(chatList == null, ErrorCode.NOT_FOUND_ERROR, "会话不存在");
        ChatListVO chatListVO = new ChatListVO();
        BeanUtils.copyProperties(chatList, chatListVO);
        chatListVO.setChatMessageVOS(chatMessageVOs);
        return chatListVO;
    }

    @Override
    public List<ChatListVO> getChatListByUserId(Long userId) {
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
                // 将ChatMessage转换为ChatMessageVO
                List<ChatMessageVO> chatMessageVOS = new ArrayList<>();
                if (chatMessages != null && !chatMessages.isEmpty()) {
                    chatMessageVOS = chatMessages.stream().map(chatMessage -> {
                        ChatMessageVO chatMessageVO = new ChatMessageVO();
                        BeanUtils.copyProperties(chatMessage, chatMessageVO);
                        return chatMessageVO;
                    }).toList();
                }
                chatListVO.setChatMessageVOS(chatMessageVOS);
                return chatListVO;
            }).toList();
        }
        return new ArrayList<>();
    }


    @Override
    public ChatListVO updateChatList(ChatListUpdateRequest updateRequest) {
        ChatListVO chatListVO = new ChatListVO();
        ChatList chatList = new ChatList();
        BeanUtils.copyProperties(updateRequest, chatList);
        boolean result = this.updateById(chatList);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "更新会话失败");
        BeanUtils.copyProperties(chatList, chatListVO);
        return chatListVO;
    }

    @Override
    public boolean deleteChatList(ChatListDeleteRequest chatListDeleteRequest) {
        ThrowUtils.throwIf(ObjectUtil.isNull(chatListDeleteRequest.getConversationId()) || chatListDeleteRequest.getConversationId().isEmpty(),
                ErrorCode.PARAMS_ERROR, "会话ID不能为空");
        boolean result = this.removeById(chatListDeleteRequest.getConversationId());
        //级联删除该聊天会话下的所有消息
        boolean result1 = chatMessageService.remove(new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getConversationId, chatListDeleteRequest.getConversationId())
                .eq(ChatMessage::getIsDelete, 0));
       //删除失败的回滚事务
        ThrowUtils.throwIf(!result || !result1, ErrorCode.SYSTEM_ERROR, "删除聊天失败");
        return false;
    }
}




