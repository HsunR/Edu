package com.gpnu.auth.common.constants;

/**
 * 鉴权模块统一常量定义
 * <p>
 * 所有与身份认证、令牌、请求头传递相关的常量均在此处统一管理，
 * 避免在 Gateway、User、Resource 等模块中重复定义。
 * </p>
 *
 * <pre>
 * 改造前常量散布情况：
 *   "X-User-Id"    → AuthGlobalFilter(gateway) + UserContextHolder(common) 各定义一次
 *   "X-User-Type"  → 同上
 *   "Bearer "      → AuthGlobalFilter + AuthController + LoginService 各硬编码一次
 *   "refresh_token:"  → JwtTokenProvider 内部 private
 *   "jwt:blacklist:"  → 同上
 *   "userId"/"userType" → JwtTokenProvider 中硬编码字符串
 * </pre>
 */
public final class AuthConstants {

    private AuthConstants() {
    }

    // ==================== HTTP Header ====================

    /**
     * 标准 Authorization 请求头名称
     */
    public static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * Bearer Token 前缀（含尾部空格）
     */
    public static final String BEARER_PREFIX = "Bearer ";

    /**
     * Bearer Token 前缀长度，用于 substring 截取 token
     */
    public static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

    /**
     * 网关向下游服务透传的用户ID请求头
     * <p>使用方：Gateway 写入 → UserContextHolder / FeignRelayInterceptor 读取</p>
     */
    public static final String USER_ID_HEADER = "X-User-Id";

    /**
     * 网关向下游服务透传的用户类型请求头
     * <p>使用方：Gateway 写入 → UserContextHolder 读取</p>
     */
    public static final String USER_TYPE_HEADER = "X-User-Type";


    /**
     * Refresh Token 在 Redis 中的 key 前缀
     * <p>完整 key 格式：{@code auth:refresh_token:{userId}}</p>
     */
    public static final String REFRESH_TOKEN_PREFIX = "auth:refresh_token:";

    /**
     * Access Token 黑名单在 Redis 中的 key 前缀
     * <p>完整 key 格式：{@code auth:jwt:blacklist:{token}}</p>
     */
    public static final String JWT_BLACKLIST_PREFIX = "auth:jwt:blacklist:";

    // ==================== JWT Claims 字段名 ====================

    /**
     * JWT Claims 中存储的用户ID字段名
     */
    public static final String CLAIM_USER_ID = "userId";

    /**
     * JWT Claims 中存储的用户类型字段名
     */
    public static final String CLAIM_USER_TYPE = "userType";


    /**
     * 学生身份代码
     */
    public static final int ROLE_STUDENT = 1;

    /**
     * 教师身份代码
     */
    public static final int ROLE_TEACHER = 2;

    /**
     * 管理员身份代码
     */
    public static final int ROLE_ADMIN   = 3;
}
