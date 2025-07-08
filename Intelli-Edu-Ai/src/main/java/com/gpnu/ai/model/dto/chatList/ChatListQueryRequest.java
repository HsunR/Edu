package com.gpnu.ai.model.dto.chatList;

import lombok.Data;

@Data
public class ChatListQueryRequest implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Id 主键
     */
    private Long id;

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
