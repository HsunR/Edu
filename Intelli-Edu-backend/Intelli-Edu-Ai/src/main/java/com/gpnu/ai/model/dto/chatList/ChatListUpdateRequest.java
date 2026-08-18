package com.gpnu.ai.model.dto.chatList;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChatListUpdateRequest implements Serializable {
    private static final long serialVersionUID = 1L;



    /**
     * 会话id,主键
     */
    private String conversationId;

    /**
     * 会话标题
     */
    private String conversationTitle;
}
