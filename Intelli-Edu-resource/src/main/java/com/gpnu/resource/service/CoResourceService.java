package com.gpnu.resource.service;

import com.gpnu.common.model.entity.resourceModel.CoResource;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gpnu.resource.model.dto.resource.UploadResult;
import com.gpnu.resource.model.vo.coResource.CoResourceVO;

/**
* @author Chenxingdong
* @description 针对表【co_resource(资源表)】的数据库操作Service
* @createDate 2025-07-11 18:41:14
*/
public interface CoResourceService extends IService<CoResource> {


    public CoResourceVO addResource(UploadResult uploadResult);


    public CoResourceVO addResource(CoResource coResource);

    public CoResourceVO updateResource(CoResource coResource);


}
