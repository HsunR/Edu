package com.gpnu.ai.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;


import com.gpnu.ai.model.enums.MessageTypeEnum;
import lombok.Data;
import org.springframework.ai.chat.messages.*;

/**
 * 聊天消息表
 * @TableName chat_message
 */
@TableName(value ="chat_message")
@Data
public class ChatMessage implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
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

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 乐观锁版本号
     */
    @Version
    private Integer version;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;



    public static ChatMessage fromMessage(String conversationId, Message message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setConversationId(conversationId);
        // 根据消息类型获取内容
        if (message instanceof SystemMessage) {
            chatMessage.setContent(((SystemMessage) message).getText());
        } else if (message instanceof UserMessage) {
            chatMessage.setContent(((UserMessage) message).getText());
        } else if (message instanceof AssistantMessage) {
            chatMessage.setContent(((AssistantMessage) message).getText());
        }else if(message instanceof ToolResponseMessage){
            chatMessage.setContent(((ToolResponseMessage) message).getText());
        }
        chatMessage.setRole(message.getMessageType().getValue());
        Object promptTokens = message.getMetadata().get("promptTokens");
        Object completionTokens = message.getMetadata().get("completionTokens");
        if (promptTokens != null) {
            chatMessage.setTokens((Integer) promptTokens);
        }
        if (completionTokens != null) {
            chatMessage.setTokens((Integer) completionTokens);
        }
        chatMessage.setCreateTime(new Date());
        chatMessage.setUpdateTime(new Date());
        return chatMessage;
    }

    /**
     * 将数据库实体转换为Spring AI的Message对象
     */
    public Message toMessage() {
        MessageTypeEnum type = MessageTypeEnum.fromString(role);
        return switch (type) {
            case SYSTEM -> new SystemMessage(content);
            case USER -> new UserMessage(content);
            case ASSISTANT -> new AssistantMessage(content);
            default -> throw new IllegalArgumentException("Unknown message role: " + role);
        };
    }


}