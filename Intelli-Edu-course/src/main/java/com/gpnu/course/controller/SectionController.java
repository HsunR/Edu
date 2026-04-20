package com.gpnu.course.controller;

import cn.hutool.core.util.StrUtil;
import com.gpnu.auth.resource.context.UserContextHolder;
import com.gpnu.course.model.dto.chapter.OrderItem;
import com.gpnu.course.model.dto.section.SectionCreateRequest;
import com.gpnu.course.model.dto.section.SectionResourceAddRequest;
import com.gpnu.course.model.dto.section.SectionUpdateRequest;
import com.gpnu.course.model.vo.section.SectionDetailVO;
import com.gpnu.course.model.vo.section.SectionResourceVO;
import com.gpnu.course.model.vo.section.SectionVO;
import com.gpnu.course.service.ISectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "节管理", description = "课程节的增删改排序及节内资源管理")
public class SectionController {

    @Resource
    private ISectionService sectionService;

    // ==================== 节管理 ====================

    @PostMapping("/chapters/{chapterId}/sections")
    @Operation(summary = "添加节")
    public SectionVO addSection(@PathVariable Long chapterId,
                                @RequestBody @Validated SectionCreateRequest request) {
        Long teacherId = UserContextHolder.getUserId();
        return sectionService.addSection(teacherId, chapterId, request);
    }

    @PutMapping("/sections/{sectionId}")
    @Operation(summary = "更新节")
    public SectionVO updateSection(@PathVariable Long sectionId,
                                   @RequestBody @Validated SectionUpdateRequest request) {
        Long teacherId = UserContextHolder.getUserId();
        return sectionService.updateSection(teacherId, sectionId, request);
    }

    @DeleteMapping("/sections/{sectionId}")
    @Operation(summary = "删除节", description = "级联删除节-资源关联")
    public void deleteSection(@PathVariable Long sectionId) {
        Long teacherId = UserContextHolder.getUserId();
        sectionService.deleteSection(teacherId, sectionId);
    }

    @PutMapping("/chapters/{chapterId}/sections/order")
    @Operation(summary = "批量调整节排序")
    public void reorderSections(@PathVariable Long chapterId,
                                @RequestBody @Validated List<OrderItem> orderItems) {
        Long teacherId = UserContextHolder.getUserId();
        sectionService.reorderSections(teacherId, chapterId, orderItems);
    }

    // ==================== 节内资源管理 ====================

    @PostMapping("/sections/{sectionId}/resources")
    @Operation(summary = "添加资源到节")
    public SectionResourceVO addResource(@PathVariable Long sectionId,
                                         @RequestBody @Validated SectionResourceAddRequest request) {
        Long teacherId = UserContextHolder.getUserId();
        return sectionService.addResource(teacherId, sectionId, request);
    }

    @DeleteMapping("/sections/{sectionId}/resources/{id}")
    @Operation(summary = "移除节内资源")
    public void removeResource(@PathVariable Long sectionId,
                               @PathVariable Long id) {
        Long teacherId = UserContextHolder.getUserId();
        sectionService.removeResource(teacherId, sectionId, id);
    }

    @PutMapping("/sections/{sectionId}/resources/order")
    @Operation(summary = "调整节内资源顺序")
    public void reorderResources(@PathVariable Long sectionId,
                                 @RequestBody @Validated List<OrderItem> orderItems) {
        Long teacherId = UserContextHolder.getUserId();
        sectionService.reorderResources(teacherId, sectionId, orderItems);
    }

    // ==================== 学生访问 ====================

    @GetMapping("/sections/{sectionId}/detail")
    @Operation(summary = "获取节详情", description = "含资源访问地址，需权限校验")
    public SectionDetailVO getSectionDetail(@PathVariable Long sectionId) {
        Long currentUserId = UserContextHolder.getUserId();
        Integer userType = Integer.valueOf(UserContextHolder.getUserType());
        return sectionService.getSectionDetail(sectionId, currentUserId, userType);
    }
}
