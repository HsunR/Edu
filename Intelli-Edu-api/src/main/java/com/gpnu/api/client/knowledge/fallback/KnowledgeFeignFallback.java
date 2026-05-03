package com.gpnu.api.client.knowledge.fallback;

import com.gpnu.api.client.knowledge.KnowledgeFeignClient;
import com.gpnu.api.dto.knowledge.KnowledgeTreeDTO;
import com.gpnu.api.dto.knowledge.PointSimpleDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class KnowledgeFeignFallback implements FallbackFactory<KnowledgeFeignClient> {

    @Override
    public KnowledgeFeignClient create(Throwable cause) {
        log.error("KnowledgeFeignClient fallback triggered", cause);
        return new KnowledgeFeignClient() {
            @Override
            public List<KnowledgeTreeDTO> getPointTree(Long courseId) {
                return List.of();
            }

            @Override
            public List<PointSimpleDTO> getPointBatch(List<Long> pointIds) {
                return List.of();
            }

            @Override
            public List<Long> getQuestionIdsByPoint(Long pointId) {
                return List.of();
            }

            @Override
            public Map<Long, List<PointSimpleDTO>> getPointsByQuestions(List<Long> questionIds) {
                return Map.of();
            }

            @Override
            public void clearSectionRelations(Long sectionId) {
            }

            @Override
            public void clearQuestionRelations(Long questionId) {
            }
        };
    }
}
