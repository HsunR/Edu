package com.gpnu.resource.manager;

import com.gpnu.resource.config.MinioClientConfig;
import com.gpnu.resource.config.MinioProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MinioManagerIntegrationTest {

    @Test
    @EnabledIfEnvironmentVariable(named = "MINIO_IT", matches = "true")
    void shouldInitializeBucketUploadThroughPresignedUrlAndReadPublicObject() throws Exception {
        MinioProperties properties = new MinioProperties();
        MinioClientConfig clientConfig = new MinioClientConfig();
        MinioManager manager = new MinioManager(
                clientConfig.minioClient(properties),
                clientConfig.minioPresignClient(properties),
                properties);

        manager.initializeBucket();
        String objectKey = "integration/minio-basic-use.txt";
        byte[] content = "Intelli-Edu MinIO integration".getBytes(StandardCharsets.UTF_8);

        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest uploadRequest = HttpRequest.newBuilder(URI.create(manager.generatePresignedUploadUrl(objectKey)))
                    .header("Content-Type", "text/plain")
                    .PUT(HttpRequest.BodyPublishers.ofByteArray(content))
                    .build();
            HttpResponse<Void> uploadResponse = httpClient.send(
                    uploadRequest, HttpResponse.BodyHandlers.discarding());

            assertEquals(200, uploadResponse.statusCode());
            assertEquals(content.length, manager.getObjectMetadata(objectKey).size());

            HttpResponse<byte[]> readResponse = httpClient.send(
                    HttpRequest.newBuilder(URI.create(manager.getFileAccessUrl(objectKey))).GET().build(),
                    HttpResponse.BodyHandlers.ofByteArray());
            assertEquals(200, readResponse.statusCode());
            assertTrue(java.util.Arrays.equals(content, readResponse.body()));
        } finally {
            manager.deleteObject(objectKey);
        }
    }
}
