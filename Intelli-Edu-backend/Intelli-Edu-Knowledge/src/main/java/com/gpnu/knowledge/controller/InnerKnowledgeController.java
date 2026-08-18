package com.gpnu.knowledge.controller;

import com.gpnu.api.dto.knowledge.KnowledgeTreeDTO;
import com.gpnu.api.dto.knowledge.PointSimpleDTO;
import com.gpnu.knowledge.service.IKnowledgePointService;
import com.gpnu.knowledge.service.IKnowledgeSectionService;
import com.gpnu.knowledge.service.IQuestionKnowledgeService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inner")
@Tag(name = "内部接口", description = "Feign内部调用，不走网关")
@Hidden
public class InnerKnowledgeController {

    @Resource
    private IKnowledgePointService knowledgePointService;

    @Resource
    private IKnowledgeSectionService knowledgeSectionService;

    @Resource
    private IQuestionKnowledgeService questionKnowledgeService;

    @GetMapping("/points/tree")
    @Operation(summary = "获取完整知识点树")
    public List<KnowledgeTreeDTO> getPointTree(@RequestParam Long courseId) {
        return knowledgePointService.getPointTreeDTO(courseId);
    }

    @PostMapping("/points/batch")
    @Operation(summary = "批量查知识点")
    public List<PointSimpleDTO> getPointBatch(@RequestBody List<Long> pointIds) {
        return knowledgePointService.getPointBatchDTO(pointIds);
    }

    @GetMapping("/points/{pointId}/question-ids")
    @Operation(summary = "获取某知识点下所有题目ID")
    public List<Long> getQuestionIdsByPoint(@PathVariable Long pointId) {
        return knowledgePointService.getQuestionIdsByPoint(pointId);
    }

    @PostMapping("/questions/points")
    @Operation(summary = "批量查题目关联的知识点")
    public Map<Long, List<PointSimpleDTO>> getPointsByQuestions(@RequestBody List<Long> questionIds) {
        return knowledgePointService.getPointsByQuestionsDTO(questionIds);
    }

    @DeleteMapping("/sections/{sectionId}/relations")
    @Operation(summary = "章节删除时级联清除关联")
    public void clearSectionRelations(@PathVariable Long sectionId) {
        knowledgeSectionService.clearSectionRelations(sectionId);
    }

    @DeleteMapping("/questions/{questionId}/relations")
    @Operation(summary = "题目删除时级联清除关联")
    public void clearQuestionRelations(@PathVariable Long questionId) {
        questionKnowledgeService.clearQuestionRelations(questionId);
    }
}
