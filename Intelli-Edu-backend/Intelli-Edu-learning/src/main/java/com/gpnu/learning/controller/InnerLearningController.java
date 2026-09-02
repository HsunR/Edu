package com.gpnu.learning.controller;

import com.gpnu.api.dto.learning.LearningMasteryDTO;
import com.gpnu.api.dto.learning.LearningPageDTO;
import com.gpnu.api.dto.learning.LearningProfileSummaryDTO;
import com.gpnu.api.dto.learning.LearningWrongQuestionDetailDTO;
import com.gpnu.api.dto.learning.LearningWrongRecordDTO;
import com.gpnu.learning.service.IInnerLearningService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 学情对内接口：供 AI 等微服务 Feign 调用，不走网关鉴权角色注解。
 */
@RestController
@RequestMapping("/inner")
@Tag(name = "学情内部接口", description = "Feign 调用")
@Hidden
public class InnerLearningController {

    @Resource
    private IInnerLearningService innerLearningService;

    @GetMapping("/profile/summary")
    @Operation(summary = "学情摘要（AI RAG）")
    public LearningProfileSummaryDTO profileSummary(@RequestParam String studentId,
                                                    @RequestParam String classId) {
        return innerLearningService.buildProfileSummary(studentId, classId);
    }

    @GetMapping("/mastery/list")
    @Operation(summary = "学生掌握度列表")
    public List<LearningMasteryDTO> masteryList(@RequestParam String studentId,
                                                @RequestParam String classId,
                                                @RequestParam(required = false) String courseId) {
        return innerLearningService.listMastery(studentId, classId, courseId);
    }

    @GetMapping("/mastery/weak-points")
    @Operation(summary = "学生薄弱知识点")
    public List<LearningMasteryDTO> weakPoints(@RequestParam String studentId,
                                               @RequestParam String classId,
                                               @RequestParam(required = false) String courseId,
                                               @RequestParam(required = false) Integer threshold) {
        return innerLearningService.listWeakPoints(studentId, classId, courseId, threshold);
    }

    @GetMapping("/wrongs")
    @Operation(summary = "学生错题列表")
    public LearningPageDTO<LearningWrongRecordDTO> pageWrongs(@RequestParam String studentId,
                                                              @RequestParam(required = false) String classId,
                                                              @RequestParam(required = false) String courseId,
                                                              @RequestParam(required = false) Integer questionType,
                                                              @RequestParam(required = false) Integer isResolved,
                                                              @RequestParam(required = false) Integer current,
                                                              @RequestParam(required = false) Integer pageSize) {
        return innerLearningService.pageWrongRecords(
                studentId, classId, courseId, questionType, isResolved, current, pageSize);
    }

    @GetMapping("/wrongs/{wrongId}")
    @Operation(summary = "学生单条错题详情")
    public LearningWrongRecordDTO wrongRecord(@PathVariable String wrongId,
                                              @RequestParam String studentId) {
        return innerLearningService.getWrongRecord(studentId, wrongId);
    }

    @GetMapping("/wrongs/questions/{questionId}/students")
    @Operation(summary = "班级某题答错学生明细")
    public LearningWrongQuestionDetailDTO questionWrongStudents(@PathVariable String questionId,
                                                               @RequestParam String classId) {
        return innerLearningService.getQuestionWrongDetail(classId, questionId);
    }
}
