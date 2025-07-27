package com.gpnu.common.utils.contextHolder;

import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.model.enums.user.UserType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户上下文持有者工具类
 * 用于从请求头中获取当前登录用户的ID和类型
 * 该工具类依赖于API网关在请求头中传递的用户信息
 */
public class UserContextHolder {

    // 与网关中定义的请求头名称保持一致
    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_TYPE = "X-User-Type";

    /**
     * 获取当前请求的 HttpServletRequest 对象
     * @return HttpServletRequest
     * @throws BusinessException 如果无法获取到请求上下文或请求对象
     */
    private static HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "无法获取请求上下文，请确保在Web请求环境中调用此方法");
        }
        return attributes.getRequest();
    }

    /**
     * 获取当前登录用户的 user_id
     * @return 用户的 user_id
     * @throws BusinessException 如果请求头中缺少 user_id 或值为空
     */
    public static Long getUserId() {
        HttpServletRequest request = getHttpServletRequest();
        String userId = request.getHeader(HEADER_USER_ID);
        if (userId == null || userId.isEmpty()) {
            // 这通常表示请求未经过网关认证，或者网关未正确传递用户ID
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未获取到用户ID，请确认登录状态");
        }
        return Long.parseLong(userId);
    }

    /**
     * 获取当前登录用户的 user_type
     * @return 用户的 user_type
     * @throws BusinessException 如果请求头中缺少 user_type 或值为空
     */
    public static String getUserType() {
        HttpServletRequest request = getHttpServletRequest();
        String userType = request.getHeader(HEADER_USER_TYPE);
        if (userType == null || userType.isEmpty()) {
            // 这通常表示请求未经过网关认证，或者网关未正确传递用户类型
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "未获取到用户类型信息");
        }
        return userType;
    }

    /**
     * 判断当前用户是否是教师
     * @return true 如果是教师，false 否则
     */
    public static boolean isTeacher() {
        return UserType.TEACHER.getType().equals(getUserType());
    }

    /**
     * 判断当前用户是否是学生
     * @return true 如果是学生，false 否则
     */
    public static boolean isStudent() {
        return UserType.STUDENT.getType().equals(getUserType());
    }

    /**
     * 判断当前用户是否是管理员
     * @return true 如果是管理员，false 否则
     */
    public static boolean isAdmin() {
        return UserType.ADMIN.getType().equals(getUserType());
    }
}