package com.gpnu.ai.model.dto.chatList;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChatListAddRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 会话id
     */
    private String conversationId;

    /**
     * 会话标题
     */
    private String conversationTitle;

}
