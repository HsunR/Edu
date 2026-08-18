package com.gpnu.exam.exam.controller;

import com.gpnu.auth.resource.context.UserContextHolder;
import com.gpnu.exam.exam.model.dto.AnswerSaveRequest;
import com.gpnu.exam.exam.model.vo.AnswerSheetDetailVO;
import com.gpnu.exam.exam.model.vo.AnswerSheetVO;
import com.gpnu.exam.exam.service.IAnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/answers")
@Tag(name = "答题模块", description = "学生进入考试、保存答案、交卷、查看结果")
public class AnswerController {

    @Resource
    private IAnswerService answerService;

    @PostMapping("/exams/{examId}/enter")
    @Operation(summary = "进入考试", description = "创建答卷，计算个人截止时间")
    public AnswerSheetVO enterExam(@PathVariable Long examId) {
        Long studentId = UserContextHolder.getUserId();
        return answerService.enterExam(studentId, examId);
    }

    @PutMapping("/sheets/{sheetId}/questions/{questionId}")
    @Operation(summary = "保存单题答案", description = "写入Redis，定时刷入DB")
    public void saveAnswer(@PathVariable Long sheetId,
                           @PathVariable Long questionId,
                           @RequestBody AnswerSaveRequest request) {
        Long studentId = UserContextHolder.getUserId();
        answerService.saveAnswer(studentId, sheetId, questionId, request.getAnswerContent());
    }

    @PostMapping("/sheets/{sheetId}/submit")
    @Operation(summary = "交卷", description = "刷Redis→自动判分→更新答卷")
    public void submitSheet(@PathVariable Long sheetId) {
        Long studentId = UserContextHolder.getUserId();
        answerService.submitSheet(studentId, sheetId);
    }

    @GetMapping("/exams/{examId}/my-sheet")
    @Operation(summary = "查看我的答卷", description = "含答题记录和题目快照")
    public AnswerSheetDetailVO getMySheet(@PathVariable Long examId) {
        Long studentId = UserContextHolder.getUserId();
        return answerService.getMySheet(studentId, examId);
    }
}
