package com.gpnu.model.entity.resourceModel;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 资源表
 * @TableName co_resource
 */
@TableName(value ="co_resource")
@Data
public class CoResource implements Serializable {
    /**
     * 资源ID，主键
     */
    @TableId(type= IdType.ASSIGN_ID)
    private Long resourceId;

    /**
     * 资源名称
     */
    private String resourceName;

    /**
     * 课程管理（外键关联到co_course）
     */
    private Long courseId;

    /**
     * 资源类型
     */
    private Integer type;

    /**
     * 资源链接
     */
    private String resourceLink;

    /**
     * 资源在服务器上的唯一标识
     */
    private String resourceUuid;

    /**
     * 上传状态
     * 0-待上传，1-上传成功，2-上传失败，
     */
    private Integer uploadStatus;

    /**
     * 乐观锁标志
     */
    @Version
    private Integer version;

    /**
     * 逻辑删除标志
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}