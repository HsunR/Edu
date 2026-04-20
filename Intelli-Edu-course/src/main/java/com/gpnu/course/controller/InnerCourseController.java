package com.gpnu.course.controller;

import com.gpnu.api.dto.course.CourseSimpleDTO;
import com.gpnu.clazz.service.IClassService;
import com.gpnu.course.service.ICourseService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inner")
@Tag(name = "内部接口", description = "Feign内部调用，不走网关")
@Hidden
public class InnerCourseController {

    @Resource
    private ICourseService courseService;

    @Resource
    private IClassService classService;

    @GetMapping("/courses/{courseId}")
    @Operation(summary = "获取课程简要信息")
    public CourseSimpleDTO getCourseSimple(@PathVariable Long courseId) {
        return courseService.getCourseSimple(courseId);
    }

    @PostMapping("/courses/batch")
    @Operation(summary = "批量获取课程简要信息")
    public List<CourseSimpleDTO> getCourseBatch(@RequestBody List<Long> courseIds) {
        return courseService.getCourseBatch(courseIds);
    }

    @GetMapping("/classes/{classId}/check-member")
    @Operation(summary = "校验学生是否在某班级中")
    public Boolean checkMember(@PathVariable Long classId,
                               @RequestParam Long studentId) {
        return classService.checkMember(classId, studentId);
    }
}
