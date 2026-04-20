package com.gpnu.course.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gpnu.auth.resource.context.UserContextHolder;
import com.gpnu.clazz.model.vo.ClassVO;
import com.gpnu.clazz.service.IClassService;
import com.gpnu.course.model.dto.course.CourseCreateRequest;
import com.gpnu.course.model.dto.course.CourseQueryRequest;
import com.gpnu.course.model.dto.course.CourseUpdateRequest;
import com.gpnu.course.model.vo.course.CourseDetailVO;
import com.gpnu.course.model.vo.course.CourseVO;
import com.gpnu.course.service.ICourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
@Tag(name = "课程管理", description = "课程的创建、编辑、发布、查询等功能")
public class CourseController {

    @Resource
    private ICourseService courseService;

    @Resource
    private IClassService classService;

    // ==================== 教师端 ====================

    @PostMapping
    @Operation(summary = "创建课程")
    public CourseVO createCourse(@RequestBody @Validated CourseCreateRequest request) {
        Long teacherId = UserContextHolder.getUserId();
        return courseService.createCourse(teacherId, request);
    }

    @PutMapping("/{courseId}")
    @Operation(summary = "更新课程基本信息")
    public CourseVO updateCourse(@PathVariable Long courseId,
                                 @RequestBody @Validated CourseUpdateRequest request) {
        Long teacherId = UserContextHolder.getUserId();
        return courseService.updateCourse(teacherId, courseId, request);
    }

    @PutMapping("/{courseId}/publish")
    @Operation(summary = "发布课程")
    public void publishCourse(@PathVariable Long courseId) {
        Long teacherId = UserContextHolder.getUserId();
        courseService.publishCourse(teacherId, courseId);
    }

    @PutMapping("/{courseId}/archive")
    @Operation(summary = "归档课程")
    public void archiveCourse(@PathVariable Long courseId) {
        Long teacherId = UserContextHolder.getUserId();
        courseService.archiveCourse(teacherId, courseId);
    }

    @DeleteMapping("/{courseId}")
    @Operation(summary = "删除课程", description = "仅草稿状态可删")
    public void deleteCourse(@PathVariable Long courseId) {
        Long teacherId = UserContextHolder.getUserId();
        courseService.deleteCourse(teacherId, courseId);
    }

    @GetMapping("/teaching")
    @Operation(summary = "我教的课程列表")
    public Page<CourseVO> listTeachingCourses(@Validated CourseQueryRequest request) {
        Long teacherId = UserContextHolder.getUserId();
        return courseService.listTeachingCourses(teacherId, request);
    }

    @GetMapping("/{courseId}/classes")
    @Operation(summary = "查看课程下的所有班级")
    public List<ClassVO> listCourseClasses(@PathVariable Long courseId) {
        Long teacherId = UserContextHolder.getUserId();
        return classService.listCourseClasses(teacherId, courseId);
    }

    // ==================== 公共端 ====================

    @GetMapping
    @Operation(summary = "浏览公开课程", description = "分页+分类过滤+关键词搜索")
    public Page<CourseVO> listPublicCourses(@Validated CourseQueryRequest request) {
        return courseService.listPublicCourses(request);
    }

    @GetMapping("/{courseId}")
    @Operation(summary = "课程详情", description = "含完整章节目录树")
    public CourseDetailVO getCourseDetail(@PathVariable Long courseId) {
        Long currentUserId = UserContextHolder.getUserId();
        return courseService.getCourseDetail(courseId, currentUserId);
    }
}
