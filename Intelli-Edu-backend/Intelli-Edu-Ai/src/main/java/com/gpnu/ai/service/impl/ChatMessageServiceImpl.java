package com.gpnu.ai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpnu.ai.model.entity.ChatMessage;
import com.gpnu.ai.mapper.ChatMessageMapper;
import com.gpnu.ai.service.ChatMessageService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Chenxingdong
* @description 针对表【chat_message(聊天消息表)】的数据库操作Service实现
* @createDate 2025-05-09 11:44:10
*/
@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage>
    implements ChatMessageService {

    @Override
    public List<ChatMessage> findLatestMessages(String conversationId, int limit) {
        // 只根据conversationId查询，不涉及messageType
        return this.lambdaQuery()
                .eq(ChatMessage::getConversationId, conversationId)
                .orderByDesc(ChatMessage::getCreateTime)
                .last("limit " + limit)
                .list();
    }


    @Override
    public List<ChatMessage> findAllMessageByConversationId(String conversationId) {
        // 只根据conversationId查询，不涉及messageType
        return this.lambdaQuery()
                .eq(ChatMessage::getConversationId, conversationId)
                .list();
    }

    @Override
    public Boolean deleteByConversationId(String conversationId) {
        return this.remove(lambdaQuery().eq(ChatMessage::getConversationId,conversationId));
    }

}




