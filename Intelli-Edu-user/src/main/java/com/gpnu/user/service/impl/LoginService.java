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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录服务 (策略模式上下文)
 */
@Service
@Slf4j
public class LoginService {

    private final Map<Integer, LoginStrategy> strategies = new ConcurrentHashMap<>();

    @Resource
    private JwtTokenProvider jwtTokenProvider;

    @Resource
    private UsUserService usUserService; // 用于获取用户信息来验证refresh token

    // Spring会自动注入所有LoginStrategy接口的实现类
    public LoginService(List<LoginStrategy> loginStrategies) {
        for (LoginStrategy strategy : loginStrategies) {
            strategies.put(strategy.getSupportedLoginType().getCode(), strategy);
            log.info("加载登录策略：{}，支持类型：{}", strategy.getClass().getSimpleName(), strategy.getSupportedLoginType().getDescription());
        }
    }

    /**
     * 执行用户登录
     * @param request 登录请求
     * @return 登录结果（包含JWT令牌）
     * @throws BusinessException 登录失败时抛出
     */
    public LoginResult login(LoginRequest request) {
        if (request == null || request.getLoginType() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "登录请求或类型不能为空");
        }

        LoginTypeEnum loginTypeEnum = LoginTypeEnum.getByCode(request.getLoginType());


        // 根据登录类型获取对应的策略
        LoginStrategy strategy = strategies.get(loginTypeEnum.getCode());
        if (strategy == null || !strategy.supports(request)) {
            // 这通常意味着配置错误或传入的请求不符合该类型策略的要求（例如mobile为空）
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "无法处理当前登录请求，请检查参数");
        }

        return strategy.authenticate(request);
    }

    /**
     * 刷新Access Token
     * @param refreshToken 刷新令牌
     * @return 新的登录结果
     * @throws LoginException 刷新失败时抛出
     */
    public LoginResult refreshToken(String refreshToken)  {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "刷新令牌不能为空");
        }

        // 验证Refresh Token的有效性及在Redis中的匹配性
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "无效的刷新令牌");
        }

        String userId = jwtTokenProvider.getUserIdFromRefreshToken(refreshToken);

        // 确保用户仍然存在且未被禁用/删除
        UsUser user = usUserService.getById(userId);
        if (user == null || (user.getIsDelete() != null && user.getIsDelete() == 1)) {
            jwtTokenProvider.invalidateRefreshToken(userId); // 用户状态异常，吊销其刷新令牌
           throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在或已被删除");
        }

        // 生成新的Access Token和新的Refresh Token
        String newAccessToken = jwtTokenProvider.generateAccessToken(userId, user.getType()); // 使用用户当前类型
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId); // 生成新的Refresh Token

        // 吊销旧的Refresh Token (从Redis删除旧的，新生成的已经存入Redis)
        jwtTokenProvider.invalidateRefreshToken(userId); // 删除以userId为key的旧refresh token

        return new LoginResult(userId, user.getType(), newAccessToken, newRefreshToken);
    }

    /**
     * 用户注销 (将Access Token加入黑名单，并移除Refresh Token)
     * @param accessToken Access Token
     */
    public void logout(String accessToken) {
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7); // 去掉"Bearer "前缀
        }

        // 将Access Token加入黑名单，防止其在有效期内继续使用
        jwtTokenProvider.addAccessTokenToBlacklist(accessToken);

        // 移除Refresh Token，防止其被用于刷新
        try {
            String userId = jwtTokenProvider.getUserIdFromAccessToken(accessToken);
            jwtTokenProvider.invalidateRefreshToken(userId);
            log.info("用户 {} 成功注销，Access Token已列入黑名单，Refresh Token已移除。", userId);
        } catch (Exception e) {
            log.warn("注销时处理Refresh Token失败，Access Token已列入黑名单：{}", e.getMessage());
        }
    }
}