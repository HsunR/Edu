package com.gpnu.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gpnu.api.dto.user.UserAuthDTO;
import com.gpnu.api.dto.user.UserSimpleDTO;
import com.gpnu.user.model.dto.ususer.*;
import com.gpnu.user.model.entity.User;

import com.gpnu.user.model.vo.user.UserDetailVO;
import com.gpnu.user.model.vo.user.UserVO;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
* @author Chenxingdong
* @description 针对表【us_user(用户详情表)】的数据库操作Service
* @createDate 2025-07-03 23:08:22
*/
public interface IUserService extends IService<User> {

    public User getByUsername(String username);
    public User getByMobile(String mobile);
    public User getByEmail(String email);
    public User getByUserNameAndPassword(String username, String password);
    public User getByOpenId(String openId); // 新增OpenID查询方法
    public UserVO updateUserInfoById(UserUpdateRequest updateRequest);
    public boolean existsByUsername(String username);
    public boolean existsByMobile(String mobile);
    public boolean existsByEmail(String email);
    public boolean existsByOpenId(String openId); // 新增OpenID存在性检查
    public boolean registerUser(RegisterRequest request); // 注册用户方法

    public UserVO getUserInfoById(Long userId);

    public Page<UserVO> listUsers(UserQueryRequest queryRequest);


    public void updateAvatar(Long userId, @NotBlank String avatarUrl);

    public UserDetailVO getUserDetail(Long userId);

    public UserDetailVO updateUserInfo(Long userId, UserUpdateRequest request);

    public void updateProfile(Long userId, ProfileUpdateRequest request);

    public void updatePassword(Long userId, PasswordUpdateRequest request);

    /**
     * 
     * 以下的接口，都是提供给内部服务的
     * 
     */
    public UserSimpleDTO getUserSimple(Long userId);

    public List<UserSimpleDTO> getUserSimpleBatch(List<Long> userIds);

    public UserAuthDTO getUserForAuth(Long userId);

    public UserAuthDTO getUserForAuthByUsername(String username);

    public UserAuthDTO getUserForAuthByEmail(String email);

    public UserAuthDTO getUserForAuthByMobile(String mobile);
}
