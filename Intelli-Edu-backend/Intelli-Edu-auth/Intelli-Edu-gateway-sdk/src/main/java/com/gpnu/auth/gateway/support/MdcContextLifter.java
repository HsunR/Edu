package com.gpnu.auth.gateway.support;

import org.reactivestreams.Subscription;
import org.slf4j.MDC;
import reactor.core.CoreSubscriber;
import reactor.util.context.Context;

import java.util.Map;
import java.util.stream.Collectors;


public class MdcContextLifter<T> implements CoreSubscriber<T> {

    /**
     * Reactor Context 中用于标识 MDC 键值对的前缀
     */
    public static final String MDC_CONTEXT_PREFIX = "MDC.";

    private final CoreSubscriber<T> delegate;

    public MdcContextLifter(CoreSubscriber<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Context currentContext() {
        return delegate.currentContext();
    }

    @Override
    public void onSubscribe(Subscription s) {
        copyContextToMdc();
        delegate.onSubscribe(s);
    }

    @Override
    public void onNext(T t) {
        copyContextToMdc();
        delegate.onNext(t);
    }

    @Override
    public void onError(Throwable throwable) {
        copyContextToMdc();
        delegate.onError(throwable);
    }

    @Override
    public void onComplete() {
        copyContextToMdc();
        delegate.onComplete();
    }

    /**
     * 从 Reactor Context 中提取以 "MDC." 为前缀的键值对，
     * 设置到当前线程的 MDC 中。
     */
    private void copyContextToMdc() {
        Context context = delegate.currentContext();
        if (context.isEmpty()) {
            MDC.clear();
            return;
        }

        Map<String, String> mdcMap = context.stream()
                .filter(entry -> entry.getKey() instanceof String key && key.startsWith(MDC_CONTEXT_PREFIX))
                .collect(Collectors.toMap(
                        entry -> ((String) entry.getKey()).substring(MDC_CONTEXT_PREFIX.length()),
                        entry -> String.valueOf(entry.getValue())
                ));

        if (mdcMap.isEmpty()) {
            MDC.clear();
        } else {
            MDC.setContextMap(mdcMap);
        }
    }
}