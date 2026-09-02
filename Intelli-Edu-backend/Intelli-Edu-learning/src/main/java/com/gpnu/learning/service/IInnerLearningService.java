package com.gpnu.learning.service;

import com.gpnu.api.dto.learning.LearningMasteryDTO;
import com.gpnu.api.dto.learning.LearningPageDTO;
import com.gpnu.api.dto.learning.LearningProfileSummaryDTO;
import com.gpnu.api.dto.learning.LearningWrongQuestionDetailDTO;
import com.gpnu.api.dto.learning.LearningWrongRecordDTO;

import java.util.List;

/**
 * 对内 Feign 学情查询服务：供 AI 模块 RAG 使用，不含推荐排序逻辑。
 * 对外（跨模块 Feign）契约型服务，采用接口 + 实现分离。
 */
public interface IInnerLearningService {

    LearningProfileSummaryDTO buildProfileSummary(String studentId, String classId);

    List<LearningMasteryDTO> listMastery(String studentId, String classId, String courseId);

    List<LearningMasteryDTO> listWeakPoints(String studentId, String classId, String courseId, Integer threshold);

    LearningPageDTO<LearningWrongRecordDTO> pageWrongRecords(String studentId,
                                                             String classId,
                                                             String courseId,
                                                             Integer questionType,
                                                             Integer isResolved,
                                                             Integer current,
                                                             Integer pageSize);

    LearningWrongRecordDTO getWrongRecord(String studentId, String wrongId);

    LearningWrongQuestionDetailDTO getQuestionWrongDetail(String classId, String questionId);
}