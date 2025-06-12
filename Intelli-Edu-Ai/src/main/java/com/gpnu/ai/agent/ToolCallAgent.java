package com.gpnu.ai.agent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;

import com.gpnu.ai.agent.model.AgentState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.stream.Collectors;

/**
 *  处理工具调用的基础代理类，具体实现了think和act方法，可以用作创建实例的父类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class ToolCallAgent extends ReActAgent{


    //可用的工具
    private final ToolCallback[] availableTools;

    //保存工具调用信息的响应结果（需要调用那些工具）
    private ChatResponse toolCallResponse;

    //工具调用管理者
    private final ToolCallingManager toolCallingManager;

    // 禁用 SpringAI 内置的工具调用机制，自己维护选项和消息上下文
     private final ChatOptions chatOptions ;


    public ToolCallAgent(ToolCallback[] availableTools ){
        super();
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        //禁止Spring AI 内置的工具调用机制,自己维护会话上下文
        this.chatOptions = DashScopeChatOptions.builder().withInternalToolExecutionEnabled(true).build();
    }

    /**
     * 处理当前状态，并决定下一步行动
     * @return
     */
    @Override
    public boolean think() {
        //1. 校验提示词，拼接用户提示词
        if(StrUtil.isNotBlank(getNextStepPrompt())){
            UserMessage userMessage = new UserMessage(getNextStepPrompt());
            this.getMessages().add(userMessage);
        }
        //2.调用 AI 大模型，获取工具调用的结果
        List<Message> messageList = getMessages();
        try {
            Prompt prompt = new Prompt(messageList,chatOptions);
            ChatResponse chatResponse = getChatClient().prompt(prompt)
                    .tools(availableTools)
                    .call()
                    .chatResponse();
            //记录响应，用于等下Act
            this.toolCallResponse = chatResponse;
            //3.解析工具调用的结果，获取所要调用的工具
            // 助手消息
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            //获取要调用的工具列表
            List<AssistantMessage.ToolCall> toolCalls = assistantMessage.getToolCalls();
            //输出提示信息
            String result = assistantMessage.getText();
            log.info(getName()+ "的思考: " + result);
            log.info(getName() + "选择了: " + toolCalls.size()+ " 个工具进行调用");
            String toolCallInfo = toolCalls.stream().map(
                    toolCall -> "工具名称: " + toolCall.name() + ", 参数: " + toolCall.arguments()
            ).collect(Collectors.joining("\n"));
            log.info(toolCallInfo);
            //4.如果没有工具调用，返回false
            if(toolCalls.isEmpty()){
                //只有不调用工具时候，才记录助手消息
                getMessages().add(assistantMessage);
                return false;
            }else{
                return true;
            }
        } catch (Exception e) {
               log.error(getName()+ " 思考过程遇到问题: "+e.getMessage());
               getMessages().add(new AssistantMessage("思考过程遇到问题: " + e.getMessage()));
            return false;
        }
    }

    /**
     * 执行工具调用并处理结果
     * @return
     */
    @Override
    public String act() {
        if(!toolCallResponse.hasToolCalls()){
            return "没有工具调用";
        }
        Prompt prompt = new Prompt(getMessages(),chatOptions);
        //调用工具
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallResponse);
        //记录消息上下文(重点了解)
        setMessages(toolExecutionResult.conversationHistory());
        ToolResponseMessage toolResponseMessage =(ToolResponseMessage) CollUtil.getLast(toolExecutionResult.conversationHistory());
        //是否调用了终止工具
        boolean terminateToolCalled = toolResponseMessage.getResponses().stream()
                .anyMatch(response -> response.name().equals("doTerminate"));
        if (terminateToolCalled) {
            //任务结束
            setState(AgentState.FINISHED);
        }
        String results = toolResponseMessage.getResponses().stream().map(response -> "工具" + response.name() + " 返回的结果： " + response.responseData()).collect(Collectors.joining("\n"));
        log.info(results);
        return results;
    }

    @Override
    public String step() {
        return super.step();
    }
}
