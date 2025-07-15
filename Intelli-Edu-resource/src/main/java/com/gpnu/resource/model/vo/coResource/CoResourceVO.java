package com.gpnu.resource.model.vo.coResource;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class CoResourceVO implements Serializable {

    /**
     * 资源ID，主键
     */
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

}
