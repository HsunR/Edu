package com.gpnu.resource.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.exception.ThrowUtils;
import com.gpnu.model.entity.resourceModel.CoResource;
import com.gpnu.resource.model.dto.resource.UploadResult;
import com.gpnu.resource.model.vo.coResource.CoResourceVO;
import com.gpnu.resource.service.CoResourceService;
import com.gpnu.resource.mapper.CoResourceMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
* @author Chenxingdong
* @description 针对表【co_resource(资源表)】的数据库操作Service实现
* @createDate 2025-07-11 18:41:14
*/
@Service
public class CoResourceServiceImpl extends ServiceImpl<CoResourceMapper, CoResource>
    implements CoResourceService{

    @Override
    public CoResourceVO addResource(UploadResult uploadResult) {
        CoResource coResource = new CoResource();
        BeanUtils.copyProperties(uploadResult, coResource);
        boolean saveResult = this.save(coResource);
        ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR);
        CoResourceVO coResourceVO = new CoResourceVO();
        BeanUtils.copyProperties(coResource, coResourceVO);
        return coResourceVO;
    }
}




