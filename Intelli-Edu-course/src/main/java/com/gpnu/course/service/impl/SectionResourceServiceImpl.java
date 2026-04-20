package com.gpnu.course.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpnu.course.model.entity.SectionResource;
import com.gpnu.course.service.ISectionResourceService;
import com.gpnu.course.mapper.SectionResourceMapper;
import org.springframework.stereotype.Service;

/**
* @author Chenxingdong
* @description 针对表【co_section_resource(节-资源关联表)】的数据库操作Service实现
* @createDate 2026-04-19 22:19:22
*/
@Service
public class SectionResourceServiceImpl extends ServiceImpl<SectionResourceMapper, SectionResource>
    implements ISectionResourceService {

}




