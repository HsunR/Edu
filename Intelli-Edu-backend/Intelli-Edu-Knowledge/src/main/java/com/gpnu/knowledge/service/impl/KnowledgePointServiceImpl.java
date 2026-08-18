package com.gpnu.knowledge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpnu.api.dto.knowledge.KnowledgeTreeDTO;
import com.gpnu.api.dto.knowledge.PointSimpleDTO;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.exception.ThrowUtils;
import com.gpnu.knowledge.mapper.KnowledgePointMapper;
import com.gpnu.knowledge.mapper.KnowledgeSectionMapper;
import com.gpnu.knowledge.mapper.QuestionKnowledgeMapper;
import com.gpnu.knowledge.model.dto.PointCreateRequest;
import com.gpnu.knowledge.model.dto.PointUpdateRequest;
import com.gpnu.knowledge.model.entity.KnowledgePoint;
import com.gpnu.knowledge.model.entity.QuestionKnowledge;
import com.gpnu.knowledge.model.vo.KnowledgePointVO;
import com.gpnu.knowledge.model.vo.KnowledgeTreeVO;
import com.gpnu.knowledge.model.vo.PointSimpleVO;
import com.gpnu.knowledge.service.IKnowledgePointService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class KnowledgePointServiceImpl extends ServiceImpl<KnowledgePointMapper, KnowledgePoint>
        implements IKnowledgePointService {

    @Resource
    private KnowledgeSectionMapper knowledgeSectionMapper;

    @Resource
    private QuestionKnowledgeMapper questionKnowledgeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public KnowledgePointVO createPoint(Long teacherId, PointCreateRequest request) {
        // TODO: 校验 teacherId 是否为 courseId 的所属教师（通过 Course 服务 Feign 调用）
        // 如果有 parentId，校验父知识点存在且为一级知识点
        if (request.getParentId() != null) {
            KnowledgePoint parent = getById(request.getParentId());
            ThrowUtils.throwIf(parent == null, ErrorCode.NOT_FOUND_ERROR, "父知识点不存在");
            ThrowUtils.throwIf(parent.getParentId() != null,
                    ErrorCode.PARAMS_ERROR, "不支持三级知识点，父知识点必须是一级知识点");
            ThrowUtils.throwIf(!parent.getCourseId().equals(request.getCourseId()),
                    ErrorCode.PARAMS_ERROR, "父知识点不属于该课程");
        }

        // 校验同课程同层级名称唯一
        checkNameUnique(request.getCourseId(), request.getParentId(), request.getPointName(), null);

        KnowledgePoint point = new KnowledgePoint();
        point.setPointName(request.getPointName());
        point.setCourseId(request.getCourseId());
        point.setParentId(request.getParentId());
        point.setDescription(request.getDescription());
        point.setOrderIndex(request.getOrderIndex() != null ? request.getOrderIndex() : 0);
        save(point);

        log.info("Knowledge point created, pointId={}, courseId={}", point.getPointId(), request.getCourseId());
        return toVO(point);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public KnowledgePointVO updatePoint(Long teacherId, Long pointId, PointUpdateRequest request) {
        KnowledgePoint point = getById(pointId);
        ThrowUtils.throwIf(point == null, ErrorCode.NOT_FOUND_ERROR, "知识点不存在");

        if (request.getPointName() != null) {
            checkNameUnique(point.getCourseId(), point.getParentId(), request.getPointName(), pointId);
            point.setPointName(request.getPointName());
        }
        if (request.getDescription() != null) {
            point.setDescription(request.getDescription());
        }
        if (request.getOrderIndex() != null) {
            point.setOrderIndex(request.getOrderIndex());
        }
        updateById(point);

        return toVO(point);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePoint(Long teacherId, Long pointId) {
        KnowledgePoint point = getById(pointId);
        ThrowUtils.throwIf(point == null, ErrorCode.NOT_FOUND_ERROR, "知识点不存在");

        boolean isRoot = point.getParentId() == null;
        if (isRoot) {
            // 一级知识点：校验下面没有二级知识点
            long childCount = count(new LambdaQueryWrapper<KnowledgePoint>()
                    .eq(KnowledgePoint::getParentId, pointId));
            ThrowUtils.throwIf(childCount > 0, ErrorCode.OPERATION_ERROR,
                    "该知识点下存在二级知识点，请先删除所有二级知识点");
        } else {
            // 二级知识点：自动清除与章节、题目的关联
            knowledgeSectionMapper.delete(new LambdaQueryWrapper<com.gpnu.knowledge.model.entity.KnowledgeSection>()
                    .eq(com.gpnu.knowledge.model.entity.KnowledgeSection::getPointId, pointId));
            questionKnowledgeMapper.delete(new LambdaQueryWrapper<QuestionKnowledge>()
                    .eq(QuestionKnowledge::getPointId, pointId));
        }

        removeById(pointId);
        log.info("Knowledge point deleted, pointId={}", pointId);
    }

    @Override
    public List<KnowledgeTreeVO> getPointTree(Long courseId) {
        List<KnowledgePoint> points = list(new LambdaQueryWrapper<KnowledgePoint>()
                .eq(KnowledgePoint::getCourseId, courseId)
                .orderByAsc(KnowledgePoint::getOrderIndex));

        // 分离一级和二级知识点
        List<KnowledgePoint> level1 = points.stream()
                .filter(p -> p.getParentId() == null)
                .toList();

        Map<Long, List<KnowledgePoint>> childrenMap = points.stream()
                .filter(p -> p.getParentId() != null)
                .collect(Collectors.groupingBy(KnowledgePoint::getParentId));

        List<KnowledgeTreeVO> roots = new ArrayList<>();
        for (KnowledgePoint p : level1) {
            KnowledgeTreeVO root = toTreeVO(p);
            List<KnowledgePoint> children = childrenMap.getOrDefault(p.getPointId(), List.of());
            root.setChildren(children.stream().map(this::toTreeVO).toList());
            roots.add(root);
        }
        return roots;
    }

    @Override
    public List<KnowledgeTreeDTO> getPointTreeDTO(Long courseId) {
        List<KnowledgePoint> points = list(new LambdaQueryWrapper<KnowledgePoint>()
                .eq(KnowledgePoint::getCourseId, courseId)
                .orderByAsc(KnowledgePoint::getOrderIndex));

        // 分离一级和二级知识点
        List<KnowledgePoint> level1 = points.stream()
                .filter(p -> p.getParentId() == null)
                .toList();

        Map<Long, List<KnowledgePoint>> childrenMap = points.stream()
                .filter(p -> p.getParentId() != null)
                .collect(Collectors.groupingBy(KnowledgePoint::getParentId));

        List<KnowledgeTreeDTO> roots = new ArrayList<>();
        for (KnowledgePoint p : level1) {
            KnowledgeTreeDTO root = toTreeDTO(p);
            List<KnowledgePoint> children = childrenMap.getOrDefault(p.getPointId(), List.of());
            root.setChildren(children.stream().map(this::toTreeDTO).toList());
            roots.add(root);
        }
        return roots;
    }

    @Override
    public List<PointSimpleVO> getPointBatch(List<Long> pointIds) {
        if (pointIds == null || pointIds.isEmpty()) {
            return List.of();
        }
        List<KnowledgePoint> points = listByIds(new HashSet<>(pointIds));
        return points.stream().map(this::toSimpleVO).toList();
    }

    @Override
    public List<PointSimpleDTO> getPointBatchDTO(List<Long> pointIds) {
        if (pointIds == null || pointIds.isEmpty()) {
            return List.of();
        }
        List<KnowledgePoint> points = listByIds(new HashSet<>(pointIds));
        return points.stream().map(this::toSimpleDTO).toList();
    }

    @Override
    public List<Long> getQuestionIdsByPoint(Long pointId) {
        List<QuestionKnowledge> records = questionKnowledgeMapper.selectList(
                new LambdaQueryWrapper<QuestionKnowledge>()
                        .eq(QuestionKnowledge::getPointId, pointId));
        return records.stream().map(QuestionKnowledge::getQuestionId).toList();
    }

    @Override
    public Map<Long, List<PointSimpleVO>> getPointsByQuestions(List<Long> questionIds) {
        if (questionIds == null || questionIds.isEmpty()) {
            return Map.of();
        }
        List<QuestionKnowledge> records = questionKnowledgeMapper.selectList(
                new LambdaQueryWrapper<QuestionKnowledge>()
                        .in(QuestionKnowledge::getQuestionId, questionIds));

        // 获取所有关联的 pointId
        List<Long> pointIds = records.stream()
                .map(QuestionKnowledge::getPointId).distinct().toList();
        List<PointSimpleVO> simples = getPointBatch(pointIds);
        Map<Long, PointSimpleVO> simpleMap = simples.stream()
                .collect(Collectors.toMap(PointSimpleVO::getPointId, s -> s));

        // 按 questionId 分组
        return records.stream()
                .collect(Collectors.groupingBy(
                        QuestionKnowledge::getQuestionId,
                        Collectors.mapping(
                                r -> simpleMap.get(r.getPointId()),
                                Collectors.toList())));
    }

    @Override
    public Map<Long, List<PointSimpleDTO>> getPointsByQuestionsDTO(List<Long> questionIds) {
        if (questionIds == null || questionIds.isEmpty()) {
            return Map.of();
        }
        List<QuestionKnowledge> records = questionKnowledgeMapper.selectList(
                new LambdaQueryWrapper<QuestionKnowledge>()
                        .in(QuestionKnowledge::getQuestionId, questionIds));

        // 获取所有关联的 pointId
        List<Long> pointIds = records.stream()
                .map(QuestionKnowledge::getPointId).distinct().toList();
        List<PointSimpleDTO> simples = getPointBatchDTO(pointIds);
        Map<Long, PointSimpleDTO> simpleMap = simples.stream()
                .collect(Collectors.toMap(PointSimpleDTO::getPointId, s -> s));

        // 按 questionId 分组
        return records.stream()
                .collect(Collectors.groupingBy(
                        QuestionKnowledge::getQuestionId,
                        Collectors.mapping(
                                r -> simpleMap.get(r.getPointId()),
                                Collectors.toList())));
    }

    // ==================== 私有方法 ====================

    private void checkNameUnique(Long courseId, Long parentId, String pointName, Long excludeId) {
        LambdaQueryWrapper<KnowledgePoint> wrapper = new LambdaQueryWrapper<KnowledgePoint>()
                .eq(KnowledgePoint::getCourseId, courseId)
                .eq(KnowledgePoint::getPointName, pointName);

        if (parentId == null) {
            wrapper.isNull(KnowledgePoint::getParentId);
        } else {
            wrapper.eq(KnowledgePoint::getParentId, parentId);
        }
        if (excludeId != null) {
            wrapper.ne(KnowledgePoint::getPointId, excludeId);
        }

        boolean exists = exists(wrapper);
        ThrowUtils.throwIf(exists, ErrorCode.OPERATION_ERROR, "同层级下已存在同名知识点");
    }

    private KnowledgePointVO toVO(KnowledgePoint point) {
        KnowledgePointVO vo = new KnowledgePointVO();
        BeanUtil.copyProperties(point, vo);
        return vo;
    }

    private KnowledgeTreeVO toTreeVO(KnowledgePoint point) {
        KnowledgeTreeVO vo = new KnowledgeTreeVO();
        BeanUtil.copyProperties(point, vo);
        return vo;
    }

    private KnowledgeTreeDTO toTreeDTO(KnowledgePoint point) {
        KnowledgeTreeDTO dto = new KnowledgeTreeDTO();
        BeanUtil.copyProperties(point, dto);
        return dto;
    }

    private PointSimpleVO toSimpleVO(KnowledgePoint point) {
        PointSimpleVO vo = new PointSimpleVO();
        vo.setPointId(point.getPointId());
        vo.setPointName(point.getPointName());
        vo.setParentId(point.getParentId());
        vo.setLevel(point.getParentId() == null ? 1 : 2);
        return vo;
    }

    private PointSimpleDTO toSimpleDTO(KnowledgePoint point) {
        PointSimpleDTO dto = new PointSimpleDTO();
        dto.setPointId(point.getPointId());
        dto.setPointName(point.getPointName());
        dto.setParentId(point.getParentId());
        dto.setLevel(point.getParentId() == null ? 1 : 2);
        return dto;
    }
}
