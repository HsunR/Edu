package com.gpnu.course.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.exception.ThrowUtils;
import com.gpnu.course.mapper.SectionMapper;
import com.gpnu.course.mapper.SectionResourceMapper;
import com.gpnu.course.model.dto.chapter.ChapterCreateRequest;
import com.gpnu.course.model.dto.chapter.ChapterUpdateRequest;
import com.gpnu.course.model.dto.chapter.OrderItem;
import com.gpnu.course.model.entity.Chapter;
import com.gpnu.course.model.entity.Course;
import com.gpnu.course.model.entity.Section;
import com.gpnu.course.model.entity.SectionResource;
import com.gpnu.course.model.vo.chapter.ChapterVO;
import com.gpnu.course.service.IChapterService;
import com.gpnu.course.mapper.ChapterMapper;
import com.gpnu.course.service.ICourseService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author Chenxingdong
* @description 针对表【co_chapter(课程章表)】的数据库操作Service实现
* @createDate 2026-04-19 22:18:55
*/
@Service
@Slf4j
public class ChapterServiceImpl extends ServiceImpl<ChapterMapper, Chapter>
        implements IChapterService {

    @Resource
    @Lazy
    private ICourseService courseService;

    @Resource
    private SectionMapper sectionMapper;

    @Resource
    private SectionResourceMapper sectionResourceMapper;

    @Override
    public ChapterVO addChapter(Long teacherId, Long courseId, ChapterCreateRequest request) {
        checkCourseOwner(courseId, teacherId);

        // 计算当前最大排序号
        Long maxOrder = lambdaQuery()
                .eq(Chapter::getCourseId, courseId)
                .orderByDesc(Chapter::getOrderIndex)
                .last("LIMIT 1")
                .oneOpt()
                .map(c -> (long) c.getOrderIndex())
                .orElse(-1L);

        Chapter chapter = new Chapter();
        chapter.setCourseId(courseId);
        chapter.setTitle(request.getTitle());
        chapter.setOrderIndex((int) (maxOrder + 1));
        save(chapter);

        log.info("Chapter added, chapterId={}, courseId={}", chapter.getChapterId(), courseId);
        return toVO(chapter);
    }

    @Override
    public ChapterVO updateChapter(Long teacherId, Long chapterId, ChapterUpdateRequest request) {
        Chapter chapter = getById(chapterId);
        ThrowUtils.throwIf(chapter == null, ErrorCode.NOT_FOUND_ERROR, "章不存在");
        checkCourseOwner(chapter.getCourseId(), teacherId);

        chapter.setTitle(request.getTitle());
        updateById(chapter);

        return toVO(chapter);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteChapter(Long teacherId, Long chapterId) {
        Chapter chapter = getById(chapterId);
        ThrowUtils.throwIf(chapter == null, ErrorCode.NOT_FOUND_ERROR, "章不存在");
        checkCourseOwner(chapter.getCourseId(), teacherId);

        // 查出章下所有节
        List<Section> sections = sectionMapper.selectList(
                new LambdaQueryWrapper<Section>().eq(Section::getChapterId, chapterId));

        if (!sections.isEmpty()) {
            List<Long> sectionIds = sections.stream().map(Section::getSectionId).toList();
            // 删除节资源关联
            sectionResourceMapper.delete(
                    new LambdaQueryWrapper<SectionResource>()
                            .in(SectionResource::getSectionId, sectionIds));
            // 删除节
            sectionMapper.delete(
                    new LambdaQueryWrapper<Section>().eq(Section::getChapterId, chapterId));
        }

        removeById(chapterId);
        log.info("Chapter deleted, chapterId={}", chapterId);
    }

    @Override
    public void reorderChapters(Long teacherId, Long courseId, List<OrderItem> orderItems) {
        checkCourseOwner(courseId, teacherId);

        for (OrderItem item : orderItems) {
            Chapter chapter = new Chapter();
            chapter.setChapterId(item.getId());
            chapter.setOrderIndex(item.getOrderIndex());
            updateById(chapter);
        }
    }

    // ==================== 私有方法 ====================

    private void checkCourseOwner(Long courseId, Long teacherId) {
        Course course = courseService.getById(courseId);
        ThrowUtils.throwIf(course == null, ErrorCode.NOT_FOUND_ERROR, "课程不存在");
        ThrowUtils.throwIf(!course.getTeacherId().equals(teacherId),
                ErrorCode.NO_AUTH_ERROR, "无权操作他人的课程");
    }

    private ChapterVO toVO(Chapter chapter) {
        ChapterVO vo = new ChapterVO();
        BeanUtil.copyProperties(chapter, vo);
        return vo;
    }
}




