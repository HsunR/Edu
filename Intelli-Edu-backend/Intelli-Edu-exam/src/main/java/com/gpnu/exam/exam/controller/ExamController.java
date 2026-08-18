package com.gpnu.exam.exam.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gpnu.auth.resource.context.UserContextHolder;
import com.gpnu.exam.exam.model.dto.ExamCreateRequest;
import com.gpnu.exam.exam.model.dto.ExamQueryRequest;
import com.gpnu.exam.exam.model.dto.ExamUpdateRequest;
import com.gpnu.exam.exam.model.dto.GradeRequest;
import com.gpnu.exam.exam.model.vo.AnswerSheetDetailVO;
import com.gpnu.exam.exam.model.vo.AnswerSheetVO;
import com.gpnu.exam.exam.model.vo.ExamStatsVO;
import com.gpnu.exam.exam.model.vo.ExamVO;
import com.gpnu.exam.exam.service.IAnswerService;
import com.gpnu.exam.exam.service.IExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exams")
@Tag(name = "考试管理", description = "考试的发布、管理、统计、批阅")
public class ExamController {

    @Resource
    private IExamService examService;

    @Resource
    private IAnswerService answerService;

    // ==================== 考试CRUD ====================

    @PostMapping
    @Operation(summary = "发布考试")
    public ExamVO createExam(@RequestBody @Validated ExamCreateRequest request) {
        Long teacherId = UserContextHolder.getUserId();
        return examService.createExam(teacherId, request);
    }

    @PutMapping("/{examId}")
    @Operation(summary = "更新考试", description = "仅未开始的考试可修改")
    public ExamVO updateExam(@PathVariable Long examId,
                             @RequestBody @Validated ExamUpdateRequest request) {
        Long teacherId = UserContextHolder.getUserId();
        return examService.updateExam(teacherId, examId, request);
    }

    @DeleteMapping("/{examId}")
    @Operation(summary = "删除考试", description = "仅未开始的考试可删除")
    public void deleteExam(@PathVariable Long examId) {
        Long teacherId = UserContextHolder.getUserId();
        examService.deleteExam(teacherId, examId);
    }

    @GetMapping
    @Operation(summary = "查询考试列表", description = "支持按班级、课程、类型、状态过滤")
    public Page<ExamVO> listExams(@Validated ExamQueryRequest request) {
        return examService.listExams(request);
    }

    // ==================== 统计与答卷管理 ====================

    @GetMapping("/{examId}/stats")
    @Operation(summary = "考试统计", description = "已交卷人数、平均分、分数段等")
    public ExamStatsVO getExamStats(@PathVariable Long examId) {
        Long teacherId = UserContextHolder.getUserId();
        return examService.getExamStats(teacherId, examId);
    }

    @GetMapping("/{examId}/sheets")
    @Operation(summary = "查看所有答卷列表")
    public List<AnswerSheetVO> listExamSheets(@PathVariable Long examId) {
        Long teacherId = UserContextHolder.getUserId();
        return examService.listExamSheets(teacherId, examId);
    }

    // ==================== 批阅 ====================

    @GetMapping("/sheets/{sheetId}/detail")
    @Operation(summary = "查看学生答卷详情", description = "教师批阅时查看")
    public AnswerSheetDetailVO getSheetDetail(@PathVariable Long sheetId) {
        return answerService.getSheetDetail(sheetId);
    }

    @PutMapping("/records/{recordId}/grade")
    @Operation(summary = "批阅单道题")
    public void gradeRecord(@PathVariable Long recordId,
                            @RequestBody @Validated GradeRequest request) {
        Long teacherId = UserContextHolder.getUserId();
        answerService.gradeRecord(teacherId, recordId, request);
    }

    @PostMapping("/sheets/{sheetId}/finish-grading")
    @Operation(summary = "完成批阅", description = "汇总主观题得分，更新答卷状态")
    public void finishGrading(@PathVariable Long sheetId) {
        Long teacherId = UserContextHolder.getUserId();
        answerService.finishGrading(teacherId, sheetId);
    }
}
