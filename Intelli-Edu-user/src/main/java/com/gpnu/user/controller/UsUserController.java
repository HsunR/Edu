package com.gpnu.user.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gpnu.common.common.BaseResponse;
import com.gpnu.common.common.ResultUtils;
import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.model.dto.userModel.ususer.UsUserQueryRequest;
import com.gpnu.model.dto.userModel.ususer.UsUserUpdateRequest;
import com.gpnu.model.entity.userModel.UsUser;
import com.gpnu.user.model.vo.UsUserVO;
import com.gpnu.user.service.UsUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
@Tag(name = "用户相关接口", description = "提供用户增删改查等功能")
public class UsUserController {

    @Resource
    private UsUserService usUserService;


    @GetMapping("/getUserInfo")
    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户信息")
    public BaseResponse<UsUserVO> getUserInfo(@RequestBody String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        UsUserVO userInfo = usUserService.getUserInfoById(userId);
        if (userInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户信息未找到");
        }
        return ResultUtils.success(userInfo);
    }

    @PostMapping("/list")
    @Operation(summary = "多条件查询", description = "多条件查询")
    public BaseResponse<Page<UsUserVO>> listUsers(@RequestBody UsUserQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "查询条件不能为空");
        }
        Page<UsUserVO> userPage = usUserService.listUsers(queryRequest);
        if (userPage == null || userPage.getRecords().isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未找到符合条件的用户");
        }
        return ResultUtils.success(userPage);
    }
    
    @PostMapping
    @Operation(summary = "更新用户信息", description = "根据用户ID更新用户信息")
    public BaseResponse<Boolean> updateUserInfo(@RequestBody UsUserUpdateRequest updateRequest) {
        if (updateRequest == null || updateRequest.getUserId() == null || updateRequest.getUserId().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户信息或用户ID不能为空");
        }
        UsUser newUser = new UsUser();
        BeanUtils.copyProperties(updateRequest, newUser);
        boolean isUpdated = usUserService.updateById(newUser);
        if (!isUpdated) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户未找到或更新失败");
        }
        return ResultUtils.success(true);
    }
    
    @PostMapping("/deleteUser")
    @Operation(summary = "删除用户", description = "根据用户ID删除用户")
    public BaseResponse<Boolean> deleteUser(@RequestBody String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        boolean isDeleted = usUserService.removeById(userId);
        if (!isDeleted) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户未找到或删除失败");
        }
        return ResultUtils.success(true);
    }
    



}
