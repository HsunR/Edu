package com.gpnu.auth.gateway.config;

import com.gpnu.auth.gateway.support.MdcContextLifter;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;

/**
 * 注册 Reactor Hook，使每个响应式操作符都包装上 {@link MdcContextLifter}
 * <p>
 * 效果：Reactor 链中每次线程切换时，自动将 Reactor Context 中的值同步到 MDC。
 * 配合 {@link com.gpnu.auth.gateway.filter.RequestIdRelayFilter} 将 requestId
 * 写入 Reactor Context，即可实现 Gateway 全链路日志追踪。
 * </p>
 */
@Slf4j
@Configuration
public class MdcContextConfig {

    private static final String HOOK_KEY = "mdc-context-propagation";

    @PostConstruct
    public void registerHook() {
        Hooks.onEachOperator(HOOK_KEY,
                Operators.lift((scannable, subscriber) -> new MdcContextLifter<>(subscriber)));
        log.info("Registered Reactor MDC context propagation hook.");
    }

    @PreDestroy
    public void removeHook() {
        Hooks.resetOnEachOperator(HOOK_KEY);
        log.info("Removed Reactor MDC context propagation hook.");
    }
}