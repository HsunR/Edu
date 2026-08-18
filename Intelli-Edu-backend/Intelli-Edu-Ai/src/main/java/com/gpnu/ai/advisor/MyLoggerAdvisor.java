package com.gpnu.ai.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.scheduler.Schedulers;
import reactor.core.scheduler.Scheduler;

@Slf4j

public class MyLoggerAdvisor implements BaseAdvisor {

	private final int order;
	private final Scheduler scheduler;

	public MyLoggerAdvisor() {
		this(0, BaseAdvisor.DEFAULT_SCHEDULER);
	}

	public MyLoggerAdvisor(int order, boolean protectFromBlocking) {
		this(order, protectFromBlocking ? BaseAdvisor.DEFAULT_SCHEDULER : Schedulers.immediate());
	}

	public MyLoggerAdvisor(int order, Scheduler scheduler) {
		this.order = order;
		this.scheduler = scheduler;
	}

	@Override
	public String getName() {
		return MyLoggerAdvisor.class.getSimpleName();
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	@Override
	public Scheduler getScheduler() {
		return this.scheduler;
	}

	@Override
	public ChatClientRequest before(ChatClientRequest request, AdvisorChain chain) {
		String userText = request.prompt().getUserMessage().getText();
		log.info("AI Request: {}", userText);
		return request; // 不修改 request，直接返回
	}

	@Override
	public ChatClientResponse after(ChatClientResponse response, AdvisorChain chain) {
		ChatResponse chatResponse = response.chatResponse();
		if (chatResponse != null) {
			var results = chatResponse.getResults();
			if (!results.isEmpty() && results.get(0).getOutput() != null) {
				String aiText = results.get(0).getOutput().getText();
				log.info("AI Response: {}", aiText);
			}
		}
		return response;
	}


}
