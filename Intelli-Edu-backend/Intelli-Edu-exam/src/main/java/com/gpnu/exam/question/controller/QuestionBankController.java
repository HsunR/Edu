package com.gpnu.exam.question.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gpnu.auth.resource.context.UserContextHolder;
import com.gpnu.exam.question.model.dto.QuestionBankCreateRequest;
import com.gpnu.exam.question.model.dto.QuestionBankQueryRequest;
import com.gpnu.exam.question.model.dto.QuestionBankUpdateRequest;
import com.gpnu.exam.question.model.vo.QuestionBankVO;
import com.gpnu.exam.question.service.IQuestionBankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/question-banks")
@Tag(name = "题库管理", description = "题库的创建、编辑、删除、查询")
public class QuestionBankController {

    @Resource
    private IQuestionBankService questionBankService;

    @PostMapping
    @Operation(summary = "创建题库")
    public QuestionBankVO createBank(@RequestBody @Validated QuestionBankCreateRequest request) {
        Long teacherId = UserContextHolder.getUserId();
        return questionBankService.createBank(teacherId, request);
    }

    @PutMapping("/{bankId}")
    @Operation(summary = "更新题库")
    public QuestionBankVO updateBank(@PathVariable Long bankId,
                                     @RequestBody @Validated QuestionBankUpdateRequest request) {
        Long teacherId = UserContextHolder.getUserId();
        return questionBankService.updateBank(teacherId, bankId, request);
    }

    @DeleteMapping("/{bankId}")
    @Operation(summary = "删除题库", description = "题库中有题目时不可删除")
    public void deleteBank(@PathVariable Long bankId) {
        Long teacherId = UserContextHolder.getUserId();
        questionBankService.deleteBank(teacherId, bankId);
    }

    @GetMapping
    @Operation(summary = "查询题库列表")
    public Page<QuestionBankVO> listBanks(@Validated QuestionBankQueryRequest request) {
        Long teacherId = UserContextHolder.getUserId();
        return questionBankService.listBanks(teacherId, request);
    }
}
