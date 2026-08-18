package com.gpnu.auth.common.constants;


/**
 * 鉴权模块统一常量定义。
 *
 * <p>
 * 所有与身份认证、令牌、请求头透传、内部服务调用签名相关的常量均在此处统一管理，
 * 避免在 Gateway、User、Course、Resource、AI 等模块中重复定义。
 * </p>
 *
 * <pre>
 * 典型使用链路：
 *   1. 前端请求 Gateway，携带 Authorization: Bearer xxx
 *   2. Gateway 校验 JWT
 *   3. Gateway 清除外部伪造的身份 Header
 *   4. Gateway 写入 X-User-Id / X-User-Type / X-Request-From
 *   5. Gateway 生成 X-Internal-Timestamp / X-Internal-Sign
 *   6. 下游服务校验内部签名后，将用户信息写入 UserContextHolder
 * </pre>
 */
public final class AuthConstants {

    private AuthConstants() {
    }

    // ==================== Standard HTTP Header ====================

    /**
     * 标准 Authorization 请求头名称。
     */
    public static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * Bearer Token 前缀，含尾部空格。
     */
    public static final String BEARER_PREFIX = "Bearer ";

    /**
     * Bearer Token 前缀长度，用于 substring 截取 token。
     */
    public static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

    // ==================== User Identity Relay Header ====================

    /**
     * 网关或内部 Feign 调用向下游服务透传的用户 ID 请求头。
     *
     * <p>
     * 注意：该 Header 只能由 Gateway 或可信内部调用方写入，外部请求携带的同名 Header
     * 必须在 Gateway 层被清除。
     * </p>
     */
    public static final String USER_ID_HEADER = "X-User-Id";

    /**
     * 网关或内部 Feign 调用向下游服务透传的用户类型请求头。
     *
     * <p>
     * 取值通常对应 {@link #ROLE_STUDENT}、{@link #ROLE_TEACHER}、{@link #ROLE_ADMIN}。
     * </p>
     */
    public static final String USER_TYPE_HEADER = "X-User-Type";


    /**
     * 请求来源标识 Header。
     *
     * <p>
     * 用于标识当前请求是由 Gateway 转发，还是由内部 Feign 调用发起。
     * 该字段不能单独作为安全依据，必须配合内部签名校验。
     * </p>
     */
    public static final String REQUEST_FROM_HEADER = "X-Request-From";

    // ==================== Request Source Values ====================

    /**
     * 请求来源：Gateway。
     */
    public static final String REQUEST_FROM_GATEWAY = "gateway";

    /**
     * 请求来源：内部 Feign 调用。
     */
    public static final String REQUEST_FROM_FEIGN = "feign";

    // ==================== Internal Request Signature Header ====================

    /**
     * 内部请求签名时间戳 Header。
     *
     * <p>
     * 建议取值为 {@code System.currentTimeMillis()}，下游服务根据该时间戳判断请求是否过期。
     * </p>
     */
    public static final String INTERNAL_TIMESTAMP_HEADER = "X-Internal-Timestamp";

    /**
     * 内部请求签名 Header。
     *
     * <p>
     * Gateway 或 Feign 调用方基于内部共享密钥生成 HMAC 签名，
     * 下游服务校验通过后才信任 X-User-Id / X-User-Type。
     * </p>
     */
    public static final String INTERNAL_SIGN_HEADER = "X-Internal-Sign";


    // ==================== Redis Key Prefix ====================

    /**
     * Refresh Token 在 Redis 中的 key 前缀。
     *
     * <p>完整 key 格式：{@code auth:refresh_token:{userId}}</p>
     */
    public static final String REFRESH_TOKEN_PREFIX = "auth:refresh_token:";

    /**
     * Access Token 黑名单在 Redis 中的 key 前缀。
     *
     * <p>完整 key 格式：{@code auth:jwt:blacklist:{token}}</p>
     */
    public static final String JWT_BLACKLIST_PREFIX = "auth:jwt:blacklist:";

    // ==================== JWT Claims Field Name ====================

    /**
     * JWT Claims 中存储的用户 ID 字段名。
     */
    public static final String CLAIM_USER_ID = "userId";

    /**
     * JWT Claims 中存储的用户类型字段名。
     */
    public static final String CLAIM_USER_TYPE = "userType";

    // ==================== Role Code ====================

    /**
     * 学生身份代码。
     */
    public static final int ROLE_STUDENT = 1;

    /**
     * 教师身份代码。
     */
    public static final int ROLE_TEACHER = 2;

    /**
     * 管理员身份代码。
     */
    public static final int ROLE_ADMIN = 3;
}