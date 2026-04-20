package com.gpnu.course.service;

import com.gpnu.course.model.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gpnu.course.model.vo.category.CategoryVO;

import java.util.List;

/**
* @author Chenxingdong
* @description 针对表【co_category(课程分类表)】的数据库操作Service
* @createDate 2026-04-19 22:13:45
*/
public interface ICategoryService extends IService<Category> {
    List<CategoryVO> getCategoryTree();
}
