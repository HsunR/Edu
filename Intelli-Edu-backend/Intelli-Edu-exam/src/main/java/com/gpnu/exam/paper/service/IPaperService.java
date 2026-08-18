package com.gpnu.exam.paper.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gpnu.exam.paper.model.dto.*;
import com.gpnu.exam.paper.model.entity.Paper;
import com.gpnu.exam.paper.model.vo.PaperDetailVO;
import com.gpnu.exam.paper.model.vo.PaperVO;

public interface IPaperService extends IService<Paper> {

    PaperVO createPaper(Long teacherId, PaperCreateRequest request);

    PaperVO updatePaper(Long teacherId, Long paperId, PaperUpdateRequest request);

    void deletePaper(Long teacherId, Long paperId);

    void addQuestions(Long teacherId, Long paperId, PaperQuestionAddRequest request);

    void removeQuestion(Long teacherId, Long paperId, Long questionId);

    void reorderQuestions(Long teacherId, Long paperId, PaperQuestionOrderRequest request);

    /**
     * 发布试卷：冻结题目快照，状态变为已发布
     */
    void publishPaper(Long teacherId, Long paperId);

    Page<PaperVO> listPapers(Long teacherId, PaperQueryRequest request);

    PaperDetailVO getPaperDetail(Long paperId);
}
