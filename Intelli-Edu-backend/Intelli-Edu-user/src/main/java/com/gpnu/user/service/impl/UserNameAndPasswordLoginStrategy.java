package com.gpnu.user.service.impl;

import com.gpnu.auth.provider.JwtTokenProvider;
import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.user.model.dto.ususer.LoginRequest;
import com.gpnu.user.model.entity.User;
import com.gpnu.user.model.enums.LoginType;
import com.gpnu.user.model.enums.UserStatus;
import com.gpnu.user.model.vo.LoginResult;
import com.gpnu.user.security.PasswordEncoder;
import com.gpnu.user.service.LoginStrategy;
import com.gpnu.user.service.IUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 用户名密码登录策略
 */
@Component
@Slf4j
public class UserNameAndPasswordLoginStrategy implements LoginStrategy {



    @Resource
    private IUserService iUserService;

    @Resource
    private JwtTokenProvider jwtTokenProvider;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public LoginType getSupportedLoginType() {
        return LoginType.USERNAME_PASSWORD;
    }

    @Override
    public boolean supports(LoginRequest request) {
        return request.getLoginType()==LoginType.USERNAME_PASSWORD;
    }

    @Override
    public LoginResult authenticate(LoginRequest request) {


        User user = iUserService.getByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        if (user.getIsDeleted() != null && user.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "用户已被删除");
        }
        if(user.getStatus()== UserStatus.BAN){
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "用户已被禁用");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名或密码错误");
        }


        log.info("用户 {} 通过账号和密码登录成功", user.getUserId());
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUserId(), user.getUserType().getCode());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId());
        return new LoginResult(user.getUserId(), user.getUserType(), accessToken, refreshToken);
    }
}
