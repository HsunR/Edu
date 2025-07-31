package com.gpnu.common.model.entity.courseModel;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 课程章节表
 * @TableName co_chapter
 */
@TableName(value ="co_chapter")
@Data
public class CoChapter implements Serializable {
    /**
     * 章节唯一标识符（主键）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 章节名称
     */
    private String name;

    /**
     * 父章节id（首级章节设置默认值-1）
     */
    private Long pid;

    /**
     * 所属课程Id（外键到co_course）
     */
    private Long courseId;

    /**
     * 章节在课程中的顺序
     */
    private Integer chapterOrder;

    /**
     * 章节层级
     */
    private Integer hierarchy;

    /**
     * 乐观锁版本控制
     */
    private Integer version;

    /**
     * 逻辑删除标志
     */
    private Integer isDelete;

    /**
     * 记录创建时间戳
     */
    private Date createTime;

    /**
     * 最后更新时间戳
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}