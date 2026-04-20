package com.gpnu.course.service;

import com.gpnu.course.model.dto.chapter.OrderItem;
import com.gpnu.course.model.dto.section.SectionCreateRequest;
import com.gpnu.course.model.dto.section.SectionResourceAddRequest;
import com.gpnu.course.model.dto.section.SectionUpdateRequest;
import com.gpnu.course.model.entity.Section;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gpnu.course.model.vo.section.SectionDetailVO;
import com.gpnu.course.model.vo.section.SectionResourceVO;
import com.gpnu.course.model.vo.section.SectionVO;

import java.util.List;

/**
* @author Chenxingdong
* @description 针对表【co_section(课程节表)】的数据库操作Service
*/
public interface ISectionService extends IService<Section> {


    SectionVO addSection(Long teacherId, Long chapterId, SectionCreateRequest request);

    SectionVO updateSection(Long teacherId, Long sectionId, SectionUpdateRequest request);

    void deleteSection(Long teacherId, Long sectionId);

    void reorderSections(Long teacherId, Long chapterId, List<OrderItem> orderItems);

    SectionResourceVO addResource(Long teacherId, Long sectionId, SectionResourceAddRequest request);

    void removeResource(Long teacherId, Long sectionId, Long id);

    void reorderResources(Long teacherId, Long sectionId, List<OrderItem> orderItems);

    SectionDetailVO getSectionDetail(Long sectionId, Long currentUserId, Integer userType);

}
