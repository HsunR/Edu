package com.gpnu.course.service;

import com.gpnu.course.model.dto.chapter.ChapterCreateRequest;
import com.gpnu.course.model.dto.chapter.ChapterUpdateRequest;
import com.gpnu.course.model.dto.chapter.OrderItem;
import com.gpnu.course.model.entity.Chapter;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gpnu.course.model.vo.chapter.ChapterVO;

import java.util.List;

/**
* @author Chenxingdong
* @description 针对表【co_chapter(课程章表)】的数据库操作Service
* @createDate 2026-04-19 22:18:55
*/
public interface IChapterService extends IService<Chapter> {

    ChapterVO addChapter(Long teacherId, Long courseId, ChapterCreateRequest request);

    ChapterVO updateChapter(Long teacherId, Long chapterId, ChapterUpdateRequest request);

    void deleteChapter(Long teacherId, Long chapterId);

    void reorderChapters(Long teacherId, Long courseId, List<OrderItem> orderItems);

}
