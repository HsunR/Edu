package com.gpnu.auth.resource.context;

import com.gpnu.auth.common.constants.AuthConstants;
import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;


/**
 * 用户上下文持有者（基于 ThreadLocal）
 */
public final class UserContextHolder {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> USER_TYPE = new ThreadLocal<>();

    private UserContextHolder() {
    }


    /**
     * 将用户信息存入当前线程上下文
     *
     * @param userId   用户ID
     * @param userType 用户类型
     */
    public static void set(Long userId, String userType) {
        USER_ID.set(userId);
        USER_TYPE.set(userType);
    }

    /**
     * 清除当前线程上下文，防止线程池复用导致数据串漏
     */
    public static void clear() {
        USER_ID.remove();
        USER_TYPE.remove();
    }


    /**
     * 获取当前登录用户的 userId
     *
     * @return 用户ID
     * @throws BusinessException 未登录时抛出 NOT_LOGIN_ERROR
     */
    public static Long getUserId() {
        Long userId = USER_ID.get();
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未获取到用户ID，请确认登录状态");
        }
        return userId;
    }

    /**
     * 获取当前登录用户的 userId，未登录时返回 null 而非抛异常
     * <p>适用于 FeignRelayInterceptor 等允许未登录的场景</p>
     */
    public static Long getUserIdOrNull() {
        return USER_ID.get();
    }

    /**
     * 获取当前登录用户的 userType
     *
     * @return 用户类型字符串
     * @throws BusinessException 未获取到时抛出 SYSTEM_ERROR
     */
    public static String getUserType() {
        String userType = USER_TYPE.get();
        if (userType == null || userType.isEmpty()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "未获取到用户类型信息");
        }
        return userType;
    }


    public static boolean isTeacher() {
        return String.valueOf(AuthConstants.ROLE_TEACHER).equals(getUserType());
    }

    public static boolean isStudent() {
        return String.valueOf(AuthConstants.ROLE_STUDENT).equals(getUserType());
    }

    public static boolean isAdmin() {
        return String.valueOf(AuthConstants.ROLE_ADMIN).equals(getUserType());
    }
}