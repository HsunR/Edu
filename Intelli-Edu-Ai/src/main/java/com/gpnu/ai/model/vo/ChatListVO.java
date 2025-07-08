package com.gpnu.ai.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.gpnu.ai.model.entity.ChatMessage;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class ChatListVO implements Serializable {

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

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 一个会话对应的消息记录列表
     */
    private List<ChatMessage> chatMessages;

}
