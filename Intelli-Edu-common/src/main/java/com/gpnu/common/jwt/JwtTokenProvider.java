package com.gpnu.common.jwt;

import com.gpnu.common.service.RedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * JWT令牌生成与验证工具
 */
@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret:yourVeryStrongSecretKeyForJWTGenerationAndValidation}") // 密钥，应足够复杂并从配置中心获取
    private String secret;

    @Value("${jwt.access-token-expiration-ms:3600000}") // Access Token过期时间，默认1小时
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration-days:7}") // Refresh Token过期时间，默认7天
    private long refreshTokenExpirationDays;

    private Key key;

    @Resource
    private RedisService redisService;

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:"; // 确保前缀唯一，避免冲突
    private static final String JWT_BLACKLIST_PREFIX = "jwt:blacklist:"; // JWT黑名单前缀

    @PostConstruct
    public void init() {
        // 确保密钥长度符合算法要求，例如HS256至少256位（32字节）
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 生成Access Token
     * @param userId 用户ID
     * @param userType 用户类型 (String 类型，与数据库UsUser.type匹配)
     * @return Access Token
     */
    public String generateAccessToken(String userId, String userType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("userType", userType);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 生成Refresh Token
     * @param userId 用户ID
     * @return Refresh Token
     */
    public String generateRefreshToken(String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        // refresh token通常不携带userType等业务信息，除非刷新时不需要查库
        // 建议refresh token只携带user id和签发时间

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationDays * 24 * 60 * 60 * 1000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // 将Refresh Token存储到Redis，便于管理和吊销
        storeRefreshToken(refreshToken, userId);
        return refreshToken;
    }

    /**
     * 存储Refresh Token到Redis
     * @param refreshToken 刷新令牌
     * @param userId 用户ID
     */
    public void storeRefreshToken(String refreshToken, String userId) {
        // Redis中以 userId 为key，refreshToken为value存储，便于通过userId找到refresh token进行更新或删除
        // 或者以 refreshToken 为key，userId为value，用于验证时快速查找
        // 这里采用 userId 作为 key，每次用户登录或刷新时更新
        redisService.setCacheObject(
                REFRESH_TOKEN_PREFIX + userId,
                refreshToken,
                refreshTokenExpirationDays * 24 * 60 * 60,
                TimeUnit.SECONDS // RedisService expire是秒
        );
        log.info("Refresh Token for user {} stored in Redis.", userId);
    }

    /**
     * 使Refresh Token失效 (从Redis删除)
     * @param userId 用户ID
     */
    public void invalidateRefreshToken(String userId) {
        redisService.deleteObject(REFRESH_TOKEN_PREFIX + userId);
        log.info("Refresh Token for user {} invalidated from Redis.", userId);
    }

    /**
     * 将Access Token加入黑名单
     * @param token Access Token
     */
    public void addAccessTokenToBlacklist(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            long ttl = expiration.getTime() - System.currentTimeMillis(); // 计算剩余过期时间
            if (ttl > 0) {
                redisService.setCacheObject(JWT_BLACKLIST_PREFIX + token, "blacklisted", ttl, TimeUnit.MILLISECONDS);
                log.info("Access Token blacklisted: {}", token);
            }
        } catch (Exception e) {
            log.warn("Failed to add token to blacklist: {}", e.getMessage());
        }
    }

    /**
     * 检查Access Token是否在黑名单中
     * @param token Access Token
     * @return true如果在黑名单中，false否则
     */
    public boolean isAccessTokenBlacklisted(String token) {
        return redisService.hasKey(JWT_BLACKLIST_PREFIX + token);
    }

    /**
     * 验证Access Token的签名和有效期，并检查黑名单
     * @param token Access Token
     * @return true if valid, false otherwise
     */
    public boolean validateAccessToken(String token) {
        try {
            if (isAccessTokenBlacklisted(token)) {
                log.warn("Access Token is blacklisted: {}", token);
                return false;
            }
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Access Token has expired: {}", e.getMessage());
        } catch (Exception e) {
            log.warn("Access Token validation failed: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 验证Refresh Token的签名和有效期，并检查Redis中是否存在且匹配
     * @param token Refresh Token
     * @return true if valid and exists in Redis, false otherwise
     */
    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
            String userId = claims.getSubject();
            // 检查Redis中是否存在该用户的Refresh Token，并且与传入的token一致
            String storedRefreshToken = redisService.getCacheObject(REFRESH_TOKEN_PREFIX + userId);
            return storedRefreshToken != null && storedRefreshToken.equals(token);
        } catch (ExpiredJwtException e) {
            log.warn("Refresh Token has expired: {}", e.getMessage());
        } catch (Exception e) {
            log.warn("Refresh Token validation failed: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 从Token中获取声明Claims
     * @param token JWT令牌
     * @return Claims对象
     */
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    /**
     * 从Access Token中获取用户ID
     * @param token Access Token
     * @return 用户ID
     */
    public String getUserIdFromAccessToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    /**
     * 从Access Token中获取用户类型
     * @param token Access Token
     * @return 用户类型 (String)
     */
    public String getUserTypeFromAccessToken(String token) {
        return (String) getAllClaimsFromToken(token).get("userType");
    }

    /**
     * 从Refresh Token中获取用户ID
     * @param token Refresh Token
     * @return 用户ID
     */
    public String getUserIdFromRefreshToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }
    
    /**
     * 从Token中获取过期时间
     * @param token JWT令牌
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }
}