package com.gpnu.api.client.exam;

import com.gpnu.api.client.exam.fallback.ExamFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "Intelli-Edu-exam", path = "/api/exam",
        fallbackFactory = ExamFeignFallback.class)
public interface ExamFeignClient {

    @GetMapping("/inner/questions/{questionId}/course")
    Long getQuestionCourseId(@PathVariable("questionId") Long questionId);
}
