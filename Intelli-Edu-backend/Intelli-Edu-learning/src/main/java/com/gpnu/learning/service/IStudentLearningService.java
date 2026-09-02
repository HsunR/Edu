package com.gpnu.learning.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gpnu.learning.model.dto.WrongRecordQueryRequest;
import com.gpnu.learning.model.dto.WrongStatsQueryRequest;
import com.gpnu.learning.model.vo.MasteryOverviewVO;
import com.gpnu.learning.model.vo.WrongRecordVO;
import com.gpnu.learning.model.vo.WrongStatsVO;

import java.util.List;

/**
 * 学生端学情查询服务（掌握度、错题本、错题统计）。
 * 对外契约型服务，采用接口 + 实现分离，与 exam/course/user 模块约定一致。
 */
public interface IStudentLearningService {

    List<MasteryOverviewVO> getMasteryOverview(Long studentId, Long classId);

    List<MasteryOverviewVO> getWeakPoints(Long studentId, Long classId);

    Page<WrongRecordVO> pageWrongRecords(Long studentId, WrongRecordQueryRequest request);

    WrongStatsVO getWrongStats(Long studentId, WrongStatsQueryRequest request);

    void resolveWrongRecord(Long studentId, Long wrongId);
}