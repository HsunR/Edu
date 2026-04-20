package com.gpnu.course.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpnu.api.client.resource.ResourceFeignClient;
import com.gpnu.api.dto.resource.ResourceSimpleDTO;
import com.gpnu.clazz.service.IClassService;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.exception.ThrowUtils;
import com.gpnu.course.mapper.SectionResourceMapper;
import com.gpnu.course.model.dto.chapter.OrderItem;
import com.gpnu.course.model.dto.section.SectionCreateRequest;
import com.gpnu.course.model.dto.section.SectionResourceAddRequest;
import com.gpnu.course.model.dto.section.SectionUpdateRequest;
import com.gpnu.course.model.entity.Chapter;
import com.gpnu.course.model.entity.Course;
import com.gpnu.course.model.entity.Section;
import com.gpnu.course.model.entity.SectionResource;
import com.gpnu.course.model.vo.section.SectionDetailVO;
import com.gpnu.course.model.vo.section.SectionResourceVO;
import com.gpnu.course.model.vo.section.SectionVO;
import com.gpnu.course.service.IChapterService;
import com.gpnu.course.service.ICourseService;
import com.gpnu.course.service.ISectionService;
import com.gpnu.course.mapper.SectionMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author Chenxingdong
* @description 针对表【co_section(课程节表)】的数据库操作Service实现
* @createDate 2026-04-19 22:19:18
*/
@Service
@Slf4j
public class SectionServiceImpl extends ServiceImpl<SectionMapper, Section>
        implements ISectionService {

    @Resource
    @Lazy
    private ICourseService courseService;

    @Resource
    @Lazy
    private IChapterService chapterService;

    @Resource
    @Lazy
    private IClassService classService;

    @Resource
    private SectionResourceMapper sectionResourceMapper;

    @Resource
    private ResourceFeignClient resourceFeignClient;

    // ==================== 节管理 ====================

    @Override
    public SectionVO addSection(Long teacherId, Long chapterId, SectionCreateRequest request) {
        Chapter chapter = chapterService.getById(chapterId);
        ThrowUtils.throwIf(chapter == null, ErrorCode.NOT_FOUND_ERROR, "章不存在");
        checkCourseOwner(chapter.getCourseId(), teacherId);

        // 计算最大排序号
        Long maxOrder = lambdaQuery()
                .eq(Section::getChapterId, chapterId)
                .orderByDesc(Section::getOrderIndex)
                .last("LIMIT 1")
                .oneOpt()
                .map(s -> (long) s.getOrderIndex())
                .orElse(-1L);

        Section section = new Section();
        section.setChapterId(chapterId);
        section.setCourseId(chapter.getCourseId());
        section.setTitle(request.getTitle());
        section.setIsFree(request.getIsFree() != null ? request.getIsFree() : 0);
        section.setOrderIndex((int) (maxOrder + 1));
        save(section);

        log.info("Section added, sectionId={}, chapterId={}", section.getSectionId(), chapterId);
        return toVO(section);
    }

    @Override
    public SectionVO updateSection(Long teacherId, Long sectionId, SectionUpdateRequest request) {
        Section section = getById(sectionId);
        ThrowUtils.throwIf(section == null, ErrorCode.NOT_FOUND_ERROR, "节不存在");
        checkCourseOwner(section.getCourseId(), teacherId);

        if (request.getTitle() != null) {
            section.setTitle(request.getTitle());
        }
        if (request.getIsFree() != null) {
            section.setIsFree(request.getIsFree());
        }
        updateById(section);

        return toVO(section);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSection(Long teacherId, Long sectionId) {
        Section section = getById(sectionId);
        ThrowUtils.throwIf(section == null, ErrorCode.NOT_FOUND_ERROR, "节不存在");
        checkCourseOwner(section.getCourseId(), teacherId);

        // 删除节资源关联
        sectionResourceMapper.delete(
                new LambdaQueryWrapper<SectionResource>()
                        .eq(SectionResource::getSectionId, sectionId));

        removeById(sectionId);
        log.info("Section deleted, sectionId={}", sectionId);
    }

    @Override
    public void reorderSections(Long teacherId, Long chapterId, List<OrderItem> orderItems) {
        Chapter chapter = chapterService.getById(chapterId);
        ThrowUtils.throwIf(chapter == null, ErrorCode.NOT_FOUND_ERROR, "章不存在");
        checkCourseOwner(chapter.getCourseId(), teacherId);

        for (OrderItem item : orderItems) {
            Section section = new Section();
            section.setSectionId(item.getId());
            section.setOrderIndex(item.getOrderIndex());
            updateById(section);
        }
    }

    // ==================== 节内资源管理 ====================

    @Override
    public SectionResourceVO addResource(Long teacherId, Long sectionId, SectionResourceAddRequest request) {
        Section section = getById(sectionId);
        ThrowUtils.throwIf(section == null, ErrorCode.NOT_FOUND_ERROR, "节不存在");
        checkCourseOwner(section.getCourseId(), teacherId);

        // 校验资源类型合法性
        String rt = request.getResourceType().toUpperCase();
        ThrowUtils.throwIf(!rt.equals("VIDEO") && !rt.equals("DOCUMENT") && !rt.equals("IMAGE"),
                ErrorCode.PARAMS_ERROR, "资源类型不合法，允许值：VIDEO/DOCUMENT/IMAGE");

        // 校验是否已关联
        boolean exists = sectionResourceMapper.exists(
                new LambdaQueryWrapper<SectionResource>()
                        .eq(SectionResource::getSectionId, sectionId)
                        .eq(SectionResource::getResourceId, request.getResourceId()));
        ThrowUtils.throwIf(exists, ErrorCode.OPERATION_ERROR, "该资源已关联到此节");

        // 计算最大排序号
        Long maxOrder = sectionResourceMapper.selectCount(
                new LambdaQueryWrapper<SectionResource>()
                        .eq(SectionResource::getSectionId, sectionId));

        SectionResource sr = new SectionResource();
        sr.setSectionId(sectionId);
        sr.setResourceId(request.getResourceId());
        sr.setResourceType(rt);
        sr.setOrderIndex(maxOrder.intValue());
        sectionResourceMapper.insert(sr);

        log.info("Resource added to section, sectionId={}, resourceId={}", sectionId, request.getResourceId());
        SectionResourceVO vo = new SectionResourceVO();
        BeanUtil.copyProperties(sr, vo);
        return vo;
    }

    @Override
    public void removeResource(Long teacherId, Long sectionId, Long id) {
        Section section = getById(sectionId);
        ThrowUtils.throwIf(section == null, ErrorCode.NOT_FOUND_ERROR, "节不存在");
        checkCourseOwner(section.getCourseId(), teacherId);

        sectionResourceMapper.deleteById(id);
        log.info("Resource removed from section, sectionId={}, srId={}", sectionId, id);
    }

    @Override
    public void reorderResources(Long teacherId, Long sectionId, List<OrderItem> orderItems) {
        Section section = getById(sectionId);
        ThrowUtils.throwIf(section == null, ErrorCode.NOT_FOUND_ERROR, "节不存在");
        checkCourseOwner(section.getCourseId(), teacherId);

        for (OrderItem item : orderItems) {
            SectionResource sr = new SectionResource();
            sr.setId(item.getId());
            sr.setOrderIndex(item.getOrderIndex());
            sectionResourceMapper.updateById(sr);
        }
    }

    // ==================== 学生访问 ====================

    @Override
    public SectionDetailVO getSectionDetail(Long sectionId, Long currentUserId, Integer userType) {
        Section section = getById(sectionId);
        ThrowUtils.throwIf(section == null, ErrorCode.NOT_FOUND_ERROR, "节不存在");

        Course course = courseService.getById(section.getCourseId());
        ThrowUtils.throwIf(course == null, ErrorCode.NOT_FOUND_ERROR, "课程不存在");

        // 权限校验
        boolean isOwner = course.getTeacherId().equals(currentUserId);
        boolean isFreeSection = course.getIsPublic() == 1 && section.getIsFree() == 1;

        if (!isOwner && !isFreeSection) {
            // 需要入班
            boolean inCourse = classService.isStudentInCourse(section.getCourseId(), currentUserId);
            ThrowUtils.throwIf(!inCourse, ErrorCode.NO_AUTH_ERROR, "请先加入班级后再访问课程内容");
        }

        // 查节内资源关联
        List<SectionResource> srList = sectionResourceMapper.selectList(
                new LambdaQueryWrapper<SectionResource>()
                        .eq(SectionResource::getSectionId, sectionId)
                        .orderByAsc(SectionResource::getOrderIndex));

        SectionDetailVO vo = new SectionDetailVO();
        BeanUtil.copyProperties(section, vo);

        // Feign 批量查资源详情
        if (!srList.isEmpty()) {
            List<Long> resourceIds = srList.stream().map(SectionResource::getResourceId).toList();
            try {
                List<ResourceSimpleDTO> details = resourceFeignClient.getResourceSimpleBatch(resourceIds);
                vo.setResourceDetails(details);
            } catch (Exception e) {
                log.warn("Failed to fetch resource details, sectionId={}", sectionId, e);
                vo.setResources(List.of());
            }

            // 同时填充简要资源列表
            vo.setResources(srList.stream().map(r -> {
                SectionResourceVO srv = new SectionResourceVO();
                BeanUtil.copyProperties(r, srv);
                return srv;
            }).toList());
        } else {
            vo.setResourceDetails(List.of());
            vo.setResources(List.of());
        }

        return vo;
    }

    // ==================== 私有方法 ====================

    private void checkCourseOwner(Long courseId, Long teacherId) {
        Course course = courseService.getById(courseId);
        ThrowUtils.throwIf(course == null, ErrorCode.NOT_FOUND_ERROR, "课程不存在");
        ThrowUtils.throwIf(!course.getTeacherId().equals(teacherId),
                ErrorCode.NO_AUTH_ERROR, "无权操作他人的课程");
    }

    private SectionVO toVO(Section section) {
        SectionVO vo = new SectionVO();
        BeanUtil.copyProperties(section, vo);
        return vo;
    }
}




