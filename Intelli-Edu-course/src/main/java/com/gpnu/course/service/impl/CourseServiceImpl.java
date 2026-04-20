package com.gpnu.course.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpnu.api.client.user.UserFeignClient;
import com.gpnu.api.dto.course.CourseSimpleDTO;
import com.gpnu.api.dto.user.UserSimpleDTO;
import com.gpnu.clazz.service.IClassService;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.exception.ThrowUtils;
import com.gpnu.course.mapper.*;
import com.gpnu.course.model.dto.course.CourseCreateRequest;
import com.gpnu.course.model.dto.course.CourseQueryRequest;
import com.gpnu.course.model.dto.course.CourseUpdateRequest;
import com.gpnu.course.model.entity.*;
import com.gpnu.course.model.enums.CourseStatus;
import com.gpnu.course.model.vo.chapter.ChapterVO;
import com.gpnu.course.model.vo.course.CourseDetailVO;
import com.gpnu.course.model.vo.course.CourseVO;
import com.gpnu.course.model.vo.section.SectionResourceVO;
import com.gpnu.course.model.vo.section.SectionVO;
import com.gpnu.course.service.ICourseService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author Chenxingdong
* @description 针对表【co_course(课程表)】的数据库操作Service实现
* @createDate 2026-04-19 22:19:15
*/
@Service
@Slf4j
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course>
        implements ICourseService {

    @Resource
    private ChapterMapper chapterMapper;

    @Resource
    private SectionMapper sectionMapper;

    @Resource
    private SectionResourceMapper sectionResourceMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private IClassService classService;

    // ==================== 教师端 ====================

    @Override
    public CourseVO createCourse(Long teacherId, CourseCreateRequest request) {
        Course course = new Course();
        BeanUtil.copyProperties(request, course);
        course.setTeacherId(teacherId);
        course.setStatus(CourseStatus.DRAFT);
        if (course.getIsPublic() == null) {
            course.setIsPublic(0);
        }
        save(course);

        log.info("Course created, courseId={}, teacherId={}", course.getCourseId(), teacherId);
        return buildCourseVO(course);
    }

    @Override
    public CourseVO updateCourse(Long teacherId, Long courseId, CourseUpdateRequest request) {
        Course course = getCourseAndCheckOwner(courseId, teacherId);
        BeanUtil.copyProperties(request, course, "courseId", "teacherId", "status");
        updateById(course);

        return buildCourseVO(getById(courseId));
    }

    @Override
    public void publishCourse(Long teacherId, Long courseId) {
        Course course = getCourseAndCheckOwner(courseId, teacherId);
        ThrowUtils.throwIf(course.getStatus() != CourseStatus.DRAFT,
                ErrorCode.OPERATION_ERROR, "只有草稿状态的课程可以发布");

        // 校验：至少有一章且一节
        long chapterCount = chapterMapper.selectCount(
                new LambdaQueryWrapper<Chapter>().eq(Chapter::getCourseId, courseId));
        ThrowUtils.throwIf(chapterCount == 0, ErrorCode.OPERATION_ERROR, "课程至少需要一个章节才能发布");

        course.setStatus(CourseStatus.PUBLISHED);
        updateById(course);
        log.info("Course published, courseId={}", courseId);
    }

    @Override
    public void archiveCourse(Long teacherId, Long courseId) {
        Course course = getCourseAndCheckOwner(courseId, teacherId);
        ThrowUtils.throwIf(course.getStatus() != CourseStatus.PUBLISHED,
                ErrorCode.OPERATION_ERROR, "只有已发布的课程可以归档");

        course.setStatus(CourseStatus.ARCHIVED);
        updateById(course);
        log.info("Course archived, courseId={}", courseId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCourse(Long teacherId, Long courseId) {
        Course course = getCourseAndCheckOwner(courseId, teacherId);
        ThrowUtils.throwIf(course.getStatus() != CourseStatus.DRAFT,
                ErrorCode.OPERATION_ERROR, "只有草稿状态的课程可以删除");

        // 级联逻辑删除：章 → 节 → 节资源关联
        List<Chapter> chapters = chapterMapper.selectList(
                new LambdaQueryWrapper<Chapter>().eq(Chapter::getCourseId, courseId));
        if (!chapters.isEmpty()) {
            List<Long> chapterIds = chapters.stream().map(Chapter::getChapterId).toList();
            // 删除节资源关联
            sectionResourceMapper.delete(
                    new LambdaQueryWrapper<SectionResource>()
                            .in(SectionResource::getSectionId,
                                    sectionMapper.selectList(
                                            new LambdaQueryWrapper<Section>().in(Section::getChapterId, chapterIds)
                                    ).stream().map(Section::getSectionId).toList()));
            // 删除节
            sectionMapper.delete(
                    new LambdaQueryWrapper<Section>().in(Section::getChapterId, chapterIds));
            // 删除章
            chapterMapper.delete(
                    new LambdaQueryWrapper<Chapter>().eq(Chapter::getCourseId, courseId));
        }

        removeById(courseId);
        log.info("Course deleted, courseId={}, teacherId={}", courseId, teacherId);
    }

    @Override
    public Page<CourseVO> listTeachingCourses(Long teacherId, CourseQueryRequest request) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getTeacherId, teacherId);
        wrapper.eq(request.getStatus() != null, Course::getStatus, request.getStatus());
        wrapper.like(StrUtil.isNotBlank(request.getCourseName()), Course::getCourseName, request.getCourseName());
        wrapper.eq(request.getCategoryId() != null, Course::getCategoryId, request.getCategoryId());
        wrapper.orderByDesc(Course::getUpdatedAt);

        Page<Course> page = this.page(new Page<>(request.getCurrent(), request.getPageSize()), wrapper);
        return convertPage(page, request);
    }

    // ==================== 公共端 ====================

    @Override
    public Page<CourseVO> listPublicCourses(CourseQueryRequest request) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getStatus, CourseStatus.PUBLISHED.getCode());
        wrapper.eq(Course::getIsPublic, 1);
        wrapper.like(StrUtil.isNotBlank(request.getCourseName()), Course::getCourseName, request.getCourseName());
        wrapper.eq(request.getCategoryId() != null, Course::getCategoryId, request.getCategoryId());
        wrapper.orderByDesc(Course::getCreatedAt);

        Page<Course> page = this.page(new Page<>(request.getCurrent(), request.getPageSize()), wrapper);
        return convertPage(page, request);
    }

    @Override
    public CourseDetailVO getCourseDetail(Long courseId, Long currentUserId) {
        Course course = getById(courseId);
        ThrowUtils.throwIf(course == null, ErrorCode.NOT_FOUND_ERROR, "课程不存在");

        // 权限校验：非公开课程需要入班
        if (course.getIsPublic() == 0 && currentUserId != null
                && !course.getTeacherId().equals(currentUserId)) {
            boolean inCourse = classService.isStudentInCourse(courseId, currentUserId);
            ThrowUtils.throwIf(!inCourse, ErrorCode.NO_AUTH_ERROR, "您未加入该课程的任何班级");
        }

        CourseDetailVO vo = new CourseDetailVO();
        BeanUtil.copyProperties(course, vo);
        fillTeacherInfo(vo);
        fillCategoryName(vo);

        // 加载目录树：3次查询 + 内存组装
        vo.setChapters(loadOutline(courseId));
        return vo;
    }

    // ==================== Feign ====================

    @Override
    public CourseSimpleDTO getCourseSimple(Long courseId) {
        Course course = getById(courseId);
        ThrowUtils.throwIf(course == null, ErrorCode.NOT_FOUND_ERROR, "课程不存在");
        CourseSimpleDTO dto = new CourseSimpleDTO();
        BeanUtil.copyProperties(course, dto);
        dto.setCourseCover(course.getCoverUrl());
        return dto;
    }

    @Override
    public List<CourseSimpleDTO> getCourseBatch(List<Long> courseIds) {
        if (courseIds == null || courseIds.isEmpty()) {
            return List.of();
        }
        List<Course> courses = listByIds(courseIds);
        return courses.stream().map(c -> {
            CourseSimpleDTO dto = new CourseSimpleDTO();
            BeanUtil.copyProperties(c, dto);
            dto.setCourseCover(c.getCoverUrl());
            return dto;
        }).toList();
    }

    // ==================== 私有方法 ====================

    /**
     * 查询课程并校验是否为课程创建者
     */
    private Course getCourseAndCheckOwner(Long courseId, Long teacherId) {
        Course course = getById(courseId);
        ThrowUtils.throwIf(course == null, ErrorCode.NOT_FOUND_ERROR, "课程不存在");
        ThrowUtils.throwIf(!course.getTeacherId().equals(teacherId),
                ErrorCode.NO_AUTH_ERROR, "无权操作他人的课程");
        return course;
    }

    /**
     * 加载课程完整目录树（章 + 节 + 节内资源）
     */
    private List<ChapterVO> loadOutline(Long courseId) {
        List<Chapter> chapters = chapterMapper.selectList(
                new LambdaQueryWrapper<Chapter>()
                        .eq(Chapter::getCourseId, courseId)
                        .orderByAsc(Chapter::getOrderIndex));

        List<Section> sections = sectionMapper.selectList(
                new LambdaQueryWrapper<Section>()
                        .eq(Section::getCourseId, courseId)
                        .orderByAsc(Section::getOrderIndex));

        // 查节内资源
        List<Long> sectionIds = sections.stream().map(Section::getSectionId).toList();
        List<SectionResource> resources = sectionIds.isEmpty() ? List.of() :
                sectionResourceMapper.selectList(
                        new LambdaQueryWrapper<SectionResource>()
                        .in(SectionResource::getSectionId, sectionIds)
                        .orderByAsc(SectionResource::getOrderIndex));

        // 资源按 sectionId 分组
        Map<Long, List<SectionResourceVO>> resMap = resources.stream()
                .collect(Collectors.groupingBy(SectionResource::getSectionId,
                        Collectors.mapping(r -> {
                            SectionResourceVO rv = new SectionResourceVO();
                            BeanUtil.copyProperties(r, rv);
                            return rv;
                        }, Collectors.toList())));

        // 节按 chapterId 分组
        Map<Long, List<SectionVO>> secMap = sections.stream()
                .collect(Collectors.groupingBy(Section::getChapterId,
                        Collectors.mapping(s -> {
                            SectionVO sv = new SectionVO();
                            BeanUtil.copyProperties(s, sv);
                            sv.setResources(resMap.getOrDefault(s.getSectionId(), List.of()));
                            return sv;
                        }, Collectors.toList())));

        // 组装章
        return chapters.stream().map(ch -> {
            ChapterVO cv = new ChapterVO();
            BeanUtil.copyProperties(ch, cv);
            cv.setSections(secMap.getOrDefault(ch.getChapterId(), List.of()));
            return cv;
        }).toList();
    }

    private CourseVO buildCourseVO(Course course) {
        CourseVO vo = new CourseVO();
        BeanUtil.copyProperties(course, vo);
        fillTeacherInfo(vo);
        fillCategoryName(vo);
        return vo;
    }

    private void fillTeacherInfo(CourseVO vo) {
        try {
            UserSimpleDTO teacher = userFeignClient.getUserSimple(vo.getTeacherId());
            if (teacher != null) {
                vo.setTeacherName(teacher.getName());
                vo.setTeacherAvatar(teacher.getAvatarUrl());
            }
        } catch (Exception e) {
            log.warn("Failed to fetch teacher info, teacherId={}", vo.getTeacherId(), e);
        }
    }

    private void fillCategoryName(CourseVO vo) {
        if (vo.getCategoryId() != null) {
            Category category = categoryMapper.selectById(vo.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }
    }

    private Page<CourseVO> convertPage(Page<Course> page, CourseQueryRequest request) {
        Page<CourseVO> voPage = new Page<>(request.getCurrent(), request.getPageSize(), page.getTotal());
        List<CourseVO> voList = page.getRecords().stream()
                .map(this::buildCourseVO)
                .toList();
        voPage.setRecords(voList);
        return voPage;
    }
}




