package com.gpnu.ai.model.dto.chatList;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChatListDeleteRequest implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 会话id,主键
     */
    private String conversationId;

}
