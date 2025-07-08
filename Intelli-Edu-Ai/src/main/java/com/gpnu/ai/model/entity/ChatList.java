package com.gpnu.ai.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 聊天会话表
 * @TableName chat_list
 */
@TableName(value ="chat_list")
@Data
public class ChatList implements Serializable {
    /**
     * Id 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
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
     * 逻辑删除标志
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}