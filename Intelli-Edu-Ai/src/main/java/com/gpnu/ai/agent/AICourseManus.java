package com.gpnu.ai.agent;


import com.gpnu.ai.advisor.MyLoggerAdvisor;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AICourseManus extends ToolCallAgent {



    public AICourseManus(ToolCallback[] allTools, @Qualifier("openAiChatModel")ChatModel chatModel) {
        super(allTools);
        this.setName("yuManus");
        String SYSTEM_PROMPT = """
                You are AICourseManus, an all-capable AI assistant, aimed at solving any task presented by the user.
                You have various tools at your disposal that you can call upon to efficiently complete complex requests.
                """;
        this.setSystemPrompt(SYSTEM_PROMPT);
        String NEXT_STEP_PROMPT = """
                Based on user needs, proactively select the most appropriate tool or combination of tools.
                For complex tasks, you can break down the problem and use different tools step by step to solve it.
                After using each tool, clearly explain the execution results and suggest the next steps.
                If you want to stop the interaction at any point, use the `terminate` tool/function call.
                """;
        this.setNextStepPrompt(NEXT_STEP_PROMPT);
        this.setMaxStep(20);
        // 初始化客户端
        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(
                        new MyLoggerAdvisor()
                )
                .build();
        this.setChatClient(chatClient);
    }
}
