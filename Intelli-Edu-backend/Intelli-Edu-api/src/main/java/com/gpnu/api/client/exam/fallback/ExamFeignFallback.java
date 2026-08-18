package com.gpnu.api.client.exam.fallback;

import com.gpnu.api.client.exam.ExamFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ExamFeignFallback implements FallbackFactory<ExamFeignClient> {

    @Override
    public ExamFeignClient create(Throwable cause) {
        log.error("ExamFeignClient fallback triggered", cause);
        return new ExamFeignClient() {
            @Override
            public Long getQuestionCourseId(Long questionId) {
                return null;
            }
        };
    }
}
