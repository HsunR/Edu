package com.gpnu.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gpnu.common.model.dto.userModule.ususer.UsUserQueryRequest;
import com.gpnu.common.model.dto.userModule.ususer.UsUserUpdateRequest;
import com.gpnu.common.model.entity.userModel.UsUser;
import com.gpnu.user.model.dto.ususer.RegisterRequest;
import com.gpnu.user.model.vo.UsUserVO;

/**
* @author Chenxingdong
* @description 针对表【us_user(用户详情表)】的数据库操作Service
* @createDate 2025-07-03 23:08:22
*/
public interface UsUserService extends IService<UsUser> {

    public UsUser getByUsername(String username);
    public UsUser getByMobile(String mobile);
    public UsUser getByEmail(String email);
    public UsUser getByUserNameAndPassword(String username, String password);
    public UsUser getByOpenId(String openId); // 新增OpenID查询方法
    public UsUserVO updateUserInfoById(UsUserUpdateRequest updateRequest);
    public boolean existsByUsername(String username);
    public boolean existsByMobile(String mobile);
    public boolean existsByEmail(String email);
    public boolean existsByOpenId(String openId); // 新增OpenID存在性检查
    public boolean registerUser(RegisterRequest request); // 注册用户方法

    public UsUserVO getUserInfoById(Long userId);

    public Page<UsUserVO> listUsers(UsUserQueryRequest queryRequest);


}
