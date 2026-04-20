package com.gpnu.api.client.course;

import com.gpnu.api.client.course.fallback.CourseFeignFallback;
import com.gpnu.api.dto.course.CourseSimpleDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@FeignClient(name = "Intelli-Edu-course", path = "/api/course",
             fallbackFactory = CourseFeignFallback.class)
public interface CourseFeignClient {

    @GetMapping("/inner/courses/{courseId}")
    CourseSimpleDTO getCourseSimple(@PathVariable("courseId") Long courseId);

    @PostMapping("/inner/courses/batch")
    List<CourseSimpleDTO> getCourseBatch(@RequestBody List<Long> courseIds);

    @GetMapping("/inner/classes/{classId}/check-member")
    Boolean checkMember(@PathVariable("classId") Long classId,
                        @RequestParam("studentId") Long studentId);
}
