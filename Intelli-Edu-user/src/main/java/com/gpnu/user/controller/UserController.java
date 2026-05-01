package com.gpnu.user.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gpnu.auth.common.constants.AuthConstants;
import com.gpnu.auth.resource.context.UserContextHolder;
import com.gpnu.common.common.BaseResponse;
import com.gpnu.common.common.ResultUtils;
import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.exception.ThrowUtils;
import com.gpnu.user.config.UserDefaultsConfig;
import com.gpnu.user.model.dto.ususer.*;
import com.gpnu.user.model.vo.user.UserDetailVO;
import com.gpnu.user.model.vo.user.UserVO;
import com.gpnu.user.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Slf4j
@Tag(name = "用户相关接口", description = "提供用户增删改查等功能")
public class UserController {

    @Resource
    private IUserService userService;



    @GetMapping("/{userId}")
    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户信息")
    public UserVO getUserInfo(@PathVariable Long userId) {
        if (userId == null ) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        UserVO userInfo = userService.getUserInfoById(userId);
        if (userInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户信息未找到");
        }
        return userInfo;
    }




    @GetMapping
    @Operation(summary = "多条件查询", description = "多条件查询")
    public Page<UserVO> listUsers(UserQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "查询条件不能为空");
        }
        Page<UserVO> userPage = userService.listUsers(queryRequest);
        if (userPage == null || userPage.getRecords().isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未找到符合条件的用户");
        }
        return userPage;
    }


    

    @Operation(summary = "更新用户基本信息", description = "根据用户ID更新用户信息")
    @PutMapping("/me")
    public void updateCurrentUser(@RequestBody @Validated UserUpdateRequest request) {
        Long userId = UserContextHolder.getUserId();
        userService.updateUserInfo(userId, request);
    }
    
    @DeleteMapping("/{userId}")
    @Operation(summary = "删除用户", description = "根据用户ID删除用户")
    public void deleteUser(@PathVariable Long userId) {
        //todo 以后需要校验当前用户是否为管理员
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR, "用户ID不能为空或无效");
        boolean isDeleted = userService.removeById(userId);
        if (!isDeleted) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户未找到或删除失败");
        }
    }


    @Operation(summary = "更新用户头像", description = "根据用户ID更新用户头像")
    @PutMapping("/me/avatar")
    public void updateAvatar(@RequestParam @NotBlank String avatarUrl) {
        Long userId = UserContextHolder.getUserId();
        userService.updateAvatar(userId, avatarUrl);
    }



    @GetMapping("/me")
    @Operation(summary="获取当前登录用户信息")
    public UserDetailVO getCurrentUser() {
        Long userId = UserContextHolder.getUserId();
        return userService.getUserDetail(userId);
    }




    @Operation(summary = "更新当前用户档案信息")
    @PutMapping("/me/profile")
    public void updateProfile(@RequestBody @Validated ProfileUpdateRequest request) {
        Long userId = UserContextHolder.getUserId();
         userService.updateProfile(userId, request);
    }



    @Operation(summary = "修改密码",description = "根据用户ID修改用户密码")
    @PutMapping("/me/password")
    public void updatePassword(@RequestBody @Validated PasswordUpdateRequest request){
        Long userId = UserContextHolder.getUserId();
        userService.updatePassword(userId, request);
    }


    @Operation(summary = "分配教师角色",description = "管理员将用户提升为教师")
    @PutMapping("/{userId}/assign-teacher")
    public void assignTeacher(@RequestBody AssignTeacherRequest request){
        Long currentLoginUser = UserContextHolder.getUserId();
        Integer userType = Integer.valueOf(UserContextHolder.getUserType());
        ThrowUtils.throwIf(!userType.equals(AuthConstants.ROLE_ADMIN),ErrorCode.NO_AUTH_ERROR,"只有管理员才能分配教师角色");
        userService.assignTeacher(request);
    }






}
