package com.gpnu.user.service.impl;


import cn.hutool.core.util.StrUtil;
import com.gpnu.auth.provider.JwtTokenProvider;
import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.service.RedisService;
import com.gpnu.user.constants.CaptchaConstants;
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
 * 邮箱验证码登录策略
 */
@Component
@Slf4j
public class EmailCodeLoginStrategy implements LoginStrategy {

    @Resource
    private RedisService redisService;

    @Resource
    private IUserService userService;

    @Resource
    private JwtTokenProvider jwtTokenProvider;



    @Override
    public boolean supports(LoginRequest request) {
        return request.getLoginType() == getSupportedLoginType();
    }

    @Override
    public LoginType getSupportedLoginType() {
        return LoginType.EMAIL_CODE;
    }

    @Override
    public LoginResult authenticate(LoginRequest request)  {
        if (request.getEmail() == null || request.getCode() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"邮箱或验证码不能为空");
        }

        String storedCode = redisService.getCacheObject(CaptchaConstants.LOGIN_CODE_PREFIX_EMAIL + request.getEmail());
        if (StrUtil.isBlank(storedCode) || !storedCode.equals(request.getCode())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误或已过期");
        }
        // 验证成功后立即删除验证码
        redisService.deleteObject(CaptchaConstants.LOGIN_CODE_PREFIX_EMAIL + request.getEmail());
        log.info("邮箱 {} 验证码校验成功", request.getEmail());

        User user = userService.getByEmail(request.getEmail());
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        if (user.getIsDeleted() != null && user.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "用户已被删除");
        }
        if(user.getStatus()== UserStatus.BAN){
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "用户已被禁用");
        }

        log.info("用户 {} 通过邮箱验证码登录成功", user.getUserId());
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUserId(), user.getUserType().getCode());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId());
        return new LoginResult(user.getUserId(), user.getUserType(), accessToken, refreshToken);
    }

}