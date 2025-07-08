package com.gpnu.user.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.service.RedisService;
import com.gpnu.user.constants.CaptchaConstants;
import com.gpnu.user.model.dto.ususer.SendRegisterCodeRequest;
import com.gpnu.user.model.dto.ususer.RegisterRequest;
import com.gpnu.user.model.enums.LoginTypeEnum;
import com.gpnu.user.model.enums.RegisterTypeEnum;
import com.gpnu.user.service.EmailService;
import com.gpnu.user.service.UsUserService;
import jakarta.annotation.Resource;
import jakarta.validation.groups.Default;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import java.util.concurrent.TimeUnit;


/**
 * 验证码发送与校验服务
 */
@Service
@Slf4j
@Validated // 启用Spring的Method Validation
public class VerificationCodeService {

    @Resource
    private RedisService redisService;

    @Resource
    private UsUserService usUserService;

    @Resource
    private EmailService emailService;






    /**
     * 发送注册验证码（手机或邮箱）
     * @param request 发送验证码请求
     */
    public void sendRegisterVerificationCode(@Validated({Default.class, SendRegisterCodeRequest.MobileGroup.class, SendRegisterCodeRequest.EmailGroup.class}) SendRegisterCodeRequest request) {
        String keyPrefix;
        String account;
        Integer type =request.getRegisterType();
        if (type == RegisterTypeEnum.MOBILE_CODE.getCode()) { // 手机注册
            account = request.getMobile();
            keyPrefix = CaptchaConstants.REGISTER_CODE_PREFIX_MOBILE;
            if (usUserService.existsByMobile(account)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"该手机号已注册");
            }
        } else if (type == RegisterTypeEnum.EMAIL_CODE.getCode()) { // 邮箱注册
            account = request.getEmail();
            keyPrefix = CaptchaConstants.REGISTER_CODE_PREFIX_EMAIL;
            if (usUserService.existsByEmail(account)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"该邮箱已注册");
            }
        } else {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"不支持的注册类型");
        }


        String redisKey = keyPrefix + account;

        // 检查发送频率，防止恶意刷验证码
        if (redisService.hasKey(redisKey)) {
            Long ttl = redisService.getExpire(redisKey); // 获取剩余时间
            if (ttl > (CaptchaConstants.CODE_EXPIRATION_SECONDS - CaptchaConstants.CODE_SEND_INTERVAL_SECONDS)) { // 如果在发送间隔内
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"验证码已发送，请" + (ttl - (CaptchaConstants.CODE_EXPIRATION_SECONDS - CaptchaConstants.CODE_SEND_INTERVAL_SECONDS)) + "秒后重试");
            }
        }

        String code = RandomUtil.randomNumbers(CaptchaConstants.CAPTCHA_LENGTH);
        // 存储到 Redis，设置过期时间
        redisService.setCacheObject(redisKey, code, CaptchaConstants.CODE_EXPIRATION_SECONDS, TimeUnit.SECONDS);
        log.info("为 {} 生成注册验证码：{}，有效期 {} 分钟", account, code, CaptchaConstants.CODE_EXPIRATION_SECONDS / 60);

        // TODO: 实际应调用短信服务（如果为手机）或邮件服务（如果为邮箱）发送验证码。
        // 例如：
        // if (request.getRegisterType() == 1) {
        //     smsService.sendSms(account, "您的注册验证码是：" + code + "，请在5分钟内使用。");
        // } else {
        //     emailService.sendEmail(account, "注册验证码", "您的注册验证码是：" + code + "，请在5分钟内使用。");
        // }
        if( type == RegisterTypeEnum.MOBILE_CODE.getCode()) {
            // smsService.sendSms(account, "您的登录验证码是：" + code + "，请在5分钟内使用。");
        } else if (type == RegisterTypeEnum.EMAIL_CODE.getCode()) {
            emailService.sendVerifiedCode(account, code);
        } else {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"不支持的登录类型");
        }
    }

    /**
     * 发送登录验证码（手机或邮箱）
     * @param account 手机号或邮箱
     * @param type 1: 手机, 2: 邮箱
     */
    public void sendLoginVerificationCode(String account, Integer type) {
        String keyPrefix;
        if (type == LoginTypeEnum.MOBILE_CODE.getCode()) {
            keyPrefix = CaptchaConstants.LOGIN_CODE_PREFIX_MOBILE;
            if (!usUserService.existsByMobile(account)) {
                throw new BusinessException( ErrorCode.NOT_FOUND_ERROR,"该手机号未注册");
            }
        } else if (type == LoginTypeEnum.EMAIL_CODE.getCode()) {
            keyPrefix =CaptchaConstants. LOGIN_CODE_PREFIX_EMAIL;
            if (!usUserService.existsByEmail(account)) {
                throw new BusinessException( ErrorCode.NOT_FOUND_ERROR,"该邮箱未注册");
            }
        } else {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"不支持的登录类型");
        }

        String redisKey = keyPrefix + account;

        if (redisService.hasKey(redisKey)) {
            Long ttl = redisService.getExpire(redisKey);
            if (ttl > (CaptchaConstants.CODE_EXPIRATION_SECONDS - CaptchaConstants.CODE_SEND_INTERVAL_SECONDS)) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"验证码已发送，请" + (ttl - (CaptchaConstants.CODE_EXPIRATION_SECONDS - CaptchaConstants.CODE_SEND_INTERVAL_SECONDS)) + "秒后重试");
            }
        }

        // 生成的随机数字验证码
        String code = RandomUtil.randomNumbers(CaptchaConstants.CAPTCHA_LENGTH);
        redisService.setCacheObject(redisKey, code, CaptchaConstants.CODE_EXPIRATION_SECONDS, TimeUnit.SECONDS);
        log.info("为 {} 生成登录验证码：{}，有效期 {} 分钟", account, code,CaptchaConstants. CODE_EXPIRATION_SECONDS / 60);

        // TODO: 实际发送逻辑
        if( type == LoginTypeEnum.MOBILE_CODE.getCode()) {
            //手机短信发送逻辑待实现
            // smsService.sendSms(account, "您的登录验证码是：" + code + "，请在5分钟内使用。");
        } else if (type == LoginTypeEnum.EMAIL_CODE.getCode()) {
            emailService.sendVerifiedCode(account, code);
        } else {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"不支持的登录类型");
        }


    }

    /**
     * 校验注册验证码并完成用户注册
     * @param request 用户注册请求，包含验证码
     */
    @Transactional // 确保注册操作的原子性
    public void verifyRegisterAndRegisterUser(@Validated({Default.class, SendRegisterCodeRequest.MobileGroup.class, SendRegisterCodeRequest.EmailGroup.class}) RegisterRequest request) {
        String key;
        String account;

        if (request.getRegisterType() == RegisterTypeEnum.MOBILE_CODE.getCode()) {
            account = request.getMobile();
            key = CaptchaConstants.REGISTER_CODE_PREFIX_MOBILE + account;
        } else if (request.getRegisterType() == RegisterTypeEnum.EMAIL_CODE.getCode()) {
            account = request.getEmail();
            key = CaptchaConstants.REGISTER_CODE_PREFIX_EMAIL + account;
        } else {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"不支持的注册类型");
        }

        String storedCode = redisService.getCacheObject(key);

        if (storedCode == null || !storedCode.equals(request.getCode())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"验证码错误或已过期");
        }



        // 验证码校验成功后立即删除，防止重复使用
        redisService.deleteObject(key);
        log.info("账户 {} 注册验证码校验成功，已从Redis删除", account);

        // 用户唯一性校验
        if (request.getRegisterType() == RegisterTypeEnum.MOBILE_CODE.getCode() && usUserService.existsByMobile(request.getMobile())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"该手机号已注册");
        }
        if (request.getRegisterType() == RegisterTypeEnum.EMAIL_CODE.getCode() && usUserService.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"该邮箱已注册");
        }

        // 创建新用户
        usUserService.registerUser(request);
        log.info("用户 {} 注册成功", request.getName());
    }




}