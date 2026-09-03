package com.gpnu.resource.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO connection and bucket settings.
 *
 * <p>The internal endpoint is used by the resource service for metadata and delete operations.
 * The public endpoint is used only when signing browser-facing upload URLs.</p>
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    private String endpoint = "http://localhost:9000";

    private String publicEndpoint = "http://localhost:9000";

    private String accessKey = "intelli-edu";

    private String secretKey = "intelli-edu-dev";

    private String bucket = "intelli-edu-resources";

    private String region = "us-east-1";

    private int presignExpiryMinutes = 15;

    private boolean autoCreateBucket = true;

    /**
     * Local development keeps stable access URLs for avatars and course covers.
     * Production deployments should normally disable this and issue signed GET URLs instead.
     */
    private boolean publicRead = true;

}
