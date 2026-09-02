package com.gpnu.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gpnu.learning.model.entity.LpMastery;
import com.gpnu.learning.model.vo.ClassMasteryPointVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LpMasteryMapper extends BaseMapper<LpMastery> {

    /**
     * 教师端：按班级聚合各知识点掌握度（共用 lp_mastery，不按学生过滤）。
     */
    List<ClassMasteryPointVO> statClassMastery(@Param("classId") Long classId,
                                               @Param("threshold") int threshold);
}
