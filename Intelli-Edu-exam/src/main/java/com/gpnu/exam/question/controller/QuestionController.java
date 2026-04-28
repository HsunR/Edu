package com.gpnu.exam.question.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gpnu.auth.resource.context.UserContextHolder;
import com.gpnu.exam.question.model.dto.QuestionCreateRequest;
import com.gpnu.exam.question.model.dto.QuestionQueryRequest;
import com.gpnu.exam.question.model.dto.QuestionUpdateRequest;
import com.gpnu.exam.question.model.vo.QuestionVO;
import com.gpnu.exam.question.service.IQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/questions")
@Tag(name = "题目管理", description = "题目的创建、编辑、删除、查询")
public class QuestionController {

    @Resource
    private IQuestionService questionService;

    @PostMapping("/banks/{bankId}")
    @Operation(summary = "在题库中创建题目")
    public QuestionVO createQuestion(@PathVariable Long bankId,
                                     @RequestBody @Validated QuestionCreateRequest request) {
        Long teacherId = UserContextHolder.getUserId();
        return questionService.createQuestion(teacherId, bankId, request);
    }

    @PutMapping("/{questionId}")
    @Operation(summary = "更新题目")
    public QuestionVO updateQuestion(@PathVariable Long questionId,
                                     @RequestBody @Validated QuestionUpdateRequest request) {
        Long teacherId = UserContextHolder.getUserId();
        return questionService.updateQuestion(teacherId, questionId, request);
    }

    @DeleteMapping("/{questionId}")
    @Operation(summary = "删除题目")
    public void deleteQuestion(@PathVariable Long questionId) {
        Long teacherId = UserContextHolder.getUserId();
        questionService.deleteQuestion(teacherId, questionId);
    }

    @GetMapping("/{questionId}")
    @Operation(summary = "查看题目详情")
    public QuestionVO getQuestion(@PathVariable Long questionId) {
        return questionService.getQuestion(questionId);
    }

    @GetMapping
    @Operation(summary = "分页查询题目", description = "支持按题库、题型、难度、关键词过滤")
    public Page<QuestionVO> listQuestions(@Validated QuestionQueryRequest request) {
        return questionService.listQuestions(request);
    }
}
