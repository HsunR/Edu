package com.gpnu.ai.chatMemory;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 缓存+持久化混合聊天记忆
 */
@Component
public class HybridChatMemory implements ChatMemory {

    // 缓存窗口大小
    private static final int WINDOW_SIZE = 20;

    // 实例化缓存和数据库持久化层
    private final MultiSessionMessageWindowMemory cacheMemory = new MultiSessionMessageWindowMemory(WINDOW_SIZE);

    @Resource
    private MySQLChatMemory mySQLChatMemory;

    @Override
    public List<Message> get(String conversationId, int lastN) {
        // 优先从缓存获取消息
        List<Message> cachedMessages = cacheMemory.get(conversationId, lastN);
        if (cachedMessages.isEmpty()) {
            // 缓存未命中，从数据库加载最近的 lastN 条消息
            List<Message> dbMessages = mySQLChatMemory.get(conversationId, lastN);
            // 同时更新缓存
            cacheMemory.add(conversationId, dbMessages);
            return dbMessages;
        }
        return cachedMessages;
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        // 同时写入缓存和数据库
        cacheMemory.add(conversationId, messages);
        mySQLChatMemory.add(conversationId, messages);
    }

    @Override
    public void clear(String conversationId) {
        // 清空缓存和数据库
        cacheMemory.clear(conversationId);
        mySQLChatMemory.clear(conversationId);
    }
}
