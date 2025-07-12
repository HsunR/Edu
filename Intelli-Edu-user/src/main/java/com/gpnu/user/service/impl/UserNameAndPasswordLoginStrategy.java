package com.gpnu.user.service.impl;

import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.jwt.JwtTokenProvider;
import com.gpnu.common.service.RedisService;
import com.gpnu.model.entity.userModel.UsUser;
import com.gpnu.user.model.dto.ususer.LoginRequest;
import com.gpnu.user.model.enums.LoginTypeEnum;
import com.gpnu.user.model.vo.LoginResult;
import com.gpnu.user.service.LoginStrategy;
import com.gpnu.user.service.UsUserService;
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
    private RedisService redisService;

    @Resource
    private UsUserService usUserService;

    @Resource
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public LoginTypeEnum getSupportedLoginType() {
        return LoginTypeEnum.USERNAME_PASSWORD;
    }

    @Override
    public boolean supports(LoginRequest request) {
        return request.getLoginType().equals(LoginTypeEnum.USERNAME_PASSWORD.getCode());
    }

    @Override
    public LoginResult authenticate(LoginRequest request) {
        if (request.getUsername() == null || request.getPassword() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户名或密码不能为空");
        }
        if(request.getUsername().isEmpty() || request.getPassword().isEmpty()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户名或密码不能为空");
        }

        UsUser user = usUserService.getByUserNameAndPassword(request.getUsername(), request.getPassword());
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        log.info("用户 {} 通过账号和密码登录成功", user.getUserId());
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUserId(), user.getType());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId());
        return new LoginResult(user.getUserId(), user.getType(), accessToken, refreshToken);
    }
}
