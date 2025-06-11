package com.gpnu.ai.agent;

import cn.hutool.core.util.StrUtil;
import com.gpnu.ai.agent.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 抽象的智能体类，包含了核心属性、提示词以及代理状态等信息。
 * 该类可以作为其他具体智能体的基类，提供基本的功能和属性。
 * 子类必须实现step方法
 */
@Data
@Slf4j
public abstract class BaseAgent {

    //核心属性
    private String name;

    //提示词
    private String systemPrompt;
    private String nextStepPrompt;


    //代理状态
    private AgentState state = AgentState.IDLE;

    //执行步骤控制
    private int currentStep = 0;
    private int maxStep = 10;

    // LLM大模型
    private ChatClient chatClient;

    //Memory 记忆(需要自主上维护会话上下文)
    private List<Message> messages = new ArrayList<>();


    /**
     * 运行代理
     * @param userPrompt 用户输入的提示词
     * @return 返回代理的响应结果
     */
    public String run(String userPrompt){
        // 基础校验（如果更加规范的话，需要自定义异常）
        if(this.state != AgentState.IDLE){
            throw new RuntimeException("Cannot run agent from state: " + this.state);
        }
        if(StrUtil.isBlank(userPrompt)){
            throw new RuntimeException("User prompt cannot be empty");
        }

        //执行，更改状态
        this.state = AgentState.RUNNING;
        //记录消息上下文
        messages.add(new UserMessage(userPrompt));
        //保存结果列表
        List<String> results = new ArrayList<>();

        try {
            //执行循环
            for (int i = 0; i < maxStep && state != AgentState.FINISHED; i++){
                int stepNum = i + 1;
                currentStep = stepNum;
                log.info("Running step {} of {}", stepNum, maxStep);
                //单步执行
                String stepResult = step();
                String result = "step " + stepNum + ": " + stepResult;
                results.add(result);
            }
            //检查是否超出步骤限制
            if (currentStep >= maxStep) {
                this.state = AgentState.FINISHED; // 设置为完成状态
                results.add("Terminated: Reached max steps ("+maxStep+")");
            }
            //拼接结果
            return String.join("\n", results);

        }catch (Exception e){
            log.error("Error during agent execution", e);
            this.state = AgentState.ERROR; // 设置为错误状态
            return "Error during agent execution: " + e.getMessage();
        }finally {
            this.cleanup();
        }

    }

    public SseEmitter runStream(String userPrompt){
        SseEmitter sseEmitter = new SseEmitter(300000L);
        //使用异步输出
        CompletableFuture.runAsync(() -> {
            // 基础校验（如果更加规范的话，需要自定义异常）
            try {
                if(this.state != AgentState.IDLE){
                    sseEmitter.send("错误：无法从状态运行代理:"+ this.state);
                    sseEmitter.complete();
                    return;
                }
                if(StrUtil.isBlank(userPrompt)){
                    sseEmitter.send("错误：无法使用空提示词运行代理:");
                    sseEmitter.complete();
                    return;
                }
            }catch (Exception e){
                sseEmitter.completeWithError(e);
            }

            //执行，更改状态
            this.state = AgentState.RUNNING;
            //记录消息上下文
            messages.add(new UserMessage(userPrompt));
            //保存结果列表
            List<String> results = new ArrayList<>();

            try {
                //执行循环
                for (int i = 0; i < maxStep && state != AgentState.FINISHED; i++){
                    int stepNum = i + 1;
                    currentStep = stepNum;
                    log.info("Running step {} of {}", stepNum, maxStep);
                    //单步执行
                    String stepResult = step();
                    String result = "step " + stepNum + ": " + stepResult;
                    results.add(result);
                    //完成一步就推送到前端
                    sseEmitter.send(result);
                }
                //检查是否超出步骤限制
                if (currentStep >= maxStep) {
                    this.state = AgentState.FINISHED; // 设置为完成状态
                    results.add("Terminated: Reached max steps ("+maxStep+")");
                    sseEmitter.send("执行结束，达到最大步骤( "+maxStep+" )");
                }
                //拼接结果
                sseEmitter.complete();


            }catch (Exception e){
                log.error("Error during agent execution", e);
                this.state = AgentState.ERROR; // 设置为错误状态
                try {
                    sseEmitter.send("执行错误"+ e.getMessage());
                    sseEmitter.complete();
                } catch (IOException ex) {

                    sseEmitter.completeWithError(ex);
                }
            }finally {
                this.cleanup();
            }
        });
        //设置超时回调
        sseEmitter.onTimeout(() -> {
            log.info("SSEEmitter timed out");
            this.state = AgentState.ERROR; // 设置为错误状态
            sseEmitter.complete();
        });
        sseEmitter.onCompletion(() -> {
            if(this.state == AgentState.RUNNING){
                this.state = AgentState.FINISHED;
            }
            this.cleanup();
            log.info("SSEEmitter completed");
        });
        return sseEmitter;
    }


    /**
     * 执行一步操作(具体一步的操作交由子类来实现)
     * @return 返回执行结果
     */
    public abstract String step();

    /**
     * 清理资源
     */
    protected void cleanup(){
        //交由子类来实现
    }



}
