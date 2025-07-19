package com.gpnu.user.service.impl;


import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.jwt.JwtTokenProvider;
import com.gpnu.common.service.RedisService;
import com.gpnu.common.model.entity.userModel.UsUser;
import com.gpnu.user.constants.CaptchaConstants;
import com.gpnu.user.model.dto.ususer.LoginRequest;
import com.gpnu.user.model.enums.LoginTypeEnum;
import com.gpnu.user.model.vo.LoginResult;
import com.gpnu.user.service.LoginStrategy;
import com.gpnu.user.service.UsUserService;
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
    private UsUserService usUserService;

    @Resource
    private JwtTokenProvider jwtTokenProvider;



    @Override
    public boolean supports(LoginRequest request) {
        return request.getLoginType() == getSupportedLoginType().getCode();
    }

    @Override
    public LoginTypeEnum getSupportedLoginType() {
        return LoginTypeEnum.EMAIL_CODE;
    }

    @Override
    public LoginResult authenticate(LoginRequest request)  {
        if (request.getEmail() == null || request.getCode() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"邮箱或验证码不能为空");
        }

        String storedCode = redisService.getCacheObject(CaptchaConstants.LOGIN_CODE_PREFIX_EMAIL + request.getEmail());
        if (storedCode == null || !storedCode.equals(request.getCode())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误或已过期");
        }
        // 验证成功后立即删除验证码
        redisService.deleteObject(CaptchaConstants.LOGIN_CODE_PREFIX_EMAIL + request.getEmail());
        log.info("邮箱 {} 验证码校验成功", request.getEmail());

        UsUser user = usUserService.getByEmail(request.getEmail());
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        if (user.getIsDelete() != null && user.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "用户已被删除");
        }

        log.info("用户 {} 通过邮箱验证码登录成功", user.getUserId());
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUserId(), user.getType());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId());
        return new LoginResult(user.getUserId(), user.getType(), accessToken, refreshToken);
    }

}