package com.gpnu.ai.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class ChatMessageVO implements Serializable {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 会话ID
     */
    private String conversationId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 角色：user/assistant/system
     */
    private String role;

    /**
     * 消息token数
     */
    private Integer tokens;
}
