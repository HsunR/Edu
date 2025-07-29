package com.gpnu.resource.controller;

import com.gpnu.common.common.BaseResponse;
import com.gpnu.common.common.ResultUtils;
import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.model.entity.resourceModel.CoResource;
import com.gpnu.resource.manager.upload.DocumentUploadTemplate;
import com.gpnu.resource.manager.upload.PictureUploadTemplate;
import com.gpnu.resource.manager.upload.VideoUploadTemplate;
import com.gpnu.resource.model.dto.pic.UploadPictureResult;
import com.gpnu.common.model.dto.courseModule.resource.UploadResult;
import com.gpnu.resource.model.enums.ResourceType;
import com.gpnu.resource.model.enums.UploadStatusEnum;
import com.gpnu.resource.model.vo.coResource.CoResourceVO;
import com.gpnu.resource.service.CoResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
    private VideoUploadTemplate videoUploadTemplate;

    @Resource
    private CoResourceService coResourceService;




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
    public BaseResponse<CoResourceVO> uploadDocument(@RequestParam(name = "courseId") Long courseId ,@RequestParam("file") MultipartFile file) {

            // 定义文档在COS中的上传路径前缀
        String uploadPathPrefix = "documents/";
        UploadResult result = documentUploadTemplate.upload(file, uploadPathPrefix);
        if (result == null) {
                return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "文档上传失败，请稍后再试");
            }
        CoResourceVO resourceVO = coResourceService.addResource(result);

        return ResultUtils.success(resourceVO);

    }


    /**
     * 上传视频
     * POST /resource/upload/video
     *
     * @param video 视频文件
     * @return BaseResponse<UploadResult> 视频上传结果 (或自定义的 VideoUploadResult)
     */
    @Operation(summary = "上传视频")
    @PostMapping("/upload/video")
    public BaseResponse<CoResourceVO> uploadVideo(@RequestParam("video") MultipartFile video) {
        CoResource coResource = new CoResource();
        try {
            // 视频文件上传不需要指定COS路径前缀，因为它是直接上传到VOD服务，
            // VOD会自行管理存储位置，除非您在VOD上传请求中设置了COS路径作为SourceContext。
            // 这里传递一个空字符串或者null作为uploadPathPrefix，具体取决于VideoUploadTemplate的upload方法如何处理
            //1、先插入 待上传视频的记录

            coResource.setUploadStatus(UploadStatusEnum.PENDING.getCode());
            CoResourceVO coResourceVO = coResourceService.addResource(coResource);
            log.info("待上传视频的资源记录已创建，资源信息: {}", coResourceVO);

            //2、上传视频到腾讯云VOD
            UploadResult result = videoUploadTemplate.upload(video, null);
            if (result == null) {
                return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "视频上传失败，请稍后再试");
            }

            //3、更新资源记录状态为已上传，并返回结果
            BeanUtils.copyProperties(result, coResourceVO);
            BeanUtils.copyProperties(coResourceVO, coResource);
            coResource.setUploadStatus(UploadStatusEnum.SUCCESS.getCode());
            log.info(UploadStatusEnum.getEnumByCode(coResource.getUploadStatus()).getCode() + "状态");
            coResource.setType(ResourceType.getByDescription(result.getType()).getType());
            log.info("视频上传成功，资源信息: {}", coResource);
            CoResourceVO coResourceVO1 = coResourceService.updateResource(coResource);

            return ResultUtils.success(coResourceVO1);
        } catch (Exception e) {
            log.error("视频上传时发生未知异常", e);
            //将上传状态设置为失败
            coResource.setUploadStatus(UploadStatusEnum.FAILED.getCode());
            coResourceService.updateResource(coResource);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "视频上传失败，请稍后再试");
        }
    }

    // TODO: 还可以根据需要添加文件删除、文件信息查询等API接口
    /*
    @ApiOperation("删除文件")
    @DeleteMapping("/delete")
    public BaseResponse<Void> deleteFile(@RequestParam("fileKey") String fileKey) {
        try {
            // 根据fileKey（resource_uuid）和文件类型判断是图片/文档/视频，调用相应的删除逻辑
            // 例如：如果是VOD视频，调用tencentCloudVodManager.deleteVodMedia(fileKey)
            // 如果是COS上的文件，调用cosManager.deleteObject(fileKey)
            // 这需要额外的逻辑来区分fileKey是VOD的FileId还是COS的ObjectKey
            return ResultUtils.success(null, "文件删除成功");
        } catch (BusinessException e) {
            log.error("文件删除失败: {}", e.getMessage(), e);
            return ResultUtils.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("文件删除时发生未知异常", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "文件删除失败，请稍后再试");
        }
    }
    */

}
