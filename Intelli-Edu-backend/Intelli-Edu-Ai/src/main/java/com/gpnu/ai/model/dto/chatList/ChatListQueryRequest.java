package com.gpnu.ai.model.dto.chatList;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class ChatListQueryRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 会话id
     */
    private String conversationId;

    /**
     * 用户id
     */
    private Long userId;


    /**
     * 会话标题
     */
    private String conversationTitle;
}
