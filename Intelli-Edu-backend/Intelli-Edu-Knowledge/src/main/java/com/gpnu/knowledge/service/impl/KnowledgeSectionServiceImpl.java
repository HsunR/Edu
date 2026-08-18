package com.gpnu.knowledge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpnu.api.client.course.CourseFeignClient;
import com.gpnu.api.dto.section.SectionSimpleDTO;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.exception.ThrowUtils;
import com.gpnu.knowledge.mapper.KnowledgePointMapper;
import com.gpnu.knowledge.mapper.KnowledgeSectionMapper;
import com.gpnu.knowledge.model.entity.KnowledgePoint;
import com.gpnu.knowledge.model.entity.KnowledgeSection;
import com.gpnu.knowledge.model.vo.KnowledgePointVO;
import com.gpnu.knowledge.service.IKnowledgeSectionService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class KnowledgeSectionServiceImpl extends ServiceImpl<KnowledgeSectionMapper, KnowledgeSection>
        implements IKnowledgeSectionService {

    @Resource
    private KnowledgePointMapper knowledgePointMapper;

    @Resource
    private CourseFeignClient courseFeignClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindSections(Long teacherId, Long pointId, List<Long> sectionIds) {
        // 校验知识点存在且为二级知识点
        KnowledgePoint point = knowledgePointMapper.selectById(pointId);
        ThrowUtils.throwIf(point == null, ErrorCode.NOT_FOUND_ERROR, "知识点不存在");
        ThrowUtils.throwIf(point.getParentId() == null,
                ErrorCode.PARAMS_ERROR, "一级知识点不能关联章节，请选择二级知识点");

        for (Long sectionId : sectionIds) {
            // Feign 校验章节存在且属于同一课程
            SectionSimpleDTO section = courseFeignClient.getSectionSimple(sectionId);
            ThrowUtils.throwIf(section == null, ErrorCode.NOT_FOUND_ERROR, "章节不存在");
            ThrowUtils.throwIf(!section.getCourseId().equals(point.getCourseId()),
                    ErrorCode.PARAMS_ERROR, "章节不属于该课程");

            // 检查是否已关联
            boolean exists = exists(new LambdaQueryWrapper<KnowledgeSection>()
                    .eq(KnowledgeSection::getPointId, pointId)
                    .eq(KnowledgeSection::getSectionId, sectionId));
            if (exists) {
                continue;
            }

            KnowledgeSection ks = new KnowledgeSection();
            ks.setPointId(pointId);
            ks.setSectionId(sectionId);
            ks.setCourseId(point.getCourseId());
            save(ks);
        }
        log.info("Sections bound to point, pointId={}, sectionIds={}", pointId, sectionIds);
    }

    @Override
    public void unbindSection(Long teacherId, Long pointId, Long sectionId) {
        KnowledgePoint point = knowledgePointMapper.selectById(pointId);
        ThrowUtils.throwIf(point == null, ErrorCode.NOT_FOUND_ERROR, "知识点不存在");

        int deleted = getBaseMapper().delete(new LambdaQueryWrapper<KnowledgeSection>()
                .eq(KnowledgeSection::getPointId, pointId)
                .eq(KnowledgeSection::getSectionId, sectionId));
        ThrowUtils.throwIf(deleted == 0, ErrorCode.NOT_FOUND_ERROR, "关联关系不存在");
        log.info("Section unbound from point, pointId={}, sectionId={}", pointId, sectionId);
    }

    @Override
    public List<KnowledgePointVO> getPointsBySection(Long sectionId) {
        List<KnowledgeSection> records = list(new LambdaQueryWrapper<KnowledgeSection>()
                .eq(KnowledgeSection::getSectionId, sectionId));
        List<Long> pointIds = records.stream().map(KnowledgeSection::getPointId).toList();
        if (pointIds.isEmpty()) {
            return List.of();
        }
        List<KnowledgePoint> points = knowledgePointMapper.selectList(
                new LambdaQueryWrapper<KnowledgePoint>()
                        .in(KnowledgePoint::getPointId, pointIds));
        return points.stream().map(p -> {
            KnowledgePointVO vo = new KnowledgePointVO();
            BeanUtil.copyProperties(p, vo);
            return vo;
        }).toList();
    }

    @Override
    public List<Long> getSectionIdsByPoint(Long pointId) {
        return list(new LambdaQueryWrapper<KnowledgeSection>()
                .eq(KnowledgeSection::getPointId, pointId))
                .stream().map(KnowledgeSection::getSectionId).toList();
    }

    @Override
    public void clearSectionRelations(Long sectionId) {
        // TODO: 等消息队列建设好后，切换为 MQ 事件驱动 (course.section.deleted)
        getBaseMapper().delete(new LambdaQueryWrapper<KnowledgeSection>()
                .eq(KnowledgeSection::getSectionId, sectionId));
        log.info("Cleared all knowledge relations for sectionId={}", sectionId);
    }
}
