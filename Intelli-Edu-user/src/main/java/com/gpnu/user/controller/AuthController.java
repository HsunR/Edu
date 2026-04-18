package com.gpnu.user.controller;

import com.gpnu.auth.common.constants.AuthConstants;
import com.gpnu.common.common.BaseResponse;
import com.gpnu.common.common.ResultUtils;
import com.gpnu.common.constants.Constant;
import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.utils.validator.ValidationUtil;
import com.gpnu.user.model.dto.ususer.LoginRequest;
import com.gpnu.user.model.dto.ususer.SendLoginCodeRequest;
import com.gpnu.user.model.dto.ususer.SendRegisterCodeRequest;
import com.gpnu.user.model.dto.ususer.RegisterRequest;
import com.gpnu.user.model.enums.LoginType;
import com.gpnu.user.model.enums.RegisterType;
import com.gpnu.user.model.vo.LoginResult;
import com.gpnu.user.service.impl.LoginService;
import com.gpnu.user.service.impl.VerificationCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
@Slf4j
@Tag(name = "AuthController", description = "提供用户注册、登录等功能")
public class AuthController {


    @Resource
    private LoginService loginService;



    @Resource
    private VerificationCodeService verificationCodeService;

    /**
     * 用户登录 (支持多种登录策略)
     * POST /user/login
     *
     * @param request 包含登录类型及凭证信息
     * @return BaseResponse<LoginResult> 包含JWT令牌
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public BaseResponse<LoginResult> login(@RequestBody @Validated LoginRequest request, HttpServletRequest servletRequest) {
        log.info("接收到用户登录请求，登录类型：{}", request.getLoginType());
        LoginType loginType = request.getLoginType();
        if (loginType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的登录类型");
        }

        log.info("Header requestId = {}", servletRequest.getHeader(Constant.REQUEST_ID_HEADER));
        if(loginType == LoginType.MOBILE_CODE){
            ValidationUtil.validateAndThrow(request, LoginRequest.MobileCodeGroup.class);
        }else if(loginType == LoginType.EMAIL_CODE){
            ValidationUtil.validateAndThrow(request, LoginRequest.EmailCodeGroup.class);
        }else if(loginType == LoginType.USERNAME_PASSWORD){
            ValidationUtil.validateAndThrow(request, LoginRequest.UsernamePasswordGroup.class);
        }

        LoginResult loginResult = loginService.login(request);
        log.info("用户登录成功，用户ID：{}", loginResult.getUserId());
        return ResultUtils.success(loginResult);

    }

    /**
     * 用户注册
     * POST /user/register
     *
     * @param request 包含注册信息及验证码
     * @return BaseResponse<Void>
     */
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public BaseResponse<Boolean> register(@RequestBody @Validated RegisterRequest request) {
        log.info("接收到用户注册请求：{}", request.getName());
        // 根据注册类型动态选择
        verificationCodeService.verifyRegisterAndRegisterUser(request);
        return ResultUtils.success(true);
    }


    /**
     * 用户注销 (将Access Token加入黑名单，并移除Refresh Token)
     * POST /user/logout
     * 通常通过前端在用户点击注销时清除本地存储的token，并发送此请求给后端
     *
     * @param authorizationHeader HTTP Authorization 头，包含 Bearer Access Token
     * @return BaseResponse<Void>
     */
    @Operation(summary = "用户注销")
    @PostMapping("/logout")
    public void logout(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(AuthConstants.BEARER_PREFIX)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无效的Authorization头");
        }
        String accessToken = authorizationHeader.substring(AuthConstants.BEARER_PREFIX_LENGTH); // 提取Bearer后的Token
        log.info("接收到用户注销请求，Access Token: {}", accessToken);
        loginService.logout(accessToken);
    }

    /**
     * 发送注册验证码（手机或邮箱）
     * POST /user/register/send-code
     *
     * @param request 包含注册类型（手机/邮箱）及对应账号
     * @return BaseResponse<Void>
     */
    @Operation(summary = "发送注册验证码")
    @PostMapping("/register/send-code")
    public void sendRegisterCode(@RequestBody SendRegisterCodeRequest request) {
        log.info("接收到发送注册验证码请求：{}", request);
        RegisterType registerType = request.getRegisterType();
        if (registerType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的注册验证码类型");
        }

        if(registerType == RegisterType.MOBILE_CODE){
            ValidationUtil.validateAndThrow(request, SendRegisterCodeRequest.MobileGroup.class);
            verificationCodeService.sendRegisterVerificationCode(request);
        }else if(registerType == RegisterType.EMAIL_CODE){
            // 手动校验邮箱格式
            ValidationUtil.validateAndThrow(request, SendRegisterCodeRequest.EmailGroup.class);
            verificationCodeService.sendRegisterVerificationCode(request);
        } else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的注册验证码类型");

        }
    }



    /**
     * 发送登录验证码（手机或邮箱）
     * POST /user/login/send-code
     *
     * @param request 包含登录类型及对应账号
     * @return BaseResponse<Void>
     */
    @Operation(summary = "发送登录验证码")
    @PostMapping("/login/send-code")
    public void sendLoginCode(@RequestBody SendLoginCodeRequest request) {
        log.info("接收到发送登录验证码请求：{}", request);
        LoginType loginType = LoginType.getByCode(request.getLoginType());
        if (loginType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的登录类型");
        }
        // 根据登录类型动态校验并发送验证码
        if (request.getLoginType() == LoginType.MOBILE_CODE.getCode()) { // 手机登录
            ValidationUtil.validateAndThrow(request, SendLoginCodeRequest.MobileGroup.class);
            verificationCodeService.sendLoginVerificationCode(request.getMobile(), LoginType.MOBILE_CODE.getCode());
        } else if (request.getLoginType() == LoginType.EMAIL_CODE.getCode()) { // 邮箱登录
            ValidationUtil.validateAndThrow(request, SendLoginCodeRequest.EmailGroup.class);
            verificationCodeService.sendLoginVerificationCode(request.getEmail(), LoginType.EMAIL_CODE.getCode());
        } else {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "错误的登录操作");
        }
    }




    /**
     * 刷新Access Token (通过Refresh Token获取新的Access Token)
     * POST /user/refresh-token
     *
     * @param refreshToken 刷新令牌
     * @return BaseResponse<LoginResult> 包含新的JWT令牌
     */
    @Operation(summary = "刷新Access Token")
    @PostMapping("/refresh-token")
    public BaseResponse<LoginResult> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        log.info("接收到刷新令牌请求");
        try {
            LoginResult loginResult = loginService.refreshToken(refreshToken);
            return ResultUtils.success(loginResult);
        } catch (BusinessException e) {
            log.warn("刷新令牌失败：{}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("刷新令牌时发生未知异常", e);
        }
        return null;
    }

}
