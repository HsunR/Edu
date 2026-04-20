package com.gpnu.course.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gpnu.api.dto.course.CourseSimpleDTO;
import com.gpnu.course.model.dto.course.CourseCreateRequest;
import com.gpnu.course.model.dto.course.CourseQueryRequest;
import com.gpnu.course.model.dto.course.CourseUpdateRequest;
import com.gpnu.course.model.entity.Course;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gpnu.course.model.vo.course.CourseDetailVO;
import com.gpnu.course.model.vo.course.CourseVO;

import java.util.List;

/**
* @author Chenxingdong
* @description 针对表【co_course(课程表)】的数据库操作Service
*/
public interface ICourseService extends IService<Course> {

    CourseVO createCourse(Long teacherId, CourseCreateRequest request);

    CourseVO updateCourse(Long teacherId, Long courseId, CourseUpdateRequest request);

    void publishCourse(Long teacherId, Long courseId);

    void archiveCourse(Long teacherId, Long courseId);

    void deleteCourse(Long teacherId, Long courseId);

    Page<CourseVO> listTeachingCourses(Long teacherId, CourseQueryRequest request);

    Page<CourseVO> listPublicCourses(CourseQueryRequest request);

    CourseDetailVO getCourseDetail(Long courseId, Long currentUserId);

    // ----- Feign -----

    CourseSimpleDTO getCourseSimple(Long courseId);

    List<CourseSimpleDTO> getCourseBatch(List<Long> courseIds);

}
