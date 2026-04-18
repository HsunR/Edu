package com.gpnu.resource.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;



@Slf4j
@Component
public class DailyJob {



    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨1点执行
    public void cleanFailedUploadVideos() {

    }

}
