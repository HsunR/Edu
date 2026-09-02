package com.gpnu.learning.service;

import com.gpnu.api.client.course.CourseFeignClient;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.exception.ThrowUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 班级访问权限校验：学生端、教师端共用 Feign，保证只能访问有权限的 classId。
 * <p>
 * 学情数据虽「共用一套表」，但接口层必须隔离视角，防止越权。
 */
@Service
public class ClassAccessService {

    @Resource
    private CourseFeignClient courseFeignClient;

    /**
     * 校验学生是否为班级有效成员（与 StudentLearningService 原逻辑一致）。
     */
    public void validateStudentMember(Long studentId, Long classId) {
        ThrowUtils.throwIf(classId == null, ErrorCode.PARAMS_ERROR, "classId 不能为空");
        Boolean isMember = courseFeignClient.checkMember(classId, studentId);
        ThrowUtils.throwIf(!Boolean.TRUE.equals(isMember), ErrorCode.NO_AUTH_ERROR, "您不是该班级成员");
    }

    /**
     * 校验教师是否为该班授课教师（教师端学情/推荐前置条件）。
     */
    public void validateClassTeacher(Long teacherId, Long classId) {
        ThrowUtils.throwIf(classId == null, ErrorCode.PARAMS_ERROR, "classId 不能为空");
        Boolean ok = courseFeignClient.checkClassTeacher(classId, teacherId);
        ThrowUtils.throwIf(!Boolean.TRUE.equals(ok), ErrorCode.NO_AUTH_ERROR, "您无权查看该班级学情");
    }
}
