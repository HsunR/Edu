package com.gpnu.resource.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MinioClientConfig {

    @Bean
    @Primary
    public MinioClient minioClient(MinioProperties properties) {
        return buildClient(properties.getEndpoint(), properties);
    }

    @Bean
    @Qualifier("minioPresignClient")
    public MinioClient minioPresignClient(MinioProperties properties) {
        return buildClient(properties.getPublicEndpoint(), properties);
    }

    private MinioClient buildClient(String endpoint, MinioProperties properties) {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .region(properties.getRegion())
                .build();
    }
}
