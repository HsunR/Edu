package com.gpnu.ai.chatMemory;


import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

import java.util.ArrayList;
import java.util.Collections;
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
    public List<Message> get(@NotNull String conversationId) {
        // 优先从缓存获取
        List<Message> cachedMessages = cacheMemory.get(conversationId);
        if (cachedMessages.isEmpty()) {
            // 缓存为空，从数据库加载
            List<Message> dbMessages = mySQLChatMemory.get(conversationId);
            List<Message> lastN = getLastN(dbMessages, WINDOW_SIZE);
            cacheMemory.add(conversationId, lastN);
            return lastN;
        }
        return getLastN(cachedMessages, WINDOW_SIZE);
    }

    private List<Message> getLastN(List<Message> messages, int lastN) {
        if (messages == null || messages.isEmpty()) return Collections.emptyList();
        return messages.size() > lastN ? messages.subList(messages.size() - lastN, messages.size()) : new ArrayList<>(messages);
    }

    @Override
    public void add(@NotNull String conversationId, @NotNull List<Message> messages) {
        cacheMemory.add(conversationId, messages);
        mySQLChatMemory.add(conversationId, messages);
    }

    @Override
    public void clear(@NotNull String conversationId) {
        cacheMemory.clear(conversationId);
        mySQLChatMemory.clear(conversationId);
    }
}
