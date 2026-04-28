package com.gpnu.exam.exam.controller;

import com.gpnu.api.dto.exam.ExamSimpleDTO;
import com.gpnu.exam.exam.service.IExamService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inner")
@Tag(name = "内部接口", description = "Feign内部调用，不走网关")
@Hidden
public class InnerExamController {

    @Resource
    private IExamService examService;

    @GetMapping("/exams/{examId}")
    @Operation(summary = "获取考试简要信息")
    public ExamSimpleDTO getExamSimple(@PathVariable Long examId) {
        return examService.getExamSimple(examId);
    }
}
