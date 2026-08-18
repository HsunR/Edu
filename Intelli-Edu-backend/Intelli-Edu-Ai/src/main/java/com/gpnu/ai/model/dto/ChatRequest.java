package com.gpnu.ai.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChatRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userPrompt;

    private String conversationId;

    private Boolean isUseTool;

}
