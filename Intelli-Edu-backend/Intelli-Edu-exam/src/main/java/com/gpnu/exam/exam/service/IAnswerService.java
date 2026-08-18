package com.gpnu.exam.exam.service;

import com.gpnu.exam.exam.model.dto.GradeRequest;
import com.gpnu.exam.exam.model.vo.AnswerSheetDetailVO;
import com.gpnu.exam.exam.model.vo.AnswerSheetVO;

public interface IAnswerService {

    /**
     * 学生进入考试，创建答卷并计算截止时间
     */
    AnswerSheetVO enterExam(Long studentId, Long examId);

    /**
     * 保存单题答案到Redis
     */
    void saveAnswer(Long studentId, Long sheetId, Long questionId, String content);

    /**
     * 交卷：刷Redis到DB → 客观题自动判分 → 更新答卷状态和分数
     */
    void submitSheet(Long studentId, Long sheetId);

    /**
     * 查看我的答卷详情（学生）
     */
    AnswerSheetDetailVO getMySheet(Long studentId, Long examId);

    /**
     * 查看答卷详情（教师批阅用）
     */
    AnswerSheetDetailVO getSheetDetail(Long sheetId);

    /**
     * 批阅单道主观题
     */
    void gradeRecord(Long teacherId, Long recordId, GradeRequest request);

    /**
     * 完成批阅：汇总主观题得分 → 更新答卷总分和状态
     */
    void finishGrading(Long teacherId, Long sheetId);

    /**
     * 定时刷新：将Redis中暂存的答案批量落库
     */
    void flushPendingAnswers();

    /**
     * 定时任务：自动交卷超时答卷
     */
    void autoSubmitOverdue();
}
