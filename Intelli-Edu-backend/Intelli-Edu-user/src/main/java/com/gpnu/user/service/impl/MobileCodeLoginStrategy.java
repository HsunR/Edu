package com.gpnu.user.service.impl;


import com.gpnu.auth.provider.JwtTokenProvider;
import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.service.RedisService;
import com.gpnu.user.model.dto.ususer.LoginRequest;
import com.gpnu.user.model.entity.User;
import com.gpnu.user.model.enums.LoginType;
import com.gpnu.user.model.enums.UserStatus;
import com.gpnu.user.model.vo.LoginResult;
import com.gpnu.user.service.LoginStrategy;
import com.gpnu.user.service.IUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * 手机验证码登录策略
 */
@Component
@Slf4j
public class MobileCodeLoginStrategy implements LoginStrategy {

    @Resource
    private RedisService redisService;
    @Resource
    private IUserService iUserService;

    @Resource
    private JwtTokenProvider jwtTokenProvider;

    private static final String LOGIN_CODE_PREFIX_MOBILE = "login:code:mobile:"; // 登录验证码前缀

    @Override
    public boolean supports(LoginRequest request) {
        return request.getLoginType() == getSupportedLoginType(); // 2代表手机验证码登录
    }

    @Override
    public LoginType getSupportedLoginType() {
        return LoginType.MOBILE_CODE;
    }

    @Override
    public LoginResult authenticate(LoginRequest request)  {
        if (request.getMobile() == null || request.getCode() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号或验证码不能为空");
        }

        String storedCode = redisService.getCacheObject(LOGIN_CODE_PREFIX_MOBILE + request.getMobile());
        if (storedCode == null || !storedCode.equals(request.getCode())) {
           throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误或已过期");
        }
        // 验证成功后立即删除验证码
        redisService.deleteObject(LOGIN_CODE_PREFIX_MOBILE + request.getMobile());
        log.info("手机号 {} 验证码校验成功", request.getMobile());

        User user = iUserService.getByMobile(request.getMobile());
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        if (user.getIsDeleted() != null && user.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "用户已被禁用或删除");
        }
        if(user.getStatus()== UserStatus.BAN){
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "用户已被禁用");
        }

        log.info("用户 {} 通过手机验证码登录成功", user.getUserId());
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUserId(), user.getUserType().getCode());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId());
        return new LoginResult(user.getUserId(), user.getUserType(), accessToken, refreshToken);
    }
}