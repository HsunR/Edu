package com.gpnu.learning.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gpnu.api.client.knowledge.KnowledgeFeignClient;
import com.gpnu.api.dto.knowledge.PointSimpleDTO;
import com.gpnu.learning.mapper.LpMasteryMapper;
import com.gpnu.learning.mapper.LpWrongPointMapper;
import com.gpnu.learning.mapper.LpWrongRecordMapper;
import com.gpnu.learning.model.dto.WrongRecordQueryRequest;
import com.gpnu.learning.model.dto.WrongStatsQueryRequest;
import com.gpnu.learning.model.entity.LpMastery;
import com.gpnu.learning.model.entity.LpWrongPoint;
import com.gpnu.learning.model.entity.LpWrongRecord;
import com.gpnu.learning.model.vo.*;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 学情查询/富化共享支撑类
 */
@Component
public class LearningQuerySupport {

    @Resource
    private LpMasteryMapper lpMasteryMapper;

    @Resource
    private LpWrongRecordMapper lpWrongRecordMapper;

    @Resource
    private LpWrongPointMapper lpWrongPointMapper;

    @Resource
    private KnowledgeFeignClient knowledgeFeignClient;

    // ==================== 掌握度查询====================

    public List<LpMastery> listByStudentAndClass(Long studentId, Long classId) {
        return lpMasteryMapper.selectList(new LambdaQueryWrapper<LpMastery>()
                .eq(LpMastery::getStudentId, studentId)
                .eq(LpMastery::getClassId, classId)
                .orderByAsc(LpMastery::getMasteryLevel)
                .orderByAsc(LpMastery::getPointId));
    }

    public List<MasteryOverviewVO> listOverview(Long studentId, Long classId) {
        List<LpMastery> masteries = listByStudentAndClass(studentId, classId);
        List<Long> pointIds = masteries.stream().map(LpMastery::getPointId).toList();
        Map<Long, PointSimpleDTO> pointMap = loadPointMap(pointIds);

        return masteries.stream().map(mastery -> {
            MasteryOverviewVO vo = new MasteryOverviewVO();
            BeanUtils.copyProperties(mastery, vo);
            PointSimpleDTO point = pointMap.get(mastery.getPointId());
            if (point != null) {
                vo.setPointName(point.getPointName());
            }
            return vo;
        }).sorted(Comparator.comparing(MasteryOverviewVO::getMasteryLevel)).toList();
    }

    public List<MasteryOverviewVO> listWeakPoints(Long studentId, Long classId, int threshold) {
        return listOverview(studentId, classId).stream()
                .filter(item -> item.getMasteryLevel() != null && item.getMasteryLevel() < threshold)
                .toList();
    }

    // ==================== 错题查询====================

    public Page<WrongRecordVO> pageWrongRecords(Long studentId, WrongRecordQueryRequest request) {
        LambdaQueryWrapper<LpWrongRecord> wrapper = new LambdaQueryWrapper<LpWrongRecord>()
                .eq(LpWrongRecord::getStudentId, studentId)
                .eq(request.getClassId() != null, LpWrongRecord::getClassId, request.getClassId())
                .eq(request.getCourseId() != null, LpWrongRecord::getCourseId, request.getCourseId())
                .eq(request.getQuestionType() != null, LpWrongRecord::getQuestionType, request.getQuestionType())
                .eq(request.getIsResolved() != null, LpWrongRecord::getIsResolved, request.getIsResolved())
                .orderByDesc(LpWrongRecord::getLastWrongAt);

        Page<LpWrongRecord> page = lpWrongRecordMapper.selectPage(
                new Page<>(request.getCurrent(), request.getPageSize()), wrapper);

        Page<WrongRecordVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(toWrongRecordVOList(page.getRecords()));
        return voPage;
    }

    public WrongStatsVO statWrongRecords(Long studentId, WrongStatsQueryRequest request) {
        List<WrongTypeStatVO> byType = lpWrongRecordMapper.statByQuestionType(
                studentId, request.getClassId(), request.getCourseId());
        List<WrongPointStatVO> byPoint = lpWrongRecordMapper.statByKnowledgePoint(
                studentId, request.getClassId(), request.getCourseId());

        List<Long> pointIds = byPoint.stream()
                .map(WrongPointStatVO::getPointId)
                .filter(Objects::nonNull)
                .toList();
        Map<Long, PointSimpleDTO> pointMap = loadPointMap(pointIds);
        byPoint.forEach(item -> {
            PointSimpleDTO point = pointMap.get(item.getPointId());
            if (point != null) {
                item.setPointName(point.getPointName());
            }
        });

        WrongStatsVO stats = new WrongStatsVO();
        stats.setByQuestionType(byType);
        stats.setByKnowledgePoint(byPoint);
        return stats;
    }

    public LpWrongRecord getOwnedWrongRecord(Long studentId, Long wrongId) {
        return lpWrongRecordMapper.selectOne(new LambdaQueryWrapper<LpWrongRecord>()
                .eq(LpWrongRecord::getWrongId, wrongId)
                .eq(LpWrongRecord::getStudentId, studentId));
    }

    public WrongRecordVO toWrongRecordVO(LpWrongRecord record) {
        if (record == null) {
            return null;
        }
        List<WrongRecordVO> records = toWrongRecordVOList(List.of(record));
        return records.isEmpty() ? null : records.get(0);
    }

    private List<WrongRecordVO> toWrongRecordVOList(List<LpWrongRecord> records) {
        if (CollectionUtils.isEmpty(records)) {
            return List.of();
        }

        List<Long> wrongIds = records.stream().map(LpWrongRecord::getWrongId).toList();
        List<LpWrongPoint> wrongPoints = lpWrongPointMapper.selectList(
                new LambdaQueryWrapper<LpWrongPoint>().in(LpWrongPoint::getWrongId, wrongIds));

        Map<Long, List<LpWrongPoint>> pointGroup = wrongPoints.stream()
                .collect(Collectors.groupingBy(LpWrongPoint::getWrongId));

        List<Long> pointIds = wrongPoints.stream().map(LpWrongPoint::getPointId).distinct().toList();
        Map<Long, PointSimpleDTO> pointMap = loadPointMap(pointIds);

        return records.stream().map(record -> {
            WrongRecordVO vo = new WrongRecordVO();
            BeanUtils.copyProperties(record, vo);
            List<WrongPointBriefVO> points = pointGroup.getOrDefault(record.getWrongId(), List.of()).stream()
                    .map(item -> toPointBrief(item.getPointId(), pointMap))
                    .toList();
            vo.setPoints(points);
            return vo;
        }).toList();
    }

    // ==================== 知识点名称富化 ====================

    public Map<Long, PointSimpleDTO> loadPointMap(List<Long> pointIds) {
        if (CollectionUtils.isEmpty(pointIds)) {
            return Collections.emptyMap();
        }
        List<Long> distinctIds = pointIds.stream().distinct().toList();
        List<PointSimpleDTO> points = knowledgeFeignClient.getPointBatch(distinctIds);
        if (CollectionUtils.isEmpty(points)) {
            return Collections.emptyMap();
        }
        return points.stream()
                .filter(point -> point.getPointId() != null)
                .collect(Collectors.toMap(PointSimpleDTO::getPointId, Function.identity(), (a, b) -> a));
    }

    public WrongPointBriefVO toPointBrief(Long pointId, Map<Long, PointSimpleDTO> pointMap) {
        WrongPointBriefVO brief = new WrongPointBriefVO();
        brief.setPointId(pointId);
        PointSimpleDTO point = pointMap.get(pointId);
        if (point != null) {
            brief.setPointName(point.getPointName());
        }
        return brief;
    }
}