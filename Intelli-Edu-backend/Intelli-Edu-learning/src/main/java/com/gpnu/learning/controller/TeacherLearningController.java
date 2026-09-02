package com.gpnu.learning.controller;

import com.gpnu.auth.enums.Role;
import com.gpnu.auth.resource.annotation.RequireRole;
import com.gpnu.auth.resource.context.UserContextHolder;
import com.gpnu.learning.model.vo.*;
import com.gpnu.learning.service.ITeacherLearningService;
import com.gpnu.learning.service.recommend.RecommendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 教师端学情统计（F4）：与学生端共用 lp_* 表，按班级聚合查询。
 */
@RestController
@RequestMapping("/teacher")
@Tag(name = "教师端学情", description = "班级掌握度、高频错题、错题分布")
public class TeacherLearningController {

    @Resource
    private ITeacherLearningService teacherLearningService;

    @Resource
    private RecommendService recommendService;

    @GetMapping("/mastery/class-overview")
    @RequireRole(Role.TEACHER)
    @Operation(summary = "班级掌握度概览")
    public List<ClassMasteryPointVO> classMasteryOverview(@RequestParam Long classId) {
        return teacherLearningService.getClassMasteryOverview(UserContextHolder.getUserId(), classId);
    }

    @GetMapping("/mastery/student")
    @RequireRole(Role.TEACHER)
    @Operation(summary = "单学生掌握度详情")
    public List<MasteryOverviewVO> studentMastery(@RequestParam Long classId,
                                                @RequestParam Long studentId) {
        return teacherLearningService.getStudentMastery(UserContextHolder.getUserId(), classId, studentId);
    }

    @GetMapping("/wrongs/frequent")
    @RequireRole(Role.TEACHER)
    @Operation(summary = "班级高频错题")
    public List<FrequentWrongQuestionVO> frequentWrongs(@RequestParam Long classId,
                                                        @RequestParam(required = false) Integer limit) {
        return teacherLearningService.getFrequentWrongs(UserContextHolder.getUserId(), classId, limit);
    }

    @GetMapping("/wrongs/point-distribution")
    @RequireRole(Role.TEACHER)
    @Operation(summary = "班级错题知识点分布")
    public WrongStatsVO wrongPointDistribution(@RequestParam Long classId) {
        return teacherLearningService.getClassWrongPointDistribution(UserContextHolder.getUserId(), classId);
    }

    @GetMapping("/wrongs/question-detail")
    @RequireRole(Role.TEACHER)
    @Operation(summary = "单题错误详情")
    public WrongQuestionDetailVO questionDetail(@RequestParam Long classId,
                                                @RequestParam Long questionId) {
        return teacherLearningService.getQuestionWrongDetail(
                UserContextHolder.getUserId(), classId, questionId);
    }

    @GetMapping("/recommend")
    @RequireRole(Role.TEACHER)
    @Operation(summary = "教学干预推荐", description = "scene 固定为 TEACHER_INTERVENTION")
    public RecommendResultVO teacherRecommend(@RequestParam Long classId,
                                              @RequestParam(required = false) Integer limit) {
        return recommendService.recommendForTeacher(UserContextHolder.getUserId(), classId, limit);
    }
}
