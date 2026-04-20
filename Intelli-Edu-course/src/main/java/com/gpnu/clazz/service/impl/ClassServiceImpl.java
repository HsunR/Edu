package com.gpnu.clazz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpnu.api.client.user.UserFeignClient;
import com.gpnu.api.dto.user.UserSimpleDTO;
import com.gpnu.clazz.mapper.ClassMemberMapper;
import com.gpnu.clazz.mapper.ClazzMapper;
import com.gpnu.clazz.model.dto.ClassCreateRequest;
import com.gpnu.clazz.model.dto.ClassUpdateRequest;
import com.gpnu.clazz.model.dto.JoinClassRequest;
import com.gpnu.clazz.model.entity.ClassMember;
import com.gpnu.clazz.model.entity.Clazz;
import com.gpnu.clazz.model.enums.ClassStatus;
import com.gpnu.clazz.model.enums.MemberStatus;
import com.gpnu.clazz.model.vo.ClassMemberVO;
import com.gpnu.clazz.model.vo.ClassVO;
import com.gpnu.clazz.service.IClassService;
import com.gpnu.common.common.PageRequest;
import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.exception.ThrowUtils;
import com.gpnu.course.mapper.CourseMapper;
import com.gpnu.course.model.entity.Course;
import com.gpnu.course.model.enums.CourseStatus;
import com.gpnu.course.service.ICourseService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author Chenxingdong
* @description 针对表【co_class(班级表（课程开课实例）)】的数据库操作Service实现
* @createDate 2026-04-19 22:19:03
*/
@Service
@Slf4j
public class ClassServiceImpl extends ServiceImpl<ClazzMapper, Clazz>
        implements IClassService {

    @Resource
    private ClassMemberMapper classMemberMapper;

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private UserFeignClient userFeignClient;

    private static final int INVITE_CODE_LENGTH = 8;
    private static final int MAX_RETRY = 3;

    // ==================== 教师端 ====================

    @Override
    public ClassVO createClass(Long teacherId, ClassCreateRequest request) {
        // 校验课程存在且为该教师的已发布课程
        Course course = courseMapper.selectById(request.getCourseId());
        ThrowUtils.throwIf(course == null, ErrorCode.NOT_FOUND_ERROR, "课程不存在");
        ThrowUtils.throwIf(!course.getTeacherId().equals(teacherId),
                ErrorCode.NO_AUTH_ERROR, "无权为他人的课程创建班级");
        ThrowUtils.throwIf(course.getStatus() != CourseStatus.PUBLISHED,
                ErrorCode.OPERATION_ERROR, "只有已发布的课程才能创建班级");

        Clazz clazz = new Clazz();
        BeanUtil.copyProperties(request, clazz);
        clazz.setTeacherId(teacherId);
        clazz.setInviteCode(generateUniqueInviteCode());
        clazz.setStatus(ClassStatus.RECRUITING.getCode());
        save(clazz);

        log.info("Class created, classId={}, courseId={}, teacherId={}",
                clazz.getClassId(), request.getCourseId(), teacherId);
        return buildClassVO(clazz, course);
    }

    @Override
    public ClassVO updateClass(Long teacherId, Long classId, ClassUpdateRequest request) {
        Clazz clazz = getById(classId);
        ThrowUtils.throwIf(clazz == null, ErrorCode.NOT_FOUND_ERROR, "班级不存在");
        ThrowUtils.throwIf(!clazz.getTeacherId().equals(teacherId),
                ErrorCode.NO_AUTH_ERROR, "无权操作他人的班级");

        if (request.getClassName() != null) clazz.setClassName(request.getClassName());
        if (request.getMaxStudents() != null) clazz.setMaxStudents(request.getMaxStudents());
        if (request.getStartDate() != null) clazz.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) clazz.setEndDate(request.getEndDate());
        if (request.getStatus() != null) clazz.setStatus(request.getStatus());
        updateById(clazz);

        Course course = courseMapper.selectById(clazz.getCourseId());
        return buildClassVO(clazz, course);
    }

    @Override
    public Page<ClassMemberVO> listMembers(Long teacherId, Long classId, PageRequest pageRequest) {
        Clazz clazz = getById(classId);
        ThrowUtils.throwIf(clazz == null, ErrorCode.NOT_FOUND_ERROR, "班级不存在");
        ThrowUtils.throwIf(!clazz.getTeacherId().equals(teacherId),
                ErrorCode.NO_AUTH_ERROR, "无权查看他人班级的成员");

        Page<ClassMember> page = classMemberMapper.selectPage(
                new Page<>(pageRequest.getCurrent(), pageRequest.getPageSize()),
                new LambdaQueryWrapper<ClassMember>()
                        .eq(ClassMember::getClassId, classId)
                        .eq(ClassMember::getStatus, MemberStatus.ACTIVE.getCode())
                        .orderByAsc(ClassMember::getJoinedAt));

        // 批量查学生信息
        List<Long> studentIds = page.getRecords().stream()
                .map(ClassMember::getStudentId).toList();

        Map<Long, UserSimpleDTO> userMap = Map.of();
        if (!studentIds.isEmpty()) {
            try {
                List<UserSimpleDTO> users = userFeignClient.getUserSimpleBatch(studentIds);
                userMap = users.stream()
                        .collect(Collectors.toMap(UserSimpleDTO::getUserId, u -> u));
            } catch (Exception e) {
                log.warn("Failed to fetch student info batch", e);
            }
        }

        Map<Long, UserSimpleDTO> finalUserMap = userMap;
        Page<ClassMemberVO> voPage = new Page<>(pageRequest.getCurrent(), pageRequest.getPageSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(m -> {
            ClassMemberVO vo = new ClassMemberVO();
            BeanUtil.copyProperties(m, vo);
            UserSimpleDTO user = finalUserMap.get(m.getStudentId());
            if (user != null) {
                vo.setStudentName(user.getName());
                vo.setAvatarUrl(user.getAvatarUrl());
            }
            return vo;
        }).toList());

        return voPage;
    }

    @Override
    public void removeMember(Long teacherId, Long classId, Long memberId) {
        Clazz clazz = getById(classId);
        ThrowUtils.throwIf(clazz == null, ErrorCode.NOT_FOUND_ERROR, "班级不存在");
        ThrowUtils.throwIf(!clazz.getTeacherId().equals(teacherId),
                ErrorCode.NO_AUTH_ERROR, "无权操作他人的班级");

        ClassMember member = classMemberMapper.selectById(memberId);
        ThrowUtils.throwIf(member == null || !member.getClassId().equals(classId),
                ErrorCode.NOT_FOUND_ERROR, "成员不存在");

        member.setStatus(MemberStatus.WITHDRAWN);
        classMemberMapper.updateById(member);
        log.info("Member removed, classId={}, memberId={}", classId, memberId);
    }

    // ==================== 学生端 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClassVO joinClass(Long studentId, JoinClassRequest request) {
        // 根据邀请码查班级
        Clazz clazz = lambdaQuery()
                .eq(Clazz::getInviteCode, request.getInviteCode())
                .one();
        ThrowUtils.throwIf(clazz == null, ErrorCode.NOT_FOUND_ERROR, "邀请码无效");
        ThrowUtils.throwIf(clazz.getStatus() != ClassStatus.RECRUITING,
                ErrorCode.OPERATION_ERROR, "该班级暂不接受新成员加入");

        // 校验是否已经在班级中
        boolean exists = classMemberMapper.exists(
                new LambdaQueryWrapper<ClassMember>()
                        .eq(ClassMember::getClassId, clazz.getClassId())
                        .eq(ClassMember::getStudentId, studentId)
                        .eq(ClassMember::getStatus, MemberStatus.ACTIVE.getCode()));
        ThrowUtils.throwIf(exists, ErrorCode.OPERATION_ERROR, "您已在该班级中");

        // 校验人数上限
        if (clazz.getMaxStudents() != null) {
            long currentCount = classMemberMapper.selectCount(
                    new LambdaQueryWrapper<ClassMember>()
                            .eq(ClassMember::getClassId, clazz.getClassId())
                            .eq(ClassMember::getStatus, MemberStatus.ACTIVE.getCode()));
            ThrowUtils.throwIf(currentCount >= clazz.getMaxStudents(),
                    ErrorCode.OPERATION_ERROR, "班级人数已满");
        }

        ClassMember member = new ClassMember();
        member.setClassId(clazz.getClassId());
        member.setStudentId(studentId);
        member.setStatus(MemberStatus.ACTIVE);
        member.setJoinedAt(OffsetDateTime.now());
        classMemberMapper.insert(member);

        log.info("Student joined class, studentId={}, classId={}", studentId, clazz.getClassId());

        Course course = courseMapper.selectById(clazz.getCourseId());
        ClassVO vo = buildClassVO(clazz, course);
        vo.setInviteCode(null); // 学生端不返回邀请码
        return vo;
    }

    @Override
    public void quitClass(Long studentId, Long classId) {
        ClassMember member = classMemberMapper.selectOne(
                new LambdaQueryWrapper<ClassMember>()
                        .eq(ClassMember::getClassId, classId)
                        .eq(ClassMember::getStudentId, studentId)
                        .eq(ClassMember::getStatus, MemberStatus.ACTIVE.getCode()));
        ThrowUtils.throwIf(member == null, ErrorCode.NOT_FOUND_ERROR, "您不在该班级中");

        member.setStatus(MemberStatus.WITHDRAWN);
        classMemberMapper.updateById(member);
        log.info("Student quit class, studentId={}, classId={}", studentId, classId);
    }

    @Override
    public List<ClassVO> listMyClasses(Long studentId) {
        // 查所有 ACTIVE 成员记录
        List<ClassMember> members = classMemberMapper.selectList(
                new LambdaQueryWrapper<ClassMember>()
                        .eq(ClassMember::getStudentId, studentId)
                        .eq(ClassMember::getStatus, MemberStatus.ACTIVE.getCode()));

        if (members.isEmpty()) {
            return List.of();
        }

        List<Long> classIds = members.stream().map(ClassMember::getClassId).toList();
        List<Clazz> classes = listByIds(classIds);

        // 批量查课程名
        List<Long> courseIds = classes.stream().map(Clazz::getCourseId).distinct().toList();
        Map<Long, Course> courseMap = courseMapper.selectByIds(courseIds).stream()
                .collect(Collectors.toMap(Course::getCourseId, c -> c));

        return classes.stream().map(c -> {
            ClassVO vo = buildClassVO(c, courseMap.get(c.getCourseId()));
            vo.setInviteCode(null); // 学生端不返回邀请码
            return vo;
        }).toList();
    }

    @Override
    public List<ClassVO> listCourseClasses(Long teacherId, Long courseId) {
        Course course = courseMapper.selectById(courseId);
        ThrowUtils.throwIf(course == null, ErrorCode.NOT_FOUND_ERROR, "课程不存在");
        ThrowUtils.throwIf(!course.getTeacherId().equals(teacherId),
                ErrorCode.NO_AUTH_ERROR, "无权查看他人课程的班级");

        List<Clazz> classes = lambdaQuery()
                .eq(Clazz::getCourseId, courseId)
                .orderByDesc(Clazz::getCreatedAt)
                .list();

        return classes.stream().map(c -> buildClassVO(c, course)).toList();
    }

    // ==================== 校验方法（供其他Service调用） ====================

    @Override
    public boolean checkMember(Long classId, Long studentId) {
        return classMemberMapper.exists(
                new LambdaQueryWrapper<ClassMember>()
                        .eq(ClassMember::getClassId, classId)
                        .eq(ClassMember::getStudentId, studentId)
                        .eq(ClassMember::getStatus, MemberStatus.ACTIVE.getCode()));
    }

    @Override
    public boolean isStudentInCourse(Long courseId, Long studentId) {
        // 查该课程下所有班级ID
        List<Clazz> classes = lambdaQuery()
                .eq(Clazz::getCourseId, courseId)
                .select(Clazz::getClassId)
                .list();

        if (classes.isEmpty()) {
            return false;
        }

        List<Long> classIds = classes.stream().map(Clazz::getClassId).toList();
        return classMemberMapper.exists(
                new LambdaQueryWrapper<ClassMember>()
                        .in(ClassMember::getClassId, classIds)
                        .eq(ClassMember::getStudentId, studentId)
                        .eq(ClassMember::getStatus, MemberStatus.ACTIVE.getCode()));
    }

    // ==================== 私有方法 ====================

    /**
     * 生成唯一邀请码：8位大写字母+数字，DB unique约束兜底，冲突重试
     */
    private String generateUniqueInviteCode() {
        for (int i = 0; i < MAX_RETRY; i++) {
            String code = RandomUtil.randomString("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789", INVITE_CODE_LENGTH);
            boolean exists = lambdaQuery().eq(Clazz::getInviteCode, code).exists();
            if (!exists) {
                return code;
            }
        }
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "邀请码生成失败，请重试");
    }

    private ClassVO buildClassVO(Clazz clazz, Course course) {
        ClassVO vo = new ClassVO();
        BeanUtil.copyProperties(clazz, vo);

        if (course != null) {
            vo.setCourseName(course.getCourseName());
        }

        // 填充教师名
        try {
            UserSimpleDTO teacher = userFeignClient.getUserSimple(clazz.getTeacherId());
            if (teacher != null) {
                vo.setTeacherName(teacher.getName());
            }
        } catch (Exception e) {
            log.warn("Failed to fetch teacher info, teacherId={}", clazz.getTeacherId(), e);
        }

        // 统计当前人数
        long count = classMemberMapper.selectCount(
                new LambdaQueryWrapper<ClassMember>()
                        .eq(ClassMember::getClassId, clazz.getClassId())
                        .eq(ClassMember::getStatus, MemberStatus.ACTIVE.getCode()));
        vo.setCurrentStudents((int) count);

        return vo;
    }
}




