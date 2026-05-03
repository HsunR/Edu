package com.gpnu.api.client.knowledge;

import com.gpnu.api.client.knowledge.fallback.KnowledgeFeignFallback;
import com.gpnu.api.dto.knowledge.KnowledgeTreeDTO;
import com.gpnu.api.dto.knowledge.PointSimpleDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "Intelli-Edu-knowledge", path = "/api/knowledge",
        fallbackFactory = KnowledgeFeignFallback.class)
public interface KnowledgeFeignClient {

    @GetMapping("/inner/points/tree")
    List<KnowledgeTreeDTO> getPointTree(@RequestParam("courseId") Long courseId);

    @PostMapping("/inner/points/batch")
    List<PointSimpleDTO> getPointBatch(@RequestBody List<Long> pointIds);

    @GetMapping("/inner/points/{pointId}/question-ids")
    List<Long> getQuestionIdsByPoint(@PathVariable("pointId") Long pointId);

    @PostMapping("/inner/questions/points")
    Map<Long, List<PointSimpleDTO>> getPointsByQuestions(@RequestBody List<Long> questionIds);

    @DeleteMapping("/inner/sections/{sectionId}/relations")
    void clearSectionRelations(@PathVariable("sectionId") Long sectionId);

    @DeleteMapping("/inner/questions/{questionId}/relations")
    void clearQuestionRelations(@PathVariable("questionId") Long questionId);
}
