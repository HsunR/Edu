package com.gpnu.course.controller;


import com.gpnu.auth.resource.context.UserContextHolder;
import com.gpnu.course.model.dto.chapter.ChapterCreateRequest;
import com.gpnu.course.model.dto.chapter.ChapterUpdateRequest;
import com.gpnu.course.model.dto.chapter.OrderItem;
import com.gpnu.course.model.vo.chapter.ChapterVO;
import com.gpnu.course.service.IChapterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "章管理", description = "课程章的增删改排序")
public class ChapterController {

    @Resource
    private IChapterService chapterService;

    @PostMapping("/courses/{courseId}/chapters")
    @Operation(summary = "添加章")
    public ChapterVO addChapter(@PathVariable Long courseId,
                                @RequestBody @Validated ChapterCreateRequest request) {
        Long teacherId = UserContextHolder.getUserId();
        return chapterService.addChapter(teacherId, courseId, request);
    }

    @PutMapping("/chapters/{chapterId}")
    @Operation(summary = "更新章标题")
    public ChapterVO updateChapter(@PathVariable Long chapterId,
                                   @RequestBody @Validated ChapterUpdateRequest request) {
        Long teacherId = UserContextHolder.getUserId();
        return chapterService.updateChapter(teacherId, chapterId, request);
    }

    @DeleteMapping("/chapters/{chapterId}")
    @Operation(summary = "删除章", description = "级联删除其下所有节和节-资源关联")
    public void deleteChapter(@PathVariable Long chapterId) {
        Long teacherId = UserContextHolder.getUserId();
        chapterService.deleteChapter(teacherId, chapterId);
    }

    @PutMapping("/courses/{courseId}/chapters/order")
    @Operation(summary = "批量调整章排序")
    public void reorderChapters(@PathVariable Long courseId,
                                @RequestBody @Validated List<OrderItem> orderItems) {
        Long teacherId = UserContextHolder.getUserId();
        chapterService.reorderChapters(teacherId, courseId, orderItems);
    }
}
