package com.gpnu.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.gpnu.model.dto.userModel.ususer.UsUserQueryRequest;
import com.gpnu.model.entity.userModel.UsUser;
import com.gpnu.user.mapper.UsUserMapper;
import com.gpnu.user.model.dto.ususer.RegisterRequest;
import com.gpnu.user.model.vo.UsUserVO;
import com.gpnu.user.service.UsUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* @author Chenxingdong
* @description 针对表【us_user(用户详情表)】的数据库操作Service实现
* @createDate 2025-07-03 23:08:22
*/
@Service
public class UsUserServiceImpl extends ServiceImpl<UsUserMapper, UsUser>
    implements UsUserService {


     public final String DEFAULT_HEAD_PORTRAIT = "https://intelli-edu-backend-1332962051.cos.ap-guangzhou.myqcloud.com/images/default_avatar.webp"; // 默认头像链接


    @Override
    public UsUserVO getUserInfoById(Long userId) {
        UsUser usUser = getOne(new QueryWrapper<UsUser>().eq("user_id", userId).eq("is_delete", 0));
        if (usUser == null) {
            return null;
        }
        UsUserVO usUserVO = new UsUserVO();
        BeanUtils.copyProperties(usUser, usUserVO);
        return usUserVO;
    }



    @Override
    public UsUser getByUsername(String username) {
        return getOne(new QueryWrapper<UsUser>().eq("username", username).eq("is_delete", 0));
    }

    @Override
    public UsUser getByMobile(String mobile) {
        return getOne(new QueryWrapper<UsUser>().eq("mobile", mobile).eq("is_delete", 0));
    }

    @Override
    public UsUser getByEmail(String email) {
        return getOne(new QueryWrapper<UsUser>().eq("email", email).eq("is_delete", 0));
    }

    @Override
    public UsUser getByOpenId(String openId) {
        return getOne(new QueryWrapper<UsUser>().eq("open_id", openId).eq("is_delete", 0));
    }

    @Override
    public boolean existsByUsername(String username) {
        return count(new QueryWrapper<UsUser>().eq("username", username).eq("is_delete", 0)) > 0;
    }

    @Override
    public boolean existsByMobile(String mobile) {
        return count(new QueryWrapper<UsUser>().eq("mobile", mobile).eq("is_delete", 0)) > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        return count(new QueryWrapper<UsUser>().eq("email", email).eq("is_delete", 0)) > 0;
    }

    @Override
    public boolean existsByOpenId(String openId) {
        return count(new QueryWrapper<UsUser>().eq("open_id", openId).eq("is_delete", 0)) > 0;
    }


    @Override
    public boolean registerUser(RegisterRequest registerRequest) {
        UsUser user = new UsUser();
        BeanUtils.copyProperties(registerRequest, user);
        user.setPassword(getEncryptPassword(user.getPassword()));
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setHeadPortrait(DEFAULT_HEAD_PORTRAIT);
        user.setPersonalSignature("这个人很懒，什么都没有留下~");
        return this.save(user);
    }


    private String getEncryptPassword(String userPassword) {
        //加盐,混淆密码
        final String SALT = "Intelli-Edu-Salt"; // 加盐
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }


    @Override
    public Page<UsUserVO> listUsers(UsUserQueryRequest queryRequest) {
        Integer current = queryRequest.getCurrent();
        Integer pageSize = queryRequest.getPageSize();
        QueryWrapper<UsUser> queryWrapper = getQueryWrapper(queryRequest);
        Page<UsUser> usUserPage =this.page(new Page<>(current, pageSize), queryWrapper);
        Page<UsUserVO> usUserVOPage = new Page<>(current, pageSize, usUserPage.getTotal());
        // 将用户数据转换为VO
        List<UsUserVO> usUserVOS =new ArrayList<>();
        for (UsUser usUser : usUserPage.getRecords()) {
            UsUserVO usUserVO = new UsUserVO();
            BeanUtils.copyProperties(usUser, usUserVO);
            usUserVOS.add(usUserVO);
        }
        usUserVOPage.setRecords(usUserVOS);
        return usUserVOPage;
    }

    private QueryWrapper<UsUser> getQueryWrapper(UsUserQueryRequest queryRequest){
        QueryWrapper<UsUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(queryRequest.getUserId() != null, "user_id", queryRequest.getUserId());
        queryWrapper.like(queryRequest.getName() != null, "name", queryRequest.getName());
        queryWrapper.eq(queryRequest.getType() != null, "type", queryRequest.getType());
        queryWrapper.eq(queryRequest.getSex() != null, "sex", queryRequest.getSex());
        queryWrapper.eq(queryRequest.getMobile() != null, "mobile", queryRequest.getMobile());
        queryWrapper.eq(queryRequest.getEmail() != null, "email", queryRequest.getEmail());
        queryWrapper.eq(queryRequest.getSchool() != null, "school", queryRequest.getSchool());
        queryWrapper.eq("is_delete", 0); // 默认查询未删除的用户
        return queryWrapper;
    }
}




