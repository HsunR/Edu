package com.gpnu.resource.schedule;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gpnu.model.entity.resourceModel.CoResource;
import com.gpnu.resource.manager.upload.VideoUploadTemplate;
import com.gpnu.resource.model.enums.UploadStatusEnum;
import com.gpnu.resource.service.CoResourceService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class DailyJob {

    @Resource
    private CoResourceService coResourceService;

    @Resource
    private VideoUploadTemplate videoUploadTemplate;

    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨1点执行
    public void cleanFailedUploadVideos() {
        List<CoResource> failedResources = coResourceService
                .list(new LambdaQueryWrapper<CoResource>()
                        .eq(CoResource::getUploadStatus, UploadStatusEnum.FAILED.getCode())
                        .lt(CoResource::getCreateTime, LocalDateTime.now().minusDays(1)));

        for (CoResource resource : failedResources) {
            try {
                if (StrUtil.isNotBlank(resource.getResourceUuid())) {
                    videoUploadTemplate.deleteObject(resource.getResourceUuid());
                }
                coResourceService.removeById(resource.getResourceId());
                log.info("已清理失败视频资源：{}", resource.getResourceId());
            } catch (Exception e) {
                log.warn("清理失败资源时出错：{}", resource.getResourceId(), e);
            }
        }
    }

}
