package com.gpnu.exam.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gpnu.api.client.course.CourseFeignClient;
import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.exception.ThrowUtils;
import com.gpnu.common.service.RedisService;
import com.gpnu.exam.exam.mapper.AnswerRecordMapper;
import com.gpnu.exam.exam.mapper.AnswerSheetMapper;
import com.gpnu.exam.exam.model.dto.GradeRequest;
import com.gpnu.exam.exam.model.entity.AnswerRecord;
import com.gpnu.exam.exam.model.entity.AnswerSheet;
import com.gpnu.exam.exam.model.entity.Exam;
import com.gpnu.exam.exam.model.enums.ExamStatus;
import com.gpnu.exam.exam.model.enums.ExamType;
import com.gpnu.exam.exam.model.enums.GradingStatus;
import com.gpnu.exam.exam.model.enums.SheetStatus;
import com.gpnu.exam.question.model.enums.QuestionType;
import com.gpnu.exam.exam.model.vo.AnswerRecordVO;
import com.gpnu.exam.exam.model.vo.AnswerSheetDetailVO;
import com.gpnu.exam.exam.model.vo.AnswerSheetVO;
import com.gpnu.exam.exam.service.IAnswerService;
import com.gpnu.exam.exam.service.IExamService;
import com.gpnu.exam.paper.mapper.PaperQuestionMapper;
import com.gpnu.exam.paper.model.entity.PaperQuestion;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AnswerServiceImpl implements IAnswerService {

    private static final String ANSWER_KEY_PREFIX = "exam:answers:";

    @Resource
    private AnswerSheetMapper answerSheetMapper;

    @Resource
    private AnswerRecordMapper answerRecordMapper;

    @Resource
    private IExamService examService;

    @Resource
    private PaperQuestionMapper paperQuestionMapper;

    @Resource
    private CourseFeignClient courseFeignClient;

    @Resource
    private RedisService redisService;

    // ==================== 学生操作 ====================

    @Override
    @Transactional
    public AnswerSheetVO enterExam(Long studentId, Long examId) {
        Exam exam = examService.getById(examId);
        ThrowUtils.throwIf(exam == null, ErrorCode.NOT_FOUND_ERROR, "考试不存在");

        // 校验考试时间窗口
        OffsetDateTime now = OffsetDateTime.now();
        ThrowUtils.throwIf(now.isBefore(exam.getStartTime()), ErrorCode.OPERATION_ERROR, "考试尚未开始");
        ThrowUtils.throwIf(now.isAfter(exam.getEndTime()), ErrorCode.OPERATION_ERROR, "考试已结束");

        // 校验学生是否在班级中
        Boolean isMember = courseFeignClient.checkMember(exam.getClassId(), studentId);
        ThrowUtils.throwIf(!Boolean.TRUE.equals(isMember), ErrorCode.NO_AUTH_ERROR, "您不是该班级的成员");

        // 检查是否已有答卷
        AnswerSheet existing = answerSheetMapper.selectOne(
                new LambdaQueryWrapper<AnswerSheet>()
                        .eq(AnswerSheet::getExamId, examId)
                        .eq(AnswerSheet::getStudentId, studentId));

        if (existing != null) {
            // 对于作业模式，允许重新进入已提交的答卷
            if (exam.getExamType() == ExamType.HOMEWORK && existing.getStatus() == SheetStatus.SUBMITTED) {
                existing.setStatus(SheetStatus.ANSWERING);
                answerSheetMapper.updateById(existing);
            }
            return toSheetVO(existing);
        }

        // 计算个人截止时间
        OffsetDateTime deadline = calculateDeadline(exam, now);

        AnswerSheet sheet = new AnswerSheet();
        sheet.setExamId(examId);
        sheet.setStudentId(studentId);
        sheet.setStatus(SheetStatus.ANSWERING);
        sheet.setTotalScore(BigDecimal.ZERO);
        sheet.setObjectiveScore(BigDecimal.ZERO);
        sheet.setSubjectiveScore(BigDecimal.ZERO);
        sheet.setSubmitCount(0);
        sheet.setStartAnswerTime(now);
        sheet.setDeadline(deadline);
        answerSheetMapper.insert(sheet);

        return toSheetVO(sheet);
    }

    @Override
    public void saveAnswer(Long studentId, Long sheetId, Long questionId, String content) {
        AnswerSheet sheet = answerSheetMapper.selectById(sheetId);
        ThrowUtils.throwIf(sheet == null, ErrorCode.NOT_FOUND_ERROR, "答卷不存在");
        ThrowUtils.throwIf(!sheet.getStudentId().equals(studentId), ErrorCode.NO_AUTH_ERROR, "无权操作该答卷");

        // 检查答卷状态：答题中可保存；作业模式下已提交也可保存
        Exam exam = examService.getById(sheet.getExamId());
        boolean canSave = sheet.getStatus() == SheetStatus.ANSWERING
                || (exam.getExamType() == ExamType.HOMEWORK && sheet.getStatus() == SheetStatus.SUBMITTED);
        ThrowUtils.throwIf(!canSave, ErrorCode.OPERATION_ERROR, "当前状态不允许保存答案");

        // 检查是否超时
        if (sheet.getDeadline() != null) {
            ThrowUtils.throwIf(OffsetDateTime.now().isAfter(sheet.getDeadline()),
                    ErrorCode.OPERATION_ERROR, "已超过截止时间");
        }

        // 保存到Redis
        String key = ANSWER_KEY_PREFIX + sheetId;
        redisService.setCacheMapValue(key, String.valueOf(questionId), content);
    }

    @Override
    @Transactional
    public void submitSheet(Long studentId, Long sheetId) {
        AnswerSheet sheet = answerSheetMapper.selectById(sheetId);
        ThrowUtils.throwIf(sheet == null, ErrorCode.NOT_FOUND_ERROR, "答卷不存在");
        ThrowUtils.throwIf(!sheet.getStudentId().equals(studentId), ErrorCode.NO_AUTH_ERROR, "无权操作该答卷");

        Exam exam = examService.getById(sheet.getExamId());
        boolean canSubmit = sheet.getStatus() == SheetStatus.ANSWERING
                || (exam.getExamType() == ExamType.HOMEWORK && sheet.getStatus() == SheetStatus.SUBMITTED);
        ThrowUtils.throwIf(!canSubmit, ErrorCode.OPERATION_ERROR, "当前状态不允许提交");

        doSubmit(sheet, exam);
    }

    @Override
    public AnswerSheetDetailVO getMySheet(Long studentId, Long examId) {
        AnswerSheet sheet = answerSheetMapper.selectOne(
                new LambdaQueryWrapper<AnswerSheet>()
                        .eq(AnswerSheet::getExamId, examId)
                        .eq(AnswerSheet::getStudentId, studentId));
        ThrowUtils.throwIf(sheet == null, ErrorCode.NOT_FOUND_ERROR, "答卷不存在");
        return buildSheetDetail(sheet);
    }

    @Override
    public AnswerSheetDetailVO getSheetDetail(Long sheetId) {
        AnswerSheet sheet = answerSheetMapper.selectById(sheetId);
        ThrowUtils.throwIf(sheet == null, ErrorCode.NOT_FOUND_ERROR, "答卷不存在");
        return buildSheetDetail(sheet);
    }

    // ==================== 教师批阅 ====================

    @Override
    @Transactional
    public void gradeRecord(Long teacherId, Long recordId, GradeRequest request) {
        AnswerRecord record = answerRecordMapper.selectById(recordId);
        ThrowUtils.throwIf(record == null, ErrorCode.NOT_FOUND_ERROR, "答题记录不存在");

        record.setScore(request.getScore());
        record.setComment(request.getComment());
        record.setGradingStatus(GradingStatus.GRADED);
        record.setGraderId(teacherId);
        answerRecordMapper.updateById(record);
    }

    @Override
    @Transactional
    public void finishGrading(Long teacherId, Long sheetId) {
        AnswerSheet sheet = answerSheetMapper.selectById(sheetId);
        ThrowUtils.throwIf(sheet == null, ErrorCode.NOT_FOUND_ERROR, "答卷不存在");
        ThrowUtils.throwIf(sheet.getStatus() != SheetStatus.SUBMITTED, ErrorCode.OPERATION_ERROR, "答卷未提交，不可批阅");

        // 检查是否所有记录都已批阅
        List<AnswerRecord> records = answerRecordMapper.selectList(
                new LambdaQueryWrapper<AnswerRecord>().eq(AnswerRecord::getSheetId, sheetId));

        boolean allGraded = records.stream()
                .allMatch(r -> r.getGradingStatus() == GradingStatus.GRADED);
        ThrowUtils.throwIf(!allGraded, ErrorCode.OPERATION_ERROR, "还有未批阅的题目");

        // 汇总主观题得分
        BigDecimal subjectiveScore = records.stream()
                .filter(r -> r.getGradingStatus() == GradingStatus.GRADED && r.getGraderId() != null)
                .map(AnswerRecord::getScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        sheet.setSubjectiveScore(subjectiveScore);
        sheet.setTotalScore(sheet.getObjectiveScore().add(subjectiveScore));
        sheet.setStatus(SheetStatus.GRADED);
        answerSheetMapper.updateById(sheet);

        // 检查该考试下所有已提交答卷是否都已批阅，是则更新考试状态为 GRADED
        Long examId = sheet.getExamId();
        Long submittedCount = answerSheetMapper.selectCount(
                new LambdaQueryWrapper<AnswerSheet>()
                        .eq(AnswerSheet::getExamId, examId)
                        .ge(AnswerSheet::getStatus, SheetStatus.SUBMITTED));

        Long gradedCount = answerSheetMapper.selectCount(
                new LambdaQueryWrapper<AnswerSheet>()
                        .eq(AnswerSheet::getExamId, examId)
                        .eq(AnswerSheet::getStatus, SheetStatus.GRADED));

        if (submittedCount > 0 && submittedCount.equals(gradedCount)) {
            Exam exam = examService.getById(examId);
            if (exam != null && exam.getStatus() == ExamStatus.ENDED) {
                exam.setStatus(ExamStatus.GRADED);
                examService.updateById(exam);
            }
        }
    }

    // ==================== 定时任务 ====================

    @Override
    public void flushPendingAnswers() {
        Collection<String> keys = redisService.keys(ANSWER_KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return;
        }

        for (String key : keys) {
            try {
                Long sheetId = Long.parseLong(key.substring(ANSWER_KEY_PREFIX.length()));
                flushSheetAnswers(sheetId, key);
            } catch (Exception e) {
                log.error("刷新答案失败, key={}", key, e);
            }
        }
    }

    @Override
    public void autoSubmitOverdue() {
        OffsetDateTime now = OffsetDateTime.now();
        List<AnswerSheet> overdueSheets = answerSheetMapper.selectList(
                new LambdaQueryWrapper<AnswerSheet>()
                        .eq(AnswerSheet::getStatus, SheetStatus.ANSWERING)
                        .isNotNull(AnswerSheet::getDeadline)
                        .lt(AnswerSheet::getDeadline, now));

        for (AnswerSheet sheet : overdueSheets) {
            try {
                Exam exam = examService.getById(sheet.getExamId());
                if (exam != null) {
                    log.info("自动交卷: sheetId={}, studentId={}", sheet.getSheetId(), sheet.getStudentId());
                    doSubmit(sheet, exam);
                }
            } catch (Exception e) {
                log.error("自动交卷失败: sheetId={}", sheet.getSheetId(), e);
            }
        }
    }

    // ==================== 核心私有方法 ====================

    /**
     * 执行交卷逻辑：刷Redis → 自动判分 → 更新答卷
     */
    private void doSubmit(AnswerSheet sheet, Exam exam) {
        Long sheetId = sheet.getSheetId();

        // 1. 刷Redis到DB
        String redisKey = ANSWER_KEY_PREFIX + sheetId;
        flushSheetAnswers(sheetId, redisKey);

        // 2. 查询试卷题目（含快照）
        List<PaperQuestion> paperQuestions = paperQuestionMapper.selectList(
                new LambdaQueryWrapper<PaperQuestion>()
                        .eq(PaperQuestion::getPaperId, exam.getPaperId()));
        Map<Long, PaperQuestion> pqMap = paperQuestions.stream()
                .collect(Collectors.toMap(PaperQuestion::getQuestionId, Function.identity()));

        // 3. 查询答题记录
        List<AnswerRecord> records = answerRecordMapper.selectList(
                new LambdaQueryWrapper<AnswerRecord>().eq(AnswerRecord::getSheetId, sheetId));

        // 4. 自动判分客观题
        BigDecimal objectiveScore = BigDecimal.ZERO;
        for (AnswerRecord record : records) {
            PaperQuestion pq = pqMap.get(record.getQuestionId());
            if (pq == null || pq.getQuestionSnapshot() == null) {
                continue;
            }

            Map<String, Object> snapshot = pq.getQuestionSnapshot();
            Integer questionType = (Integer) snapshot.get("question_type");
            boolean isObjective = questionType != null && questionType <= 2; // 单选、多选、判断

            if (isObjective) {
                String correctAnswer = (String) snapshot.get("answer");
                boolean correct = correctAnswer != null
                        && correctAnswer.equalsIgnoreCase(record.getAnswerContent());
                record.setIsCorrect(correct);
                record.setScore(correct ? pq.getScore() : BigDecimal.ZERO);
                record.setGradingStatus(GradingStatus.GRADED);
                if (correct) {
                    objectiveScore = objectiveScore.add(pq.getScore());
                }
            } else {
                // 主观题：待批阅
                record.setGradingStatus(GradingStatus.PENDING);
                record.setScore(BigDecimal.ZERO);
            }
            answerRecordMapper.updateById(record);
        }

        // 5. 更新答卷
        sheet.setObjectiveScore(objectiveScore);
        sheet.setTotalScore(objectiveScore); // 主观题批阅后会追加
        sheet.setStatus(SheetStatus.SUBMITTED);
        sheet.setSubmitTime(OffsetDateTime.now());
        sheet.setSubmitCount(sheet.getSubmitCount() + 1);
        answerSheetMapper.updateById(sheet);

        // 6. 清除Redis缓存
        redisService.deleteObject(redisKey);
    }

    /**
     * 将Redis中某答卷的答案刷入DB
     */
    private void flushSheetAnswers(Long sheetId, String redisKey) {
        Map<String, String> answers = redisService.getCacheMap(redisKey);
        if (answers == null || answers.isEmpty()) {
            return;
        }

        for (Map.Entry<String, String> entry : answers.entrySet()) {
            Long questionId = Long.parseLong(entry.getKey());
            String content = entry.getValue();

            // saveOrUpdate: 先查再决定插入或更新
            AnswerRecord existing = answerRecordMapper.selectOne(
                    new LambdaQueryWrapper<AnswerRecord>()
                            .eq(AnswerRecord::getSheetId, sheetId)
                            .eq(AnswerRecord::getQuestionId, questionId));

            if (existing != null) {
                existing.setAnswerContent(content);
                answerRecordMapper.updateById(existing);
            } else {
                AnswerRecord record = new AnswerRecord();
                record.setSheetId(sheetId);
                record.setQuestionId(questionId);
                record.setAnswerContent(content);
                record.setScore(BigDecimal.ZERO);
                record.setGradingStatus(GradingStatus.PENDING);
                answerRecordMapper.insert(record);
            }
        }
    }

    /**
     * 计算个人截止时间
     */
    private OffsetDateTime calculateDeadline(Exam exam, OffsetDateTime enterTime) {
        // 允许迟交 → 不自动交卷
        if (Boolean.TRUE.equals(exam.getAllowLateSubmit())) {
            return null;
        }
        // 不限时 → 截止退化为end_time
        if (exam.getDurationMinutes() == null) {
            return exam.getEndTime();
        }
        // 限时 → min(进入时间+时长, end_time)
        OffsetDateTime byDuration = enterTime.plusMinutes(exam.getDurationMinutes());
        return byDuration.isBefore(exam.getEndTime()) ? byDuration : exam.getEndTime();
    }

    /**
     * 构建答卷详情VO，包含考试名称、题目列表及每道题的详细信息
     *
     * @param sheet 答卷实体
     * @return 答卷详情视图对象
     */
    private AnswerSheetDetailVO buildSheetDetail(AnswerSheet sheet) {
        // 1. 拷贝基本属性到VO
        AnswerSheetDetailVO detail = new AnswerSheetDetailVO();
        BeanUtils.copyProperties(sheet, detail);

        // 2. 获取考试信息并设置考试名称
        Exam exam = examService.getById(sheet.getExamId());
        if (exam != null) {
            detail.setExamName(exam.getExamName());
        }

        // 3. 查询该答卷的所有答题记录
        List<AnswerRecord> records = answerRecordMapper.selectList(
                new LambdaQueryWrapper<AnswerRecord>()
                        .eq(AnswerRecord::getSheetId, sheet.getSheetId()));

        // 4. 获取试卷题目映射（按题目ID索引），用于后续填充快照信息
        Map<Long, PaperQuestion> pqMap = Map.of();
        if (exam != null) {
            List<PaperQuestion> pqs = paperQuestionMapper.selectList(
                    new LambdaQueryWrapper<PaperQuestion>()
                            .eq(PaperQuestion::getPaperId, exam.getPaperId())
                            .orderByAsc(PaperQuestion::getOrderIndex));
            pqMap = pqs.stream().collect(Collectors.toMap(PaperQuestion::getQuestionId, Function.identity()));
        }

        // 5. 最终不可变的题目映射
        Map<Long, PaperQuestion> finalPqMap = pqMap;
        List<AnswerRecordVO> recordVOs;

        // 6. 如果有答题记录，则根据记录生成VO；否则根据试卷题目生成空记录VO
        if (!records.isEmpty()) {
            recordVOs = records.stream().map(r -> {
                AnswerRecordVO vo = new AnswerRecordVO();
                BeanUtils.copyProperties(r, vo);

                // 从快照中填充题目类型、题干、选项等额外信息
                PaperQuestion pq = finalPqMap.get(r.getQuestionId());
                if (pq != null) {
                    fillFromSnapshot(vo, pq);
                }
                return vo;
            }).toList();
        } else {
            // 无答题记录时，使用试卷题目生成空回答的VO
            recordVOs = finalPqMap.values().stream().map(pq -> {
                AnswerRecordVO vo = new AnswerRecordVO();
                vo.setQuestionId(pq.getQuestionId());
                vo.setAnswerContent(null);
                vo.setScore(null);
                vo.setIsCorrect(null);
                vo.setGradingStatus(GradingStatus.PENDING);
                fillFromSnapshot(vo, pq);
                return vo;
            }).toList();
        }

        // 7. 设置VO中的答题记录列表
        detail.setRecords(recordVOs);
        return detail;
    }

    private void fillFromSnapshot(AnswerRecordVO vo, PaperQuestion pq) {
        vo.setQuestionScore(pq.getScore());
        if (pq.getQuestionSnapshot() != null) {
            Map<String, Object> snap = pq.getQuestionSnapshot();
            Object qtObj = snap.get("question_type");
            if (qtObj instanceof Number qtNum) {
                int code = qtNum.intValue();
                for (QuestionType qt : QuestionType.values()) {
                    if (qt.getCode() == code) {
                        vo.setQuestionType(qt);
                        break;
                    }
                }
            } else if (qtObj instanceof String qtStr) {
                vo.setQuestionType(QuestionType.valueOf(qtStr));
            }
            Object stemVal = snap.get("stem");
            if (stemVal instanceof String) {
                vo.setStem((String) stemVal);
            } else if (stemVal != null) {
                vo.setStem(stemVal.toString());
            }
            Object answerVal = snap.get("answer");
            if (answerVal instanceof String) {
                vo.setCorrectAnswer((String) answerVal);
            } else if (answerVal != null) {
                vo.setCorrectAnswer(answerVal.toString());
            }
        }
    }

    private AnswerSheetVO toSheetVO(AnswerSheet sheet) {
        AnswerSheetVO vo = new AnswerSheetVO();
        BeanUtils.copyProperties(sheet, vo);
        return vo;
    }
}
