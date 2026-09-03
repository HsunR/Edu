package com.gpnu.resource.service.impl;

import com.gpnu.common.exception.BusinessException;
import com.gpnu.resource.manager.MinioManager;
import com.gpnu.resource.mapper.RsResourceMapper;
import com.gpnu.resource.mapper.RsVideoMetaMapper;
import com.gpnu.resource.model.dto.PresignRequest;
import com.gpnu.resource.model.dto.UploadConfirmRequest;
import com.gpnu.resource.model.entity.RsResource;
import com.gpnu.resource.model.entity.RsVideoMeta;
import com.gpnu.resource.model.enums.ResourceType;
import com.gpnu.resource.model.enums.UploadStatus;
import com.gpnu.resource.model.vo.PresignedUrlVO;
import io.minio.StatObjectResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceServiceImplTest {

    @Mock
    private MinioManager minioManager;

    @Mock
    private RsResourceMapper resourceMapper;

    @Mock
    private RsVideoMetaMapper videoMetaMapper;

    private ResourceServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ResourceServiceImpl();
        ReflectionTestUtils.setField(service, "baseMapper", resourceMapper);
        ReflectionTestUtils.setField(service, "minioManager", minioManager);
        ReflectionTestUtils.setField(service, "videoMetaMapper", videoMetaMapper);
    }

    @Test
    void shouldCreatePendingVideoAndReturnMinioUploadUrl() {
        PresignRequest request = new PresignRequest();
        request.setFileName("lesson.mp4");
        request.setFileSize(1024L);

        when(resourceMapper.insert((RsResource) any())).thenAnswer(invocation -> {
            RsResource resource = invocation.getArgument(0);
            resource.setResourceId(100L);
            return 1;
        });
        when(minioManager.generatePresignedUploadUrl(any())).thenReturn("http://localhost:9000/upload");
        when(minioManager.getFileAccessUrl(any())).thenReturn("http://localhost:9000/file");
        when(minioManager.getPresignExpirySeconds()).thenReturn(900L);

        PresignedUrlVO result = service.generatePresignedUrl(7L, request, ResourceType.VIDEO);

        assertEquals(100L, result.getResourceId());
        assertEquals("http://localhost:9000/upload", result.getUploadUrl());
        assertEquals(900L, result.getExpiresIn());
        assertTrue(result.getStorageKey().startsWith("videos/"));
        verify(resourceMapper).insert(argThat((RsResource resource) ->
                resource.getUploaderId().equals(7L)
                        && resource.getFileSize().equals(1024L)
                        && UploadStatus.PENDING.equals(resource.getUploadStatus())));
        verify(videoMetaMapper).insert(argThat((RsVideoMeta meta) -> meta.getResourceId().equals(100L)));
    }

    @Test
    void shouldConfirmOnlyAfterMinioObjectSizeMatches() {
        RsResource resource = pendingResource(100L, 7L, ResourceType.IMAGE, 256L);
        StatObjectResponse metadata = org.mockito.Mockito.mock(StatObjectResponse.class);
        when(resourceMapper.selectById(100L)).thenReturn(resource);
        when(minioManager.getObjectMetadata(resource.getStorageKey())).thenReturn(metadata);
        when(metadata.size()).thenReturn(256L);
        when(minioManager.getFileAccessUrl(resource.getStorageKey())).thenReturn("http://localhost:9000/file");
        when(resourceMapper.updateById(resource)).thenReturn(1);

        UploadConfirmRequest request = new UploadConfirmRequest();
        request.setResourceId(100L);
        service.confirmUpload(7L, request);

        assertEquals(UploadStatus.SUCCESS, resource.getUploadStatus());
        assertEquals("http://localhost:9000/file", resource.getAccessUrl());
        verify(minioManager, never()).deleteObject(any());
    }

    @Test
    void shouldRejectConfirmationFromAnotherUserBeforeReadingMinio() {
        RsResource resource = pendingResource(100L, 7L, ResourceType.DOCUMENT, 256L);
        when(resourceMapper.selectById(100L)).thenReturn(resource);

        UploadConfirmRequest request = new UploadConfirmRequest();
        request.setResourceId(100L);

        assertThrows(BusinessException.class, () -> service.confirmUpload(8L, request));
        verify(minioManager, never()).getObjectMetadata(any());
    }

    @Test
    void shouldDeleteMismatchedObjectAndRejectConfirmation() {
        RsResource resource = pendingResource(100L, 7L, ResourceType.IMAGE, 256L);
        StatObjectResponse metadata = org.mockito.Mockito.mock(StatObjectResponse.class);
        when(resourceMapper.selectById(100L)).thenReturn(resource);
        when(minioManager.getObjectMetadata(resource.getStorageKey())).thenReturn(metadata);
        when(metadata.size()).thenReturn(255L);
        when(resourceMapper.updateById(resource)).thenReturn(1);

        UploadConfirmRequest request = new UploadConfirmRequest();
        request.setResourceId(100L);

        assertThrows(BusinessException.class, () -> service.confirmUpload(7L, request));
        assertEquals(UploadStatus.FAILED, resource.getUploadStatus());
        verify(minioManager).deleteObject(resource.getStorageKey());
    }

    private RsResource pendingResource(
            Long resourceId,
            Long uploaderId,
            ResourceType resourceType,
            Long fileSize) {
        RsResource resource = new RsResource();
        resource.setResourceId(resourceId);
        resource.setUploaderId(uploaderId);
        resource.setResourceType(resourceType);
        resource.setFileSize(fileSize);
        resource.setStorageKey("objects/test-file");
        resource.setUploadStatus(UploadStatus.PENDING);
        return resource;
    }
}
