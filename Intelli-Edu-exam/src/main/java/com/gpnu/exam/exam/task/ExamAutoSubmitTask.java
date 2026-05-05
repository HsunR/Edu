package com.gpnu.exam.exam.task;

import com.gpnu.exam.exam.service.IAnswerService;
import com.gpnu.exam.exam.service.IExamService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 考试定时任务
 * <p>
 * 自动交卷策略采用 @Scheduled 定时轮询（预留MQ扩展点：
 * 后续引入RabbitMQ时，可新增 DelayedMessageStrategy 实现，不动此类逻辑）
 */
@Component
@Slf4j
public class ExamAutoSubmitTask {

    @Resource
    private IAnswerService answerService;

    @Resource
    private IExamService examService;

    /**
     * 每30秒扫描超时未提交的答卷并自动交卷
     */
    @Scheduled(fixedDelay = 30_000)
    public void autoSubmitOverdue() {
        try {
            answerService.autoSubmitOverdue();
        } catch (Exception e) {
            log.error("自动交卷定时任务执行异常", e);
        }
    }

    /**
     * 每60秒将Redis中暂存的答案批量刷入数据库
     */
    @Scheduled(fixedDelay = 60_000)
    public void flushPendingAnswers() {
        try {
            answerService.flushPendingAnswers();
        } catch (Exception e) {
            log.error("答案刷盘定时任务执行异常", e);
        }
    }

    /**
     * 每60秒自动更新考试状态
     */
    @Scheduled(fixedDelay = 60_000)
    public void autoUpdateExamStatus() {
        try {
            examService.updateExamStatus();
        } catch (Exception e) {
            log.error("考试状态更新定时任务执行异常", e);
        }
    }
}
