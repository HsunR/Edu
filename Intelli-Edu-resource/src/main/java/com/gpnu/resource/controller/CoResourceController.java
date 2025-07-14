package com.gpnu.resource.controller;

import com.gpnu.common.common.BaseResponse;
import com.gpnu.common.common.ResultUtils;
import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.resource.manager.upload.DocumentUploadTemplate;
import com.gpnu.resource.manager.upload.PictureUploadTemplate;
import com.gpnu.resource.model.dto.pic.UploadPictureResult;
import com.gpnu.resource.model.dto.resource.UploadResult;
import com.gpnu.resource.model.vo.coResource.CoResourceVO;
import com.gpnu.resource.service.CoResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/coresource")
@Tag(name = "CoResourceController", description = "提供资源上传等功能等功能")
@Slf4j
public class CoResourceController {

    @Resource
    private PictureUploadTemplate pictureUploadTemplate;

    @Resource
    private DocumentUploadTemplate documentUploadTemplate;

    @Resource
    private CoResourceService coResourceService;


    @GetMapping("/health")
    public String healthCheck() {
        return "CoResourceController is healthy";
    }

    /**
     * 上传图片
     * POST /resource/upload/picture
     * @param file 图片文件
     * @return BaseResponse<UploadPictureResult> 图片上传结果
     */
    @Operation(summary = "上传图片")
    @PostMapping("/upload/picture")
    public BaseResponse<UploadPictureResult> uploadPicture(@RequestParam("file") MultipartFile file) {
        try {
            // 定义图片在COS中的上传路径前缀
            String uploadPathPrefix = "images/";
            UploadPictureResult result = pictureUploadTemplate.upload(file, uploadPathPrefix);
            return ResultUtils.success(result);
        } catch (BusinessException e) {
            log.error("图片上传失败: {}", e.getMessage(), e);
            return ResultUtils.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("图片上传时发生未知异常", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "图片上传失败，请稍后再试");
        }
    }

    /**
     * 上传文档
     * POST /resource/upload/document
     * @param file 文档文件
     * @return BaseResponse<UploadResult> 文档上传结果
     */
    @Operation(summary = "上传文档")
    @PostMapping("/upload/document")
    public BaseResponse<CoResourceVO> uploadDocument(@RequestParam Long courseId ,@RequestParam("file") MultipartFile file) {

            // 定义文档在COS中的上传路径前缀
            String uploadPathPrefix = "documents/";
            UploadResult result = documentUploadTemplate.upload(file, uploadPathPrefix);
            if (result == null) {
                return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "文档上传失败，请稍后再试");
            }
        CoResourceVO resourceVO = coResourceService.addResource(result);

        return ResultUtils.success(resourceVO);

    }
}
