package com.gpnu.learning.controller;

import com.gpnu.auth.enums.Role;
import com.gpnu.auth.resource.annotation.RequireRole;
import com.gpnu.auth.resource.context.UserContextHolder;
import com.gpnu.learning.model.dto.RecommendQueryRequest;
import com.gpnu.learning.model.enums.RecommendScene;
import com.gpnu.learning.model.vo.RecommendResultVO;
import com.gpnu.learning.service.recommend.RecommendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 学生端个性化推荐（F5）：WMCLR 算法，不依赖 LLM。
 */
@RestController
@RequestMapping("/student/recommend")
@Tag(name = "学生端个性化推荐", description = "基于掌握度与错题的规则推荐")
public class StudentRecommendController {

    @Resource
    private RecommendService recommendService;

    @GetMapping
    @RequireRole(Role.STUDENT)
    @Operation(summary = "获取个性化推荐", description = "scene: REVIEW_WEAK / REVIEW_WRONG / DAILY_PLAN")
    public RecommendResultVO recommend(@Validated RecommendQueryRequest request) {
        Long studentId = UserContextHolder.getUserId();
        RecommendScene scene = RecommendScene.fromCode(request.getScene());
        return recommendService.recommendForStudent(
                studentId, request.getClassId(), request.getCourseId(), scene, request.getLimit());
    }
}
