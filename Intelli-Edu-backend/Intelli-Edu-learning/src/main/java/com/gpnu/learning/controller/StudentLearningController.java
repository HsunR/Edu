package com.gpnu.learning.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gpnu.auth.enums.Role;
import com.gpnu.auth.resource.annotation.RequireRole;
import com.gpnu.auth.resource.context.UserContextHolder;
import com.gpnu.learning.model.dto.WrongRecordQueryRequest;
import com.gpnu.learning.model.dto.WrongStatsQueryRequest;
import com.gpnu.learning.model.vo.MasteryOverviewVO;
import com.gpnu.learning.model.vo.WrongRecordVO;
import com.gpnu.learning.model.vo.WrongStatsVO;
import com.gpnu.learning.service.IStudentLearningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student")
@Tag(name = "学生端学情", description = "掌握度、错题本、错题统计")
public class StudentLearningController {

    @Resource
    private IStudentLearningService studentLearningService;

    @GetMapping("/mastery/overview")
    @RequireRole(Role.STUDENT)   // 批次1 修复：原为注释，导致未鉴权端点 + 可能拿到 null studentId
    @Operation(summary = "查看我在某班的课程掌握度概览")
    public List<MasteryOverviewVO> getMasteryOverview(@RequestParam Long classId) {
        Long studentId = UserContextHolder.getUserId();
        return studentLearningService.getMasteryOverview(studentId, classId);
    }

    @GetMapping("/mastery/weak-points")
    @RequireRole(Role.STUDENT)
    @Operation(summary = "查看我的薄弱知识点")
    public List<MasteryOverviewVO> getWeakPoints(@RequestParam Long classId) {
        Long studentId = UserContextHolder.getUserId();
        return studentLearningService.getWeakPoints(studentId, classId);
    }

    @GetMapping("/wrongs")
    @RequireRole(Role.STUDENT)
    @Operation(summary = "我的错题本（分页）")
    public Page<WrongRecordVO> pageWrongRecords(@Validated WrongRecordQueryRequest request) {
        Long studentId = UserContextHolder.getUserId();
        return studentLearningService.pageWrongRecords(studentId, request);
    }

    @GetMapping("/wrongs/stats")
    @RequireRole(Role.STUDENT)
    @Operation(summary = "查看课程/班级错题统计")
    public WrongStatsVO getWrongStats(@Validated WrongStatsQueryRequest request) {
        Long studentId = UserContextHolder.getUserId();
        return studentLearningService.getWrongStats(studentId, request);
    }

    @PutMapping("/wrongs/{wrongId}/resolve")
    @RequireRole(Role.STUDENT)
    @Operation(summary = "标记错题已解决")
    public void resolveWrongRecord(@PathVariable Long wrongId) {
        Long studentId = UserContextHolder.getUserId();
        studentLearningService.resolveWrongRecord(studentId, wrongId);
    }
}