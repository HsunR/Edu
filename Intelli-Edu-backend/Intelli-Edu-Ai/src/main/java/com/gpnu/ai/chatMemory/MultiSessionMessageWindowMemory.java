package com.gpnu.ai.chatMemory;

import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 多会话窗口消息记忆，支持按 conversationId 维护固定长度消息窗口
 */
@Component
public class MultiSessionMessageWindowMemory implements ChatMemory {

    private final int windowSize;
    // key: conversationId，value: 对应会话的消息窗口（链表）
    private final Map<String, Deque<Message>> conversationWindows = new ConcurrentHashMap<>();

    public MultiSessionMessageWindowMemory() {
        this.windowSize = 20; // 默认窗口大小，可构造时参数化
    }

    public MultiSessionMessageWindowMemory(int windowSize) {
        this.windowSize = windowSize;
    }

    @Override
    public List<Message> get(@NotNull String conversationId) {
        Deque<Message> window = conversationWindows.get(conversationId);
        return window == null ? Collections.emptyList() : new ArrayList<>(window);
    }

    @Override
    public void add(@NotNull String conversationId, @NotNull List<Message> messages) {
        conversationWindows.compute(conversationId, (key, window) -> {
            if (window == null) {
                window = new LinkedList<>();
            }
            for (Message message : messages) {
                window.addLast(message);
                if (window.size() > windowSize) {
                    window.removeFirst();
                }
            }
            return window;
        });
    }

    @Override
    public void clear(@NotNull String conversationId) {
        conversationWindows.remove(conversationId);
    }
}
