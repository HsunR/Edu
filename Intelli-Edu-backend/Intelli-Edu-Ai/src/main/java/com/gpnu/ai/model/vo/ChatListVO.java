package com.gpnu.ai.model.vo;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ChatListVO implements Serializable {

    /**
     * 会话id
     */
    private String conversationId;


    /**
     * 用户id
     */
    private String userId;


    /**
     * 会话标题
     */
    private String conversationTitle;



    /**
     * 一个会话对应的消息记录列表
     */
    private List<ChatMessageVO> chatMessageVOS;

}
