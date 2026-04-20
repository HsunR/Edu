package com.gpnu.clazz.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gpnu.clazz.model.dto.ClassCreateRequest;
import com.gpnu.clazz.model.dto.ClassUpdateRequest;
import com.gpnu.clazz.model.dto.JoinClassRequest;
import com.gpnu.clazz.model.entity.Clazz;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gpnu.clazz.model.vo.ClassMemberVO;
import com.gpnu.clazz.model.vo.ClassVO;
import com.gpnu.common.common.PageRequest;

import java.util.List;

/**
* @author Chenxingdong
* @description 针对表【co_class(班级表（课程开课实例）)】的数据库操作Service
*/
public interface IClassService extends IService<Clazz> {
    ClassVO createClass(Long teacherId, ClassCreateRequest request);

    ClassVO updateClass(Long teacherId, Long classId, ClassUpdateRequest request);

    Page<ClassMemberVO> listMembers(Long teacherId, Long classId, PageRequest pageRequest);

    void removeMember(Long teacherId, Long classId, Long memberId);

    ClassVO joinClass(Long studentId, JoinClassRequest request);

    void quitClass(Long studentId, Long classId);

    List<ClassVO> listMyClasses(Long studentId);

    List<ClassVO> listCourseClasses(Long teacherId, Long courseId);

    /** 校验学生是否在某班级中 */
    boolean checkMember(Long classId, Long studentId);

    /** 校验学生是否在某课程的任一班级中 */
    boolean isStudentInCourse(Long courseId, Long studentId);
}
