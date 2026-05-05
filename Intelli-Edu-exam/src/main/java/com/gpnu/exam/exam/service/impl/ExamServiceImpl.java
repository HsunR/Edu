package com.gpnu.exam.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpnu.api.client.course.CourseFeignClient;
import com.gpnu.api.dto.course.CourseSimpleDTO;
import com.gpnu.api.dto.exam.ExamSimpleDTO;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.exception.ThrowUtils;
import com.gpnu.exam.exam.mapper.AnswerSheetMapper;
import com.gpnu.exam.exam.mapper.ExamMapper;
import com.gpnu.exam.exam.model.dto.ExamCreateRequest;
import com.gpnu.exam.exam.model.dto.ExamQueryRequest;
import com.gpnu.exam.exam.model.dto.ExamUpdateRequest;
import com.gpnu.exam.exam.model.entity.AnswerSheet;
import com.gpnu.exam.exam.model.entity.Exam;
import com.gpnu.exam.exam.model.enums.ExamStatus;
import com.gpnu.exam.exam.model.enums.ExamType;
import com.gpnu.exam.exam.model.enums.SheetStatus;
import com.gpnu.exam.exam.model.vo.AnswerSheetVO;
import com.gpnu.exam.exam.model.vo.ExamStatsVO;
import com.gpnu.exam.exam.model.vo.ExamVO;
import com.gpnu.exam.exam.service.IExamService;
import com.gpnu.exam.paper.model.entity.Paper;
import com.gpnu.exam.paper.model.enums.PaperStatus;
import com.gpnu.exam.paper.service.IPaperService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExamServiceImpl extends ServiceImpl<ExamMapper, Exam>
        implements IExamService {

    @Resource
    private IPaperService paperService;

    @Resource
    private CourseFeignClient courseFeignClient;

    @Resource
    private AnswerSheetMapper answerSheetMapper;

    @Override
    public ExamVO createExam(Long teacherId, ExamCreateRequest request) {
        // 校验试卷存在且已发布
        Paper paper = paperService.getById(request.getPaperId());
        ThrowUtils.throwIf(paper == null, ErrorCode.NOT_FOUND_ERROR, "试卷不存在");
        ThrowUtils.throwIf(paper.getStatus() != PaperStatus.PUBLISHED, ErrorCode.OPERATION_ERROR, "试卷未发布，不能创建考试");

        // 校验试卷归属
        ThrowUtils.throwIf(!paper.getTeacherId().equals(teacherId), ErrorCode.NO_AUTH_ERROR, "无权使用该试卷");

        // 校验班级存在且与试卷同课程（通过Feign查Course服务）
        CourseSimpleDTO course = courseFeignClient.getCourseSimple(paper.getCourseId());
        ThrowUtils.throwIf(course == null, ErrorCode.NOT_FOUND_ERROR, "课程不存在");

        // 校验时间合法
        ThrowUtils.throwIf(request.getStartTime().isAfter(request.getEndTime()),
                ErrorCode.PARAMS_ERROR, "开始时间不能晚于结束时间");

        Exam exam = new Exam();
        exam.setExamName(request.getExamName());
        exam.setPaperId(request.getPaperId());
        exam.setClassId(request.getClassId());
        exam.setCourseId(paper.getCourseId());
        exam.setTeacherId(teacherId);
        exam.setExamType(ExamType.values()[request.getExamType()]);
        exam.setStartTime(request.getStartTime());
        exam.setEndTime(request.getEndTime());
        exam.setDurationMinutes(request.getDurationMinutes());
        exam.setAllowLateSubmit(request.getAllowLateSubmit() != null ? request.getAllowLateSubmit() : false);
        exam.setStatus(ExamStatus.NOT_STARTED);
        save(exam);

        //TODO ：创建试卷应该发布消息，来通知该班级的所有学生

        return toVO(exam, paper.getPaperName());
    }

    @Override
    public ExamVO updateExam(Long teacherId, Long examId, ExamUpdateRequest request) {
        Exam exam = getAndValidate(teacherId, examId);
        ThrowUtils.throwIf(exam.getStatus() != ExamStatus.NOT_STARTED, ErrorCode.OPERATION_ERROR, "考试已开始，不可修改");

        if (StringUtils.hasText(request.getExamName())) {
            exam.setExamName(request.getExamName());
        }
        if (request.getStartTime() != null) {
            exam.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            exam.setEndTime(request.getEndTime());
        }
        if (request.getDurationMinutes() != null) {
            exam.setDurationMinutes(request.getDurationMinutes());
        }
        if (request.getAllowLateSubmit() != null) {
            exam.setAllowLateSubmit(request.getAllowLateSubmit());
        }
        updateById(exam);

        Paper paper = paperService.getById(exam.getPaperId());
        return toVO(exam, paper != null ? paper.getPaperName() : null);
    }

    @Override
    public void deleteExam(Long teacherId, Long examId) {
        Exam exam = getAndValidate(teacherId, examId);
        ThrowUtils.throwIf(exam.getStatus() != ExamStatus.NOT_STARTED, ErrorCode.OPERATION_ERROR, "考试已开始，不可删除");
        removeById(examId);
    }

    @Override
    public Page<ExamVO> listExams(ExamQueryRequest request) {
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(request.getClassId() != null, Exam::getClassId, request.getClassId());
        wrapper.eq(request.getCourseId() != null, Exam::getCourseId, request.getCourseId());
        wrapper.eq(request.getExamType() != null, Exam::getExamType, request.getExamType());
        wrapper.eq(request.getStatus() != null, Exam::getStatus, request.getStatus());
        wrapper.like(StringUtils.hasText(request.getKeyword()), Exam::getExamName, request.getKeyword());
        wrapper.orderByDesc(Exam::getCreatedAt);

        Page<Exam> page = page(new Page<>(request.getCurrent(), request.getPageSize()), wrapper);
        Page<ExamVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());

        Set<Long> paperIds = page.getRecords().stream()
                .map(Exam::getPaperId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, String> paperNameMap = new HashMap<>();
        if (!paperIds.isEmpty()) {
            paperNameMap = paperService.listByIds(paperIds).stream()
                    .collect(Collectors.toMap(Paper::getPaperId, Paper::getPaperName));
        }

        Map<Long, String> finalPaperNameMap = paperNameMap;
        voPage.setRecords(page.getRecords().stream()
                .map(e -> toVO(e, finalPaperNameMap.get(e.getPaperId())))
                .toList());
        return voPage;
    }

    @Override
    public ExamStatsVO getExamStats(Long teacherId, Long examId) {
        getAndValidate(teacherId, examId);

        List<AnswerSheet> sheets = answerSheetMapper.selectList(
                new LambdaQueryWrapper<AnswerSheet>().eq(AnswerSheet::getExamId, examId));

        ExamStatsVO stats = new ExamStatsVO();
        stats.setTotalStudents(sheets.size());
        stats.setAnsweringCount((int) sheets.stream().filter(s -> s.getStatus() == SheetStatus.ANSWERING).count());
        stats.setSubmittedCount((int) sheets.stream().filter(s -> s.getStatus() == SheetStatus.SUBMITTED).count());
        stats.setGradedCount((int) sheets.stream().filter(s -> s.getStatus() == SheetStatus.GRADED).count());

        List<BigDecimal> scores = sheets.stream()
                .filter(s -> s.getStatus() != SheetStatus.ANSWERING)
                .map(AnswerSheet::getTotalScore)
                .toList();

        if (!scores.isEmpty()) {
            stats.setMaxScore(scores.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
            stats.setMinScore(scores.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
            BigDecimal sum = scores.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            stats.setAvgScore(sum.divide(BigDecimal.valueOf(scores.size()), 1, RoundingMode.HALF_UP));
        }
        return stats;
    }

    @Override
    public List<AnswerSheetVO> listExamSheets(Long teacherId, Long examId) {
        getAndValidate(teacherId, examId);

        List<AnswerSheet> sheets = answerSheetMapper.selectList(
                new LambdaQueryWrapper<AnswerSheet>()
                        .eq(AnswerSheet::getExamId, examId)
                        .orderByDesc(AnswerSheet::getSubmitTime));

        return sheets.stream().map(this::toSheetVO).toList();
    }

    @Override
    public ExamSimpleDTO getExamSimple(Long examId) {
        Exam exam = getById(examId);
        if (exam == null) {
            return null;
        }
        ExamSimpleDTO dto = new ExamSimpleDTO();
        dto.setExamId(exam.getExamId());
        dto.setExamName(exam.getExamName());
        dto.setClassId(exam.getClassId());
        dto.setCourseId(exam.getCourseId());
        dto.setStatus(exam.getStatus().getCode());
        return dto;
    }

    // ==================== 私有方法 ====================

    private Exam getAndValidate(Long teacherId, Long examId) {
        Exam exam = getById(examId);
        ThrowUtils.throwIf(exam == null, ErrorCode.NOT_FOUND_ERROR, "考试不存在");
        ThrowUtils.throwIf(!exam.getTeacherId().equals(teacherId), ErrorCode.NO_AUTH_ERROR, "无权操作该考试");
        return exam;
    }

    private ExamVO toVO(Exam exam, String paperName) {
        ExamVO vo = new ExamVO();
        BeanUtils.copyProperties(exam, vo);
        vo.setPaperName(paperName);
        return vo;
    }

    private AnswerSheetVO toSheetVO(AnswerSheet sheet) {
        AnswerSheetVO vo = new AnswerSheetVO();
        BeanUtils.copyProperties(sheet, vo);
        return vo;
    }
}
