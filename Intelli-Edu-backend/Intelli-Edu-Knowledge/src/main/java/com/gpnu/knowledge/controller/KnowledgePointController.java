package com.gpnu.knowledge.controller;

import com.gpnu.auth.resource.context.UserContextHolder;
import com.gpnu.knowledge.model.dto.PointCreateRequest;
import com.gpnu.knowledge.model.dto.PointUpdateRequest;
import com.gpnu.knowledge.model.dto.SectionBindRequest;
import com.gpnu.knowledge.model.dto.QuestionBindRequest;
import com.gpnu.knowledge.model.vo.KnowledgePointVO;
import com.gpnu.knowledge.model.vo.KnowledgeTreeVO;
import com.gpnu.knowledge.service.IKnowledgePointService;
import com.gpnu.knowledge.service.IKnowledgeSectionService;
import com.gpnu.knowledge.service.IQuestionKnowledgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "知识点管理", description = "知识点CRUD、树形查询、章节/题目关联")
public class KnowledgePointController {

    @Resource
    private IKnowledgePointService knowledgePointService;

    @Resource
    private IKnowledgeSectionService knowledgeSectionService;

    @Resource
    private IQuestionKnowledgeService questionKnowledgeService;

    // ==================== 知识点 CRUD ====================

    @PostMapping("/points")
    @Operation(summary = "创建知识点")
    public KnowledgePointVO createPoint(@RequestBody @Validated PointCreateRequest request) {
        Long teacherId = UserContextHolder.getUserId();
        return knowledgePointService.createPoint(teacherId, request);
    }

    @PutMapping("/points/{pointId}")
    @Operation(summary = "更新知识点")
    public KnowledgePointVO updatePoint(@PathVariable Long pointId,
                                        @RequestBody @Validated PointUpdateRequest request) {
        Long teacherId = UserContextHolder.getUserId();
        return knowledgePointService.updatePoint(teacherId, pointId, request);
    }

    @DeleteMapping("/points/{pointId}")
    @Operation(summary = "删除知识点")
    public void deletePoint(@PathVariable Long pointId) {
        Long teacherId = UserContextHolder.getUserId();
        knowledgePointService.deletePoint(teacherId, pointId);
    }

    @GetMapping("/points/tree")
    @Operation(summary = "查询知识点树", description = "返回指定课程的两级知识点树形结构")
    public List<KnowledgeTreeVO> getPointTree(@RequestParam Long courseId) {
        return knowledgePointService.getPointTree(courseId);
    }

    // ==================== 章节关联 ====================

    @PostMapping("/points/{pointId}/sections")
    @Operation(summary = "批量绑定章节到知识点")
    public void bindSections(@PathVariable Long pointId,
                             @RequestBody @Validated SectionBindRequest request) {
        Long teacherId = UserContextHolder.getUserId();
        knowledgeSectionService.bindSections(teacherId, pointId, request.getSectionIds());
    }

    @DeleteMapping("/points/{pointId}/sections/{sectionId}")
    @Operation(summary = "解绑章节")
    public void unbindSection(@PathVariable Long pointId, @PathVariable Long sectionId) {
        Long teacherId = UserContextHolder.getUserId();
        knowledgeSectionService.unbindSection(teacherId, pointId, sectionId);
    }

    @GetMapping("/points/{pointId}/sections")
    @Operation(summary = "查看知识点关联的章节ID列表")
    public List<Long> getSectionIdsByPoint(@PathVariable Long pointId) {
        return knowledgeSectionService.getSectionIdsByPoint(pointId);
    }

    // ==================== 题目关联 ====================

    @PostMapping("/points/{pointId}/questions")
    @Operation(summary = "批量绑定题目到知识点")
    public void bindQuestions(@PathVariable Long pointId,
                              @RequestBody @Validated QuestionBindRequest request) {
        Long teacherId = UserContextHolder.getUserId();
        questionKnowledgeService.bindQuestions(teacherId, pointId, request.getQuestionIds());
    }

    @DeleteMapping("/points/{pointId}/questions/{questionId}")
    @Operation(summary = "解绑题目")
    public void unbindQuestion(@PathVariable Long pointId, @PathVariable Long questionId) {
        Long teacherId = UserContextHolder.getUserId();
        questionKnowledgeService.unbindQuestion(teacherId, pointId, questionId);
    }

    @GetMapping("/points/{pointId}/questions")
    @Operation(summary = "查看知识点关联的题目ID列表")
    public List<Long> getQuestionIdsByPoint(@PathVariable Long pointId) {
        return knowledgePointService.getQuestionIdsByPoint(pointId);
    }

    // ==================== 反向查询 ====================

    @GetMapping("/sections/{sectionId}/points")
    @Operation(summary = "查看某节关联的所有知识点")
    public List<KnowledgePointVO> getPointsBySection(@PathVariable Long sectionId) {
        return knowledgeSectionService.getPointsBySection(sectionId);
    }

    @GetMapping("/questions/{questionId}/points")
    @Operation(summary = "查看某题关联的所有知识点")
    public List<KnowledgePointVO> getPointsByQuestion(@PathVariable Long questionId) {
        return questionKnowledgeService.getPointsByQuestion(questionId);
    }
}
