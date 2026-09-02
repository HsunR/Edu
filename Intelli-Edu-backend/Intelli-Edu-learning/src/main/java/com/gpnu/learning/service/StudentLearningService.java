package com.gpnu.learning.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.exception.ThrowUtils;
import com.gpnu.learning.config.LearningProperties;
import com.gpnu.learning.mapper.LpWrongRecordMapper;
import com.gpnu.learning.model.dto.WrongRecordQueryRequest;
import com.gpnu.learning.model.dto.WrongStatsQueryRequest;
import com.gpnu.learning.model.entity.LpWrongRecord;
import com.gpnu.learning.model.vo.MasteryOverviewVO;
import com.gpnu.learning.model.vo.WrongRecordVO;
import com.gpnu.learning.model.vo.WrongStatsVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class StudentLearningService {

    @Resource
    private MasteryQueryService masteryQueryService;

    @Resource
    private WrongRecordQueryService wrongRecordQueryService;

    @Resource
    private LpWrongRecordMapper lpWrongRecordMapper;

    @Resource
    private ClassAccessService classAccessService;

    @Resource
    private LearningProperties learningProperties;

    public List<MasteryOverviewVO> getMasteryOverview(Long studentId, Long classId) {
        validateClassMember(studentId, classId);
        return masteryQueryService.listOverview(studentId, classId);
    }

    public List<MasteryOverviewVO> getWeakPoints(Long studentId, Long classId) {
        validateClassMember(studentId, classId);
        return masteryQueryService.listWeakPoints(studentId, classId, learningProperties.getWeakMasteryThreshold());
    }

    public Page<WrongRecordVO> pageWrongRecords(Long studentId, WrongRecordQueryRequest request) {
        if (request.getClassId() != null) {
            validateClassMember(studentId, request.getClassId());
        }
        return wrongRecordQueryService.pageWrongRecords(studentId, request);
    }

    public WrongStatsVO getWrongStats(Long studentId, WrongStatsQueryRequest request) {
        if (request.getClassId() == null && request.getCourseId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "classId 与 courseId 至少传一个");
        }
        if (request.getClassId() != null) {
            validateClassMember(studentId, request.getClassId());
        }
        return wrongRecordQueryService.statWrongRecords(studentId, request);
    }

    @Transactional(rollbackFor = Exception.class)
    public void resolveWrongRecord(Long studentId, Long wrongId) {
        LpWrongRecord wrongRecord = wrongRecordQueryService.getOwnedWrongRecord(studentId, wrongId);
        ThrowUtils.throwIf(wrongRecord == null, ErrorCode.NOT_FOUND_ERROR, "错题记录不存在");

        if (Integer.valueOf(1).equals(wrongRecord.getIsResolved())) {
            return;
        }

        wrongRecord.setIsResolved(1);
        wrongRecord.setResolvedAt(OffsetDateTime.now());
        lpWrongRecordMapper.updateById(wrongRecord);
    }

    private void validateClassMember(Long studentId, Long classId) {
        classAccessService.validateStudentMember(studentId, classId);
    }
}
