package com.gpnu.auth.resource.interceptor;

import com.gpnu.auth.common.constants.AuthConstants;
import com.gpnu.auth.resource.context.UserContextHolder;
import com.gpnu.common.constants.Constant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * MVC 拦截器：从网关透传的请求头中提取用户信息，存入 ThreadLocal
 * <p>
 * 在请求进入 Controller 之前执行，请求结束后自动清理，
 * 保证 {@link UserContextHolder} 在整个请求生命周期内可用。
 * </p>
 */
@Slf4j
public class UserInfoRelayInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String requestFrom = request.getHeader(AuthConstants.REQUEST_FROM_HEADER);

        boolean trustedSource = AuthConstants.REQUEST_FROM_GATEWAY.equals(requestFrom)
                        || AuthConstants.REQUEST_FROM_FEIGN.equals(requestFrom);

        if (!trustedSource) {
            return true;
        }

        String userId = request.getHeader(AuthConstants.USER_ID_HEADER);
        String userType = request.getHeader(AuthConstants.USER_TYPE_HEADER);

        if (userId != null && !userId.isEmpty()) {
            UserContextHolder.set(Long.parseLong(userId), userType);
            log.debug("UserContextHolder set: userId={}, userType={}", userId, userType);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        UserContextHolder.clear();
    }
}