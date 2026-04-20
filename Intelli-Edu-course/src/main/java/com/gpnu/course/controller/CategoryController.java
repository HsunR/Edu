package com.gpnu.course.controller;

import com.gpnu.course.model.vo.category.CategoryVO;
import com.gpnu.course.service.ICategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
@Tag(name = "课程分类", description = "课程分类树查询")
public class CategoryController {

    @Resource
    private ICategoryService categoryService;

    @GetMapping("/")
    @Operation(summary = "获取分类树", description = "全量返回，前端缓存")
    public List<CategoryVO> getCategoryTree() {
        return categoryService.getCategoryTree();
    }
}

