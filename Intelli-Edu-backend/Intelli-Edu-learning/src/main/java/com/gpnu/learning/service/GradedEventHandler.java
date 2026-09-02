package com.gpnu.learning.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gpnu.api.dto.knowledge.PointSimpleDTO;
import com.gpnu.api.event.SheetGradedEvent;
import com.gpnu.api.client.knowledge.KnowledgeFeignClient;
import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.learning.mapper.*;
import com.gpnu.learning.model.dto.SheetContributionSnapshot;
import com.gpnu.learning.model.entity.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.*;

@Service
@Slf4j
public class GradedEventHandler {

    @Resource
    private KnowledgeFeignClient knowledgeFeignClient;

    @Resource
    private LpMasteryMapper lpMasteryMapper;

    @Resource
    private LpWrongRecordMapper lpWrongRecordMapper;

    @Resource
    private LpWrongPointMapper lpWrongPointMapper;

    @Resource
    private LpSheetGradedLogMapper lpSheetGradedLogMapper;

    @Resource
    private ObjectMapper objectMapper;

    @Transactional(rollbackFor = Exception.class)
    public void handle(SheetGradedEvent event) {
        validateEvent(event);

        LpSheetGradedLog existingLog = lpSheetGradedLogMapper.selectOne(
                new LambdaQueryWrapper<LpSheetGradedLog>()
                        .eq(LpSheetGradedLog::getSheetId, event.getSheetId()));

        if (existingLog != null) {
            if (Objects.equals(existingLog.getSubmitCount(), event.getSubmitCount())) {
                log.info("答卷批改事件已处理，跳过: sheetId={}, submitCount={}",
                        event.getSheetId(), event.getSubmitCount());
                return;
            }
            if (existingLog.getSubmitCount() > event.getSubmitCount()) {
                log.warn("收到过期批改事件，跳过: sheetId={}, eventSubmitCount={}, processedSubmitCount={}",
                        event.getSheetId(), event.getSubmitCount(), existingLog.getSubmitCount());
                return;
            }
            rollbackContribution(deserializeSnapshot(existingLog.getContributionJson()),
                    event.getStudentId(), event.getClassId());
        }

        SheetContributionSnapshot snapshot = applyContribution(event);
        persistProcessLog(event, snapshot, existingLog);
    }

    private void validateEvent(SheetGradedEvent event) {
        if (event.getSheetId() == null || event.getStudentId() == null
                || event.getClassId() == null || event.getCourseId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "SheetGradedEvent 缺少必要字段");
        }
        if (event.getSubmitCount() == null || event.getSubmitCount() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "SheetGradedEvent.submitCount 无效");
        }
        if (CollectionUtils.isEmpty(event.getRecords())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "SheetGradedEvent.records 不能为空");
        }
    }

    private SheetContributionSnapshot applyContribution(SheetGradedEvent event) {
        List<Long> questionIds = event.getRecords().stream()
                .map(SheetGradedEvent.RecordItem::getQuestionId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, List<PointSimpleDTO>> questionPointMap = Collections.emptyMap();
        if (!questionIds.isEmpty()) {
            questionPointMap = knowledgeFeignClient.getPointsByQuestions(questionIds);
            if (questionPointMap == null) {
                questionPointMap = Collections.emptyMap();
            }
        }

        SheetContributionSnapshot snapshot = new SheetContributionSnapshot();
        OffsetDateTime practiceAt = event.getGradedAt() != null ? event.getGradedAt() : OffsetDateTime.now();

        for (SheetGradedEvent.RecordItem record : event.getRecords()) {
            if (record.getQuestionId() == null) {
                continue;
            }
            BigDecimal fullScore = defaultZero(record.getFullScore());
            BigDecimal earnedScore = defaultZero(record.getEarnedScore());
            List<PointSimpleDTO> points = questionPointMap.getOrDefault(record.getQuestionId(), List.of());
            List<Long> pointIds = points.stream().map(PointSimpleDTO::getPointId).filter(Objects::nonNull).toList();

            boolean isCorrect = fullScore.compareTo(BigDecimal.ZERO) > 0
                    && earnedScore.compareTo(fullScore) >= 0;

            for (Long pointId : pointIds) {
                applyMasteryDelta(event, pointId, fullScore, earnedScore, isCorrect, practiceAt, snapshot);
            }

            if (earnedScore.compareTo(fullScore) < 0) {
                applyWrongRecord(event, record, pointIds, snapshot);
            }
        }
        return snapshot;
    }

    private void applyMasteryDelta(SheetGradedEvent event, Long pointId,
                                   BigDecimal fullScore, BigDecimal earnedScore,
                                   boolean isCorrect, OffsetDateTime practiceAt,
                                   SheetContributionSnapshot snapshot) {
        LpMastery mastery = lpMasteryMapper.selectOne(
                new LambdaQueryWrapper<LpMastery>()
                        .eq(LpMastery::getStudentId, event.getStudentId())
                        .eq(LpMastery::getClassId, event.getClassId())
                        .eq(LpMastery::getPointId, pointId));

        if (mastery == null) {
            mastery = new LpMastery();
            mastery.setStudentId(event.getStudentId());
            mastery.setClassId(event.getClassId());
            mastery.setCourseId(event.getCourseId());
            mastery.setPointId(pointId);
            mastery.setTotalScore(BigDecimal.ZERO);
            mastery.setEarnedScore(BigDecimal.ZERO);
            mastery.setAnswerCount(0);
            mastery.setCorrectCount(0);
            mastery.setMasteryLevel(0);
        }

        mastery.setTotalScore(mastery.getTotalScore().add(fullScore));
        mastery.setEarnedScore(mastery.getEarnedScore().add(earnedScore));
        mastery.setAnswerCount(mastery.getAnswerCount() + 1);
        if (isCorrect) {
            mastery.setCorrectCount(mastery.getCorrectCount() + 1);
        }
        mastery.setMasteryLevel(calculateMasteryLevel(mastery.getTotalScore(), mastery.getEarnedScore()));
        mastery.setLastPracticeAt(practiceAt);

        if (mastery.getMasteryId() == null) {
            lpMasteryMapper.insert(mastery);
        } else {
            lpMasteryMapper.updateById(mastery);
        }

        SheetContributionSnapshot.MasteryContribution contribution = new SheetContributionSnapshot.MasteryContribution();
        contribution.setPointId(pointId);
        contribution.setTotalScoreDelta(fullScore);
        contribution.setEarnedScoreDelta(earnedScore);
        contribution.setAnswerCountDelta(1);
        contribution.setCorrectCountDelta(isCorrect ? 1 : 0);
        snapshot.getMasteries().add(contribution);
    }

    private void applyWrongRecord(SheetGradedEvent event, SheetGradedEvent.RecordItem record,
                                  List<Long> pointIds, SheetContributionSnapshot snapshot) {
        LpWrongRecord wrongRecord = lpWrongRecordMapper.selectOne(
                new LambdaQueryWrapper<LpWrongRecord>()
                        .eq(LpWrongRecord::getStudentId, event.getStudentId())
                        .eq(LpWrongRecord::getClassId, event.getClassId())
                        .eq(LpWrongRecord::getQuestionId, record.getQuestionId()));

        boolean created = wrongRecord == null;
        if (created) {
            wrongRecord = new LpWrongRecord();
            wrongRecord.setStudentId(event.getStudentId());
            wrongRecord.setClassId(event.getClassId());
            wrongRecord.setCourseId(event.getCourseId());
            wrongRecord.setQuestionId(record.getQuestionId());
            wrongRecord.setWrongCount(0);
            wrongRecord.setIsResolved(0);
        }

        wrongRecord.setRecordId(record.getRecordId());
        wrongRecord.setExamId(event.getExamId());
        wrongRecord.setQuestionType(record.getQuestionType());
        wrongRecord.setFullScore(record.getFullScore());
        wrongRecord.setEarnedScore(record.getEarnedScore());
        wrongRecord.setWrongType(record.getAiWrongType());
        wrongRecord.setLastWrongAt(event.getGradedAt() != null ? event.getGradedAt() : OffsetDateTime.now());
        wrongRecord.setWrongCount(wrongRecord.getWrongCount() + 1);

        if (created) {
            lpWrongRecordMapper.insert(wrongRecord);
        } else {
            lpWrongRecordMapper.updateById(wrongRecord);
        }

        syncWrongPoints(wrongRecord, pointIds, event);

        SheetContributionSnapshot.WrongContribution contribution = new SheetContributionSnapshot.WrongContribution();
        contribution.setQuestionId(record.getQuestionId());
        contribution.setWrongCountDelta(1);
        contribution.setWrongId(wrongRecord.getWrongId());
        contribution.setPointIds(new ArrayList<>(pointIds));
        snapshot.getWrongs().add(contribution);
    }

    private void syncWrongPoints(LpWrongRecord wrongRecord, List<Long> pointIds, SheetGradedEvent event) {
        lpWrongPointMapper.delete(new LambdaQueryWrapper<LpWrongPoint>()
                .eq(LpWrongPoint::getWrongId, wrongRecord.getWrongId()));

        for (Long pointId : pointIds) {
            LpWrongPoint wrongPoint = new LpWrongPoint();
            wrongPoint.setWrongId(wrongRecord.getWrongId());
            wrongPoint.setStudentId(event.getStudentId());
            wrongPoint.setClassId(event.getClassId());
            wrongPoint.setCourseId(event.getCourseId());
            wrongPoint.setPointId(pointId);
            lpWrongPointMapper.insert(wrongPoint);
        }
    }

    private void rollbackContribution(SheetContributionSnapshot snapshot,
                                      Long studentId, Long classId) {
        if (snapshot == null) {
            return;
        }

        for (SheetContributionSnapshot.MasteryContribution contribution : snapshot.getMasteries()) {
            LpMastery mastery = lpMasteryMapper.selectOne(
                    new LambdaQueryWrapper<LpMastery>()
                            .eq(LpMastery::getStudentId, studentId)
                            .eq(LpMastery::getClassId, classId)
                            .eq(LpMastery::getPointId, contribution.getPointId()));
            if (mastery == null) {
                continue;
            }

            mastery.setTotalScore(mastery.getTotalScore().subtract(defaultZero(contribution.getTotalScoreDelta())));
            mastery.setEarnedScore(mastery.getEarnedScore().subtract(defaultZero(contribution.getEarnedScoreDelta())));
            mastery.setAnswerCount(Math.max(0, mastery.getAnswerCount() - contribution.getAnswerCountDelta()));
            mastery.setCorrectCount(Math.max(0, mastery.getCorrectCount() - contribution.getCorrectCountDelta()));
            mastery.setMasteryLevel(calculateMasteryLevel(mastery.getTotalScore(), mastery.getEarnedScore()));
            lpMasteryMapper.updateById(mastery);
        }

        for (SheetContributionSnapshot.WrongContribution contribution : snapshot.getWrongs()) {
            LpWrongRecord wrongRecord = lpWrongRecordMapper.selectById(contribution.getWrongId());
            if (wrongRecord == null) {
                wrongRecord = lpWrongRecordMapper.selectOne(
                        new LambdaQueryWrapper<LpWrongRecord>()
                                .eq(LpWrongRecord::getStudentId, studentId)
                                .eq(LpWrongRecord::getClassId, classId)
                                .eq(LpWrongRecord::getQuestionId, contribution.getQuestionId()));
            }
            if (wrongRecord == null) {
                continue;
            }

            int newCount = wrongRecord.getWrongCount() - contribution.getWrongCountDelta();
            if (newCount <= 0) {
                lpWrongPointMapper.delete(new LambdaQueryWrapper<LpWrongPoint>()
                        .eq(LpWrongPoint::getWrongId, wrongRecord.getWrongId()));
                lpWrongRecordMapper.deleteById(wrongRecord.getWrongId());
            } else {
                wrongRecord.setWrongCount(newCount);
                lpWrongRecordMapper.updateById(wrongRecord);
            }
        }
    }

    private void persistProcessLog(SheetGradedEvent event, SheetContributionSnapshot snapshot,
                                   LpSheetGradedLog existingLog) {
        String json = serializeSnapshot(snapshot);
        if (existingLog == null) {
            LpSheetGradedLog logEntity = new LpSheetGradedLog();
            logEntity.setSheetId(event.getSheetId());
            logEntity.setSubmitCount(event.getSubmitCount());
            logEntity.setStudentId(event.getStudentId());
            logEntity.setClassId(event.getClassId());
            logEntity.setCourseId(event.getCourseId());
            logEntity.setContributionJson(json);
            lpSheetGradedLogMapper.insert(logEntity);
        } else {
            existingLog.setSubmitCount(event.getSubmitCount());
            existingLog.setContributionJson(json);
            lpSheetGradedLogMapper.updateById(existingLog);
        }
    }

    private int calculateMasteryLevel(BigDecimal totalScore, BigDecimal earnedScore) {
        if (totalScore == null || totalScore.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        return earnedScore.multiply(BigDecimal.valueOf(100))
                .divide(totalScore, 0, RoundingMode.HALF_UP)
                .intValue();
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String serializeSnapshot(SheetContributionSnapshot snapshot) {
        try {
            return objectMapper.writeValueAsString(snapshot);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "贡献快照序列化失败");
        }
    }

    private SheetContributionSnapshot deserializeSnapshot(String json) {
        if (json == null || json.isBlank()) {
            return new SheetContributionSnapshot();
        }
        try {
            return objectMapper.readValue(json, SheetContributionSnapshot.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "贡献快照反序列化失败");
        }
    }
}
