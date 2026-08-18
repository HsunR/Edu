package com.gpnu.clazz.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gpnu.auth.resource.context.UserContextHolder;
import com.gpnu.clazz.model.dto.ClassCreateRequest;
import com.gpnu.clazz.model.dto.ClassUpdateRequest;
import com.gpnu.clazz.model.dto.JoinClassRequest;
import com.gpnu.clazz.model.vo.ClassMemberVO;
import com.gpnu.clazz.model.vo.ClassVO;
import com.gpnu.clazz.service.IClassService;
import com.gpnu.common.common.PageRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/classes")
@Tag(name = "班级管理", description = "班级的创建、加入、退出、成员管理等功能")
public class ClassController {

    @Resource
    private IClassService classService;

    // ==================== 教师端 ====================

    @PostMapping
    @Operation(summary = "创建班级")
    public ClassVO createClass(@RequestBody @Validated ClassCreateRequest request) {
        Long teacherId = UserContextHolder.getUserId();
        return classService.createClass(teacherId, request);
    }

    @PutMapping("/{classId}")
    @Operation(summary = "更新班级信息")
    public ClassVO updateClass(@PathVariable Long classId,
                               @RequestBody @Validated ClassUpdateRequest request) {
        Long teacherId = UserContextHolder.getUserId();
        return classService.updateClass(teacherId, classId, request);
    }

    @GetMapping("/{classId}/members")
    @Operation(summary = "班级成员列表")
    public Page<ClassMemberVO> listMembers(@PathVariable Long classId,
                                           @Validated PageRequest pageRequest) {
        Long teacherId = UserContextHolder.getUserId();
        return classService.listMembers(teacherId, classId, pageRequest);
    }

    @DeleteMapping("/{classId}/members/{memberId}")
    @Operation(summary = "移除学生")
    public void removeMember(@PathVariable Long classId,
                             @PathVariable Long memberId) {
        Long teacherId = UserContextHolder.getUserId();
        classService.removeMember(teacherId, classId, memberId);
    }

    // ==================== 学生端 ====================

    @PostMapping("/join")
    @Operation(summary = "通过邀请码加入班级")
    public ClassVO joinClass(@RequestBody @Validated JoinClassRequest request) {
        Long studentId = UserContextHolder.getUserId();
        return classService.joinClass(studentId, request);
    }

    @PostMapping("/{classId}/quit")
    @Operation(summary = "退出班级")
    public void quitClass(@PathVariable Long classId) {
        Long studentId = UserContextHolder.getUserId();
        classService.quitClass(studentId, classId);
    }

    @GetMapping("/my")
    @Operation(summary = "我加入的班级列表")
    public List<ClassVO> listMyClasses() {
        Long studentId = UserContextHolder.getUserId();
        return classService.listMyClasses(studentId);
    }
}

