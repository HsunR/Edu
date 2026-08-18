package com.gpnu.ai.service;

import com.gpnu.ai.model.dto.chatList.ChatListAddRequest;
import com.gpnu.ai.model.dto.chatList.ChatListDeleteRequest;
import com.gpnu.ai.model.dto.chatList.ChatListUpdateRequest;
import com.gpnu.ai.model.entity.ChatList;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gpnu.ai.model.vo.ChatListVO;

import java.util.List;

/**
* @author Chenxingdong
* @description 针对表【chat_list(聊天会话表)】的数据库操作Service
* @createDate 2025-06-13 16:12:38
*/
public interface ChatListService extends IService<ChatList> {


    /**
     * 创建新的聊天会话
     */
    public ChatListVO createChatList(ChatListAddRequest chatListAddRequest);

    /**
     * 查询根据会话ID查询当前的聊天记录
     */
    public ChatListVO getChatListByConversationId(String conversationId);

    /**
     * 查询当前用户的所有聊天会话
     */
    public List<ChatListVO> getChatListByUserId(Long userId);


    public ChatListVO updateChatList(ChatListUpdateRequest updateRequest);

    public boolean deleteChatList(ChatListDeleteRequest chatListDeleteRequest);
}
