package com.gpnu.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.gpnu.api.dto.user.UserAuthDTO;
import com.gpnu.api.dto.user.UserSimpleDTO;
import com.gpnu.auth.common.constants.AuthConstants;
import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.exception.ThrowUtils;
import com.gpnu.user.config.UserDefaultsConfig;
import com.gpnu.user.mapper.StudentProfileMapper;
import com.gpnu.user.mapper.TeacherProfileMapper;
import com.gpnu.user.mapper.UserMapper;
import com.gpnu.user.model.dto.ususer.*;

import com.gpnu.user.model.entity.StudentProfile;
import com.gpnu.user.model.entity.TeacherProfile;
import com.gpnu.user.model.entity.User;

import com.gpnu.user.model.enums.UserType;
import com.gpnu.user.model.vo.user.StudentProfileVO;
import com.gpnu.user.model.vo.user.TeacherProfileVO;
import com.gpnu.user.model.vo.user.UserDetailVO;
import com.gpnu.user.model.vo.user.UserVO;
import com.gpnu.user.security.PasswordEncoder;
import com.gpnu.user.service.IUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Chenxingdong
 * @description 针对表【us_user(用户详情表)】的数据库操作Service实现
 * @createDate 2025-07-03 23:08:22
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements IUserService {


    @Resource
    private StudentProfileMapper studentProfileMapper;

    @Resource
    private TeacherProfileMapper teacherProfileMapper;

    @Resource
    private UserDefaultsConfig userDefaultsConfig;

    @Resource
    private UserMapper userMapper;

    @Resource
    private PasswordEncoder passwordEncoder;




    @Override
    public UserVO getUserInfoById(Long userId) {
        User usUser = userMapper.selectById(userId);
        if (usUser == null) {
            return null;
        }
        UserVO usUserVO = new UserVO();
        BeanUtil.copyProperties(usUser, usUserVO);
        return usUserVO;
    }



    @Override
    public User getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<>(User.class)
                .eq(User::getName, username));
    }

    @Override
    public User getByMobile(String mobile) {

        return getOne(new LambdaQueryWrapper<>(User.class)
                .eq(User::getMobile, mobile));
    }

    @Override
    public User getByEmail(String email) {

        return getOne(new LambdaQueryWrapper<>(User.class)
                .eq(User::getEmail, email));
    }

    @Override
    public User getByUserNameAndPassword(String username, String password) {
        String encryptedPassword = passwordEncoder.encode(password);

        return getOne(new LambdaQueryWrapper<>(User.class)
                .eq(User::getName, username)
                .eq(User::getPassword, encryptedPassword));
    }

    @Override
    public User getByOpenId(String openId) {
        return getOne(new LambdaQueryWrapper<>(User.class)
                .eq(User::getOpenId, openId));
    }

    @Override
    public UserVO updateUserInfoById(UserUpdateRequest updateRequest) {
        User user = new User();
        BeanUtil.copyProperties(updateRequest, user);

        //判断用户是否存在
        User existingUser = getById(user.getUserId());
        if(existingUser == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        boolean isUpdated = updateById(user);
        UserVO usUserVO = new UserVO();
        if(isUpdated) {
            User updatedUser = getById(user.getUserId());
            BeanUtil.copyProperties(updatedUser, usUserVO);

        }else{
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新用户信息失败");
        }
        return usUserVO;
    }

    @Override
    public boolean existsByUsername(String username) {
        return exists(new LambdaQueryWrapper<>(User.class).eq(User::getName, username));
    }

    @Override
    public boolean existsByMobile(String mobile) {
        return exists(new LambdaQueryWrapper<>(User.class).eq(User::getMobile, mobile));
    }

    @Override
    public boolean existsByEmail(String email) {
        return exists(new LambdaQueryWrapper<>(User.class).eq(User::getEmail, email)) ;
    }

    @Override
    public boolean existsByOpenId(String openId) {
        return exists(new LambdaQueryWrapper<>(User.class).eq(User::getOpenId, openId)) ;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean registerUser(RegisterRequest registerRequest) {
        User user = new User();
        BeanUtil.copyProperties(registerRequest, user);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());
        user.setAvatarUrl(userDefaultsConfig.getAvatarUrl());
        user.setPersonalSignature(userDefaultsConfig.getPersonalSignature());
        boolean save = this.save(user);
        StudentProfile studentProfile = new StudentProfile();
        BeanUtil.copyProperties(registerRequest, studentProfile);
        studentProfile.setUserId(user.getUserId());
        studentProfileMapper.insert(studentProfile);
        return save;
    }


    @Override
    public Page<UserVO> listUsers(UserQueryRequest queryRequest) {
        Integer current = queryRequest.getCurrent();
        Integer pageSize = queryRequest.getPageSize();
        LambdaQueryWrapper<User> queryWrapper = getQueryWrapper(queryRequest);

        Page<User> usUserPage = this.page(new Page<>(current, pageSize), queryWrapper);
        Page<UserVO> usUserVOPage = new Page<>(current, pageSize, usUserPage.getTotal());

        // 将用户数据转换为VO
        List<UserVO> usUserVOS = new ArrayList<>();
        for (User usUser : usUserPage.getRecords()) {
            UserVO usUserVO = new UserVO();
            BeanUtil.copyProperties(usUser, usUserVO);
            usUserVOS.add(usUserVO);
        }
        usUserVOPage.setRecords(usUserVOS);
        return usUserVOPage;
    }

    private LambdaQueryWrapper<User> getQueryWrapper(UserQueryRequest queryRequest){
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(queryRequest.getUserId() != null, User::getUserId, queryRequest.getUserId());
        queryWrapper.like(StrUtil.isNotBlank(queryRequest.getName()), User::getName, queryRequest.getName());
        queryWrapper.eq(queryRequest.getUserType() != null, User::getUserType, queryRequest.getUserType());
        queryWrapper.eq(queryRequest.getSex() != null, User::getSex, queryRequest.getSex());
        queryWrapper.eq(StrUtil.isNotBlank(queryRequest.getMobile()) , User::getMobile, queryRequest.getMobile());
        queryWrapper.eq(StrUtil.isNotBlank(queryRequest.getEmail()) , User::getEmail, queryRequest.getEmail());
        queryWrapper.eq(StrUtil.isNotBlank(queryRequest.getSchool()) , User::getSchool, queryRequest.getSchool());

        return queryWrapper;
    }


    @Override
    public void updateAvatar(Long userId, String avatarUrl) {
        if (!avatarUrl.startsWith(userDefaultsConfig.getAvatarUrlPrefix())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "非法的头像地址");
        }
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        user.setAvatarUrl(avatarUrl);
        boolean isUpdated = updateById(user);
        if (!isUpdated) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新头像失败");
        }
    }


    @Override
    public UserDetailVO getUserDetail(Long userId) {

        // 1. 查用户基础信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        // 2. 填充基础字段
        UserDetailVO vo = new UserDetailVO();
        BeanUtil.copyProperties(user, vo);
        vo.setMobile(UserDetailVO.maskMobile(user.getMobile()));
        vo.setEmail(UserDetailVO.maskEmail(user.getEmail()));

        // 3. 根据 userType 查询对应档案
        if (AuthConstants.ROLE_STUDENT == user.getUserType().getCode()) {
            StudentProfile profile = studentProfileMapper.selectById(userId);
            if (profile != null) {
                StudentProfileVO profileVO = new StudentProfileVO();
                BeanUtil.copyProperties(profile, profileVO);
                vo.setStudentProfile(profileVO);
            }
        } else if (AuthConstants.ROLE_TEACHER == user.getUserType().getCode()) {
            TeacherProfile profile = teacherProfileMapper.selectById(userId);
            if (profile != null) {
                TeacherProfileVO profileVO = new TeacherProfileVO();
                BeanUtil.copyProperties(profile, profileVO);
                vo.setTeacherProfile(profileVO);
            }
            // 教师可能也有学生档案（从学生提升上来的），一并查询
            StudentProfile studentProfile = studentProfileMapper.selectById(userId);
            if (studentProfile != null) {
                StudentProfileVO spVO = new StudentProfileVO();
                BeanUtil.copyProperties(studentProfile, spVO);
                vo.setStudentProfile(spVO);
            }
        }


        return vo;
    }

    @Override
    public UserDetailVO updateUserInfo(Long userId, UserUpdateRequest request) {
        User user = new User();
        BeanUtil.copyProperties(request, user);
        user.setUserId(userId);
        boolean isUpdated = updateById(user);
        ThrowUtils.throwIf(!isUpdated, ErrorCode.OPERATION_ERROR, "更新用户信息失败");
        return null;
    }

    @Override
    public void updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = getById(userId);
        if(ObjectUtil.isNull(user)){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        if(user.getUserType().getCode() == AuthConstants.ROLE_STUDENT){
            StudentProfile profile = new StudentProfile();
            BeanUtil.copyProperties(request, profile);
            profile.setUserId(userId);
            int result = studentProfileMapper.updateById(profile);
            ThrowUtils.throwIf(result <= 0, ErrorCode.OPERATION_ERROR, "更新学生档案失败");
        }else if(user.getUserType().getCode() == AuthConstants.ROLE_TEACHER){
            TeacherProfile profile = new TeacherProfile();
            BeanUtil.copyProperties(request, profile);
            profile.setUserId(userId);
            int result = teacherProfileMapper.updateById(profile);
            ThrowUtils.throwIf(result <= 0, ErrorCode.OPERATION_ERROR, "更新教师档案失败");
        }else{
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "不支持的用户类型");
        }
        return ;
    }

    @Override
    public void updatePassword(Long userId, PasswordUpdateRequest request) {
            User user = getById(userId);
            if (user == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
            }
            // 验证旧密码
            if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "旧密码不正确");
            }
            // 更新为新密码
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            boolean isUpdated = updateById(user);
            if (!isUpdated) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新密码失败");
            }
    }


    @Override
    public UserSimpleDTO getUserSimple(Long userId) {
        User user = getById(userId);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        UserSimpleDTO simpleDTO = new UserSimpleDTO();
        BeanUtil.copyProperties(user, simpleDTO);
        return simpleDTO;
    }

    @Override
    public List<UserSimpleDTO> getUserSimpleBatch(List<Long> userIds) {
        return List.of();
    }

    @Override
    public UserAuthDTO getUserForAuth(Long userId) {
        User user = getById(userId);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        UserAuthDTO authDTO = new UserAuthDTO();
        BeanUtil.copyProperties(user, authDTO);
        return authDTO;
    }

    @Override
    public UserAuthDTO getUserForAuthByUsername(String username) {
        User user = lambdaQuery().eq(User::getName, username).one();
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        UserAuthDTO authDTO = new UserAuthDTO();
        BeanUtil.copyProperties(user, authDTO);
        return authDTO;
    }

    @Override
    public UserAuthDTO getUserForAuthByEmail(String email) {
        User user = lambdaQuery().eq(User::getEmail, email).one();
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        UserAuthDTO authDTO = new UserAuthDTO();
        BeanUtil.copyProperties(user, authDTO);
        return authDTO;
    }

    @Override
    public UserAuthDTO getUserForAuthByMobile(String mobile) {
        User user = lambdaQuery().eq(User::getMobile, mobile).one();
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        UserAuthDTO authDTO = new UserAuthDTO();
        BeanUtil.copyProperties(user, authDTO);
        return authDTO;
    }

    @Transactional
    @Override
    public void assignTeacher(AssignTeacherRequest request) {


        // 1. 校验目标用户存在且当前是学生
        User targetUser = userMapper.selectById(request.getUserId());
        if (targetUser == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }

        // 2. 更新身份为教师
        targetUser.setUserType(UserType.TEACHER);
        userMapper.updateById(targetUser);

        // 3. 创建教师档案
        TeacherProfile profile = new TeacherProfile();
        profile.setUserId(request.getUserId());
        profile.setTeacherNo(request.getTeacherNo());
        profile.setTitle(request.getTitle());
        profile.setDepartment(request.getDepartment());
        profile.setBio(request.getBio());
        int result = teacherProfileMapper.insert(profile);
        ThrowUtils.throwIf(result <= 0, ErrorCode.OPERATION_ERROR, "创建教师档案失败");

        // 4、删除学生档案
        int deleted = studentProfileMapper.deleteById(request.getUserId());
        ThrowUtils.throwIf(deleted <= 0, ErrorCode.OPERATION_ERROR, "删除学生档案失败");

    }
}