package com.gpnu.course.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpnu.course.model.entity.Category;
import com.gpnu.course.model.vo.category.CategoryVO;
import com.gpnu.course.service.ICategoryService;
import com.gpnu.course.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author Chenxingdong
* @description 针对表【co_category(课程分类表)】的数据库操作Service实现
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements ICategoryService {

    @Override
    public List<CategoryVO> getCategoryTree() {
        List<Category> all = list(
                new LambdaQueryWrapper<Category>()
                        .orderByAsc(Category::getOrderIndex));

        List<CategoryVO> voList = all.stream().map(c -> {
            CategoryVO vo = new CategoryVO();
            BeanUtil.copyProperties(c, vo);
            return vo;
        }).toList();

        // 按parentId分组构建树
        Map<Long, List<CategoryVO>> childrenMap = voList.stream()
                .filter(v -> v.getParentId() != null)
                .collect(Collectors.groupingBy(CategoryVO::getParentId));

        // 挂载children
        voList.forEach(v -> v.setChildren(childrenMap.getOrDefault(v.getCategoryId(), List.of())));

        // 返回顶级节点
        return voList.stream()
                .filter(v -> v.getParentId() == null)
                .toList();
    }

}




