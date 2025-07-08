package com.gpnu.user.service;


import com.gpnu.user.model.dto.ususer.LoginRequest;
import com.gpnu.user.model.enums.LoginTypeEnum;
import com.gpnu.user.model.vo.LoginResult;

import javax.security.auth.login.LoginException;

/**
 * 登录策略接口
 */
public interface LoginStrategy {

    /**
     * 获取当前策略支持的登录类型
     * @return 登录类型枚举
     */
    LoginTypeEnum getSupportedLoginType(); // 新增方法


    /**
     * 判断当前策略是否支持该登录请求
     * @param request 登录请求
     * @return true if supported, false otherwise
     */
    boolean supports(LoginRequest request);

    /**
     * 执行登录验证逻辑
     * @param request 登录请求
     * @return 登录结果（包含用户ID、JWT等）
     * @throws LoginException 登录失败时抛出异常
     */
    LoginResult authenticate(LoginRequest request) ;
}