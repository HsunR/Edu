package com.gpnu.clazz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpnu.clazz.model.entity.ClassMember;
import com.gpnu.clazz.service.IClassMemberService;
import com.gpnu.clazz.mapper.ClassMemberMapper;
import org.springframework.stereotype.Service;

/**
* @author Chenxingdong
* @description 针对表【co_class_member(班级成员表)】的数据库操作Service实现
* @createDate 2026-04-19 22:19:11
*/
@Service
public class ClassMemberServiceImpl extends ServiceImpl<ClassMemberMapper, ClassMember>
    implements IClassMemberService {

}




