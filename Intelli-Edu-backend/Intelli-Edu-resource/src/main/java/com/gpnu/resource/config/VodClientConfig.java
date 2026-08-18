package com.gpnu.resource.config;

import com.qcloud.vod.VodUploadClient;
import com.tencentcloudapi.common.Credential; // 引入 Credential
import com.tencentcloudapi.common.profile.ClientProfile; // 引入 ClientProfile
import com.tencentcloudapi.common.profile.HttpProfile; // 引入 HttpProfile
import com.tencentcloudapi.vod.v20180717.VodClient; // 引入 VODClient
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 腾讯云VOD客户端配置
 */
@Configuration
@Data
@Slf4j
public class VodClientConfig {

    @Value("${vod.client.secretId}")
    private String secretId;

    @Value("${vod.client.secretKey}")
    private String secretKey;

    @Value("${vod.client.region}") // VOD API请求的地域，例如 ap-shanghai
    private String region;

    @Value("${vod.client.appId}") // VOD应用ID
    private Long appId;

    @Bean
    public VodUploadClient vodUploadClient() {
        log.info("Initializing Tencent Cloud VOD Upload Client...");
        VodUploadClient client = new VodUploadClient(secretId, secretKey);
        log.info("Tencent Cloud VOD Upload Client initialized successfully.");
        return client;
    }

    @Bean
    public VodClient vodClient() {
        log.info("Initializing Tencent Cloud VOD API Client...");
        // 实例化认证对象
        Credential cred = new Credential(secretId, secretKey);

        // 实例化一个http选项，可选的，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint("vod.tencentcloudapi.com"); // VOD服务域名

        // 实例化一个客户端配置对象
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);

        // 实例化要请求产品的client对象,根据官方文档，需要指定地域
        VodClient client = new VodClient(cred, region, clientProfile);
        log.info("Tencent Cloud VOD API Client initialized successfully.");
        return client;
    }
}