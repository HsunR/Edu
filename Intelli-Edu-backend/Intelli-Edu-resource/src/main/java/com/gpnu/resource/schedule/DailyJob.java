package com.gpnu.resource.schedule;

import com.gpnu.resource.service.IRsResourceService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;



@Slf4j
@Component
public class DailyJob {

    @Resource
    private IRsResourceService resourceService;

    @Scheduled(cron = "0 0 * * * ?") // 每小时执行一次
    public void cleanExpiredUploadResources() {
        log.info("开始执行定时任务：清理过期未完成上传资源");
        try {
            resourceService.cleanExpiredUploadResources();
            log.info("定时任务执行完成：清理过期未完成上传资源");
        } catch (Exception e) {
            log.error("定时任务执行失败：清理过期未完成上传资源", e);
        }
    }

}
