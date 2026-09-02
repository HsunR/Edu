package com.gpnu.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gpnu.api.dto.learning.LearningMasteryDTO;
import com.gpnu.api.dto.learning.LearningPageDTO;
import com.gpnu.api.dto.learning.LearningProfileSummaryDTO;
import com.gpnu.api.dto.learning.LearningWrongPointDTO;
import com.gpnu.api.dto.learning.LearningWrongQuestionDetailDTO;
import com.gpnu.api.dto.learning.LearningWrongRecordDTO;
import com.gpnu.api.dto.learning.LearningWrongStudentDTO;
import com.gpnu.api.dto.learning.LearningWrongTypeDistDTO;
import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.exception.ThrowUtils;
import com.gpnu.learning.config.LearningProperties;
import com.gpnu.learning.mapper.LpWrongRecordMapper;
import com.gpnu.learning.model.dto.WrongRecordQueryRequest;
import com.gpnu.learning.model.entity.LpWrongRecord;
import com.gpnu.learning.model.vo.MasteryOverviewVO;
import com.gpnu.learning.model.vo.WrongPointBriefVO;
import com.gpnu.learning.model.vo.WrongQuestionDetailVO;
import com.gpnu.learning.model.vo.WrongRecordVO;
import com.gpnu.learning.model.vo.WrongStudentBriefVO;
import com.gpnu.learning.model.vo.WrongTypeDistVO;
import com.gpnu.learning.service.IInnerLearningService;
import com.gpnu.learning.service.ITeacherLearningService;
import com.gpnu.learning.service.LearningQuerySupport;
import com.gpnu.learning.service.recommend.WeakPointDetector;
import com.gpnu.learning.service.recommend.WeakPointItem;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;

/**
 * 对内 Feign 学情查询：供 AI 模块 RAG，不写推荐排序逻辑。
 */
@Service
public class InnerLearningServiceImpl implements IInnerLearningService {

    @Resource
    private LearningQuerySupport learningQuerySupport;

    @Resource
    private ITeacherLearningService teacherLearningService;

    @Resource
    private WeakPointDetector weakPointDetector;

    @Resource
    private LpWrongRecordMapper lpWrongRecordMapper;

    @Resource
    private LearningProperties learningProperties;

    @Override
    public LearningProfileSummaryDTO buildProfileSummary(String studentId, String classId) {
        Long studentIdValue = parseRequiredId(studentId, "studentId");
        Long classIdValue = parseRequiredId(classId, "classId");
        LearningProfileSummaryDTO dto = new LearningProfileSummaryDTO();
        dto.setStudentId(studentId);
        dto.setClassId(classId);

        List<MasteryOverviewVO> overview = learningQuerySupport.listOverview(studentIdValue, classIdValue);
        if (!overview.isEmpty()) {
            int sum = overview.stream()
                    .map(MasteryOverviewVO::getMasteryLevel)
                    .filter(l -> l != null)
                    .mapToInt(Integer::intValue)
                    .sum();
            long count = overview.stream().map(MasteryOverviewVO::getMasteryLevel).filter(l -> l != null).count();
            if (count > 0) {
                dto.setAvgMastery((int) Math.round(sum / (double) count));
            }
        }

        List<WeakPointItem> weakPoints = weakPointDetector.detectWeakPoints(studentIdValue, classIdValue, 5);
        dto.setWeakPointCount(weakPoints.size());
        for (WeakPointItem item : weakPoints) {
            LearningProfileSummaryDTO.WeakPointBrief brief = new LearningProfileSummaryDTO.WeakPointBrief();
            brief.setPointId(toIdString(item.getPointId()));
            brief.setPointName(item.getPointName());
            brief.setMasteryLevel(item.getMasteryLevel());
            dto.getWeakPoints().add(brief);
        }

        List<LpWrongRecord> wrongs = lpWrongRecordMapper.selectList(new LambdaQueryWrapper<LpWrongRecord>()
                .eq(LpWrongRecord::getStudentId, studentIdValue)
                .eq(LpWrongRecord::getClassId, classIdValue)
                .eq(LpWrongRecord::getIsResolved, 0)
                .orderByDesc(LpWrongRecord::getWrongCount));

        dto.setUnresolvedWrongCount(wrongs.size());

        wrongs.stream().sorted(Comparator.comparing(LpWrongRecord::getWrongCount,
                Comparator.nullsLast(Comparator.reverseOrder()))).limit(5).forEach(w -> {
            LearningProfileSummaryDTO.WrongBrief brief = new LearningProfileSummaryDTO.WrongBrief();
            brief.setQuestionId(toIdString(w.getQuestionId()));
            brief.setWrongCount(w.getWrongCount());
            brief.setWrongType(w.getWrongType());
            brief.setIsResolved(w.getIsResolved());
            dto.getTopWrongs().add(brief);
        });

        return dto;
    }

    @Override
    public List<LearningMasteryDTO> listMastery(String studentId, String classId, String courseId) {
        Long studentIdValue = parseRequiredId(studentId, "studentId");
        Long classIdValue = parseRequiredId(classId, "classId");
        Long courseIdValue = parseOptionalId(courseId, "courseId");
        return learningQuerySupport.listOverview(studentIdValue, classIdValue).stream()
                .filter(item -> courseIdValue == null || courseIdValue.equals(item.getCourseId()))
                .map(item -> toMasteryDTO(studentIdValue, classIdValue, item))
                .toList();
    }

    @Override
    public List<LearningMasteryDTO> listWeakPoints(String studentId, String classId, String courseId, Integer threshold) {
        Long studentIdValue = parseRequiredId(studentId, "studentId");
        Long classIdValue = parseRequiredId(classId, "classId");
        Long courseIdValue = parseOptionalId(courseId, "courseId");
        int resolvedThreshold = threshold != null ? threshold : learningProperties.getWeakMasteryThreshold();
        return learningQuerySupport.listWeakPoints(studentIdValue, classIdValue, resolvedThreshold).stream()
                .filter(item -> courseIdValue == null || courseIdValue.equals(item.getCourseId()))
                .map(item -> toMasteryDTO(studentIdValue, classIdValue, item))
                .toList();
    }

    @Override
    public LearningPageDTO<LearningWrongRecordDTO> pageWrongRecords(String studentId,
                                                                    String classId,
                                                                    String courseId,
                                                                    Integer questionType,
                                                                    Integer isResolved,
                                                                    Integer current,
                                                                    Integer pageSize) {
        Long studentIdValue = parseRequiredId(studentId, "studentId");
        WrongRecordQueryRequest request = new WrongRecordQueryRequest();
        request.setClassId(parseOptionalId(classId, "classId"));
        request.setCourseId(parseOptionalId(courseId, "courseId"));
        request.setQuestionType(questionType);
        request.setIsResolved(isResolved);
        if (current != null && current > 0) {
            request.setCurrent(current);
        }
        if (pageSize != null && pageSize > 0) {
            request.setPageSize(pageSize);
        }

        Page<WrongRecordVO> page = learningQuerySupport.pageWrongRecords(studentIdValue, request);
        LearningPageDTO<LearningWrongRecordDTO> dto = new LearningPageDTO<>();
        dto.setCurrent(page.getCurrent());
        dto.setPageSize(page.getSize());
        dto.setTotal(page.getTotal());
        dto.setPages(page.getPages());
        dto.setRecords(page.getRecords().stream()
                .map(item -> toWrongRecordDTO(studentIdValue, item))
                .toList());
        return dto;
    }

    @Override
    public LearningWrongRecordDTO getWrongRecord(String studentId, String wrongId) {
        Long studentIdValue = parseRequiredId(studentId, "studentId");
        Long wrongIdValue = parseRequiredId(wrongId, "wrongId");
        LpWrongRecord record = learningQuerySupport.getOwnedWrongRecord(studentIdValue, wrongIdValue);
        ThrowUtils.throwIf(record == null, ErrorCode.NOT_FOUND_ERROR, "错题记录不存在");

        WrongRecordVO vo = learningQuerySupport.toWrongRecordVO(record);
        return vo == null ? toWrongRecordDTO(studentIdValue, record) : toWrongRecordDTO(studentIdValue, vo);
    }

    @Override
    public LearningWrongQuestionDetailDTO getQuestionWrongDetail(String classId, String questionId) {
        Long classIdValue = parseRequiredId(classId, "classId");
        Long questionIdValue = parseRequiredId(questionId, "questionId");
        WrongQuestionDetailVO detail = teacherLearningService.getQuestionWrongDetailWithoutAuth(classIdValue, questionIdValue);
        return toQuestionDetailDTO(detail);
    }

    private LearningMasteryDTO toMasteryDTO(Long studentId, Long classId, MasteryOverviewVO vo) {
        LearningMasteryDTO dto = new LearningMasteryDTO();
        BeanUtils.copyProperties(vo, dto);
        dto.setStudentId(toIdString(studentId));
        dto.setClassId(toIdString(classId));
        dto.setCourseId(toIdString(vo.getCourseId()));
        dto.setPointId(toIdString(vo.getPointId()));
        return dto;
    }

    private LearningWrongRecordDTO toWrongRecordDTO(Long studentId, WrongRecordVO vo) {
        LearningWrongRecordDTO dto = new LearningWrongRecordDTO();
        BeanUtils.copyProperties(vo, dto);
        dto.setWrongId(toIdString(vo.getWrongId()));
        dto.setStudentId(toIdString(studentId));
        dto.setClassId(toIdString(vo.getClassId()));
        dto.setCourseId(toIdString(vo.getCourseId()));
        dto.setQuestionId(toIdString(vo.getQuestionId()));
        dto.setExamId(toIdString(vo.getExamId()));
        dto.setPoints(vo.getPoints().stream().map(this::toWrongPointDTO).toList());
        return dto;
    }

    private LearningWrongRecordDTO toWrongRecordDTO(Long studentId, LpWrongRecord record) {
        LearningWrongRecordDTO dto = new LearningWrongRecordDTO();
        BeanUtils.copyProperties(record, dto);
        dto.setWrongId(toIdString(record.getWrongId()));
        dto.setStudentId(toIdString(studentId));
        dto.setClassId(toIdString(record.getClassId()));
        dto.setCourseId(toIdString(record.getCourseId()));
        dto.setQuestionId(toIdString(record.getQuestionId()));
        dto.setExamId(toIdString(record.getExamId()));
        return dto;
    }

    private LearningWrongPointDTO toWrongPointDTO(WrongPointBriefVO vo) {
        LearningWrongPointDTO dto = new LearningWrongPointDTO();
        BeanUtils.copyProperties(vo, dto);
        dto.setPointId(toIdString(vo.getPointId()));
        return dto;
    }

    private LearningWrongQuestionDetailDTO toQuestionDetailDTO(WrongQuestionDetailVO vo) {
        LearningWrongQuestionDetailDTO dto = new LearningWrongQuestionDetailDTO();
        BeanUtils.copyProperties(vo, dto);
        dto.setClassId(toIdString(vo.getClassId()));
        dto.setQuestionId(toIdString(vo.getQuestionId()));
        dto.setStudents(vo.getStudents().stream().map(this::toWrongStudentDTO).toList());
        dto.setWrongTypeDistribution(vo.getWrongTypeDistribution().stream()
                .map(this::toWrongTypeDistDTO)
                .toList());
        return dto;
    }

    private LearningWrongStudentDTO toWrongStudentDTO(WrongStudentBriefVO vo) {
        LearningWrongStudentDTO dto = new LearningWrongStudentDTO();
        BeanUtils.copyProperties(vo, dto);
        dto.setStudentId(toIdString(vo.getStudentId()));
        return dto;
    }

    private LearningWrongTypeDistDTO toWrongTypeDistDTO(WrongTypeDistVO vo) {
        LearningWrongTypeDistDTO dto = new LearningWrongTypeDistDTO();
        BeanUtils.copyProperties(vo, dto);
        return dto;
    }

    private Long parseRequiredId(String value, String fieldName) {
        Long id = parseOptionalId(value, fieldName);
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, fieldName + " 不能为空");
        }
        return id;
    }

    private Long parseOptionalId(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, fieldName + " 格式错误");
        }
    }

    private String toIdString(Long id) {
        return id == null ? null : String.valueOf(id);
    }
}