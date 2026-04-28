package com.gpnu.exam.paper.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gpnu.auth.resource.context.UserContextHolder;
import com.gpnu.exam.paper.model.dto.*;
import com.gpnu.exam.paper.model.vo.PaperDetailVO;
import com.gpnu.exam.paper.model.vo.PaperVO;
import com.gpnu.exam.paper.service.IPaperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/papers")
@Tag(name = "试卷管理", description = "试卷的创建、组卷、发布、查询")
public class PaperController {

    @Resource
    private IPaperService paperService;

    @PostMapping
    @Operation(summary = "创建试卷")
    public PaperVO createPaper(@RequestBody @Validated PaperCreateRequest request) {
        Long teacherId = UserContextHolder.getUserId();
        return paperService.createPaper(teacherId, request);
    }

    @PutMapping("/{paperId}")
    @Operation(summary = "更新试卷基本信息", description = "仅草稿状态可修改")
    public PaperVO updatePaper(@PathVariable Long paperId,
                               @RequestBody @Validated PaperUpdateRequest request) {
        Long teacherId = UserContextHolder.getUserId();
        return paperService.updatePaper(teacherId, paperId, request);
    }

    @DeleteMapping("/{paperId}")
    @Operation(summary = "删除试卷", description = "仅草稿状态可删除")
    public void deletePaper(@PathVariable Long paperId) {
        Long teacherId = UserContextHolder.getUserId();
        paperService.deletePaper(teacherId, paperId);
    }

    @PostMapping("/{paperId}/questions")
    @Operation(summary = "向试卷添加题目")
    public void addQuestions(@PathVariable Long paperId,
                             @RequestBody @Validated PaperQuestionAddRequest request) {
        Long teacherId = UserContextHolder.getUserId();
        paperService.addQuestions(teacherId, paperId, request);
    }

    @DeleteMapping("/{paperId}/questions/{questionId}")
    @Operation(summary = "从试卷移除题目")
    public void removeQuestion(@PathVariable Long paperId, @PathVariable Long questionId) {
        Long teacherId = UserContextHolder.getUserId();
        paperService.removeQuestion(teacherId, paperId, questionId);
    }

    @PutMapping("/{paperId}/questions/order")
    @Operation(summary = "调整试卷题目排序")
    public void reorderQuestions(@PathVariable Long paperId,
                                 @RequestBody @Validated PaperQuestionOrderRequest request) {
        Long teacherId = UserContextHolder.getUserId();
        paperService.reorderQuestions(teacherId, paperId, request);
    }

    @PutMapping("/{paperId}/publish")
    @Operation(summary = "发布试卷", description = "冻结题目快照，状态变为已发布")
    public void publishPaper(@PathVariable Long paperId) {
        Long teacherId = UserContextHolder.getUserId();
        paperService.publishPaper(teacherId, paperId);
    }

    @GetMapping
    @Operation(summary = "查询试卷列表")
    public Page<PaperVO> listPapers(@Validated PaperQueryRequest request) {
        Long teacherId = UserContextHolder.getUserId();
        return paperService.listPapers(teacherId, request);
    }

    @GetMapping("/{paperId}")
    @Operation(summary = "试卷详情", description = "含完整题目列表")
    public PaperDetailVO getPaperDetail(@PathVariable Long paperId) {
        return paperService.getPaperDetail(paperId);
    }
}
