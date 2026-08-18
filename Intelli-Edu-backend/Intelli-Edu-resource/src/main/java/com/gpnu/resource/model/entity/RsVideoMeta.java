package com.gpnu.resource.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;


/**
 * <p>
 * 视频扩展信息表
 * </p>
 *
 * @author chenxingdong
 * @since 2026-04-17
 */
@Data
@TableName("rs_video_meta")
public class RsVideoMeta implements Serializable {

    private static final long serialVersionUID = 1L;



    /**
     * 关联资源ID (外键)
     */
    @TableId(value = "resource_id", type = IdType.INPUT)
    private Long resourceId;

    /**
     * 时长（秒）
     */
    private Integer duration;

    /**
     * 视频封面
     */
    private String coverUrl;

    /**
     * 清晰度
     */
    private String definition;

    /**
     * 0=待处理 1=完成 2=失败
     */
    private Integer transcodeStatus;

    /**
     * 腾讯云 VOD FileId
     */
    private String vodFileId;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;


}
