package com.gpnu.learning.controller;

import com.gpnu.auth.enums.Role;
import com.gpnu.auth.resource.annotation.RequireRole;
import com.gpnu.auth.resource.context.UserContextHolder;
import com.gpnu.learning.model.vo.GraphOverviewVO;
import com.gpnu.learning.service.GraphOverviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 学生端课程图谱（F2）：知识点树 + 个人掌握度。
 */
@RestController
@RequestMapping("/student/graph")
@Tag(name = "学生端课程图谱", description = "知识点与掌握度叠加展示")
public class StudentGraphController {

    @Resource
    private GraphOverviewService graphOverviewService;

    @GetMapping("/overview")
    @RequireRole(Role.STUDENT)
    @Operation(summary = "课程图谱概览", description = "合并 Knowledge 知识点树与 lp_mastery 掌握度")
    public GraphOverviewVO getOverview(@RequestParam Long classId,
                                       @RequestParam Long courseId) {
        Long studentId = UserContextHolder.getUserId();
        return graphOverviewService.getStudentGraphOverview(studentId, classId, courseId);
    }
}
