package com.gpnu.api.client.course.fallback;

import com.gpnu.api.client.course.CourseFeignClient;
import com.gpnu.api.dto.course.CourseSimpleDTO;
import com.gpnu.api.dto.section.SectionSimpleDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Slf4j
public class CourseFeignFallback implements FallbackFactory<CourseFeignClient> {

    @Override
    public CourseFeignClient create(Throwable cause) {
        log.error("CourseFeignClient fallback triggered", cause);
        return new CourseFeignClient() {
            @Override
            public CourseSimpleDTO getCourseSimple(Long courseId) {
                return null;
            }

            @Override
            public List<CourseSimpleDTO> getCourseBatch(List<Long> courseIds) {
                return List.of();
            }

            @Override
            public Boolean checkMember(Long classId, Long studentId) {
                return false;
            }

            @Override
            public SectionSimpleDTO getSectionSimple(Long sectionId) {
                return null;
            }
        };
    }
}
