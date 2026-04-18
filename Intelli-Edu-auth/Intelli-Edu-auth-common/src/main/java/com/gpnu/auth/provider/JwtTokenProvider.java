package com.gpnu.auth.provider;


import com.gpnu.auth.common.constants.AuthConstants;
import com.gpnu.auth.common.jwt.JwtProperties;
import com.gpnu.common.service.RedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * JWT 令牌提供者
 * <p>
 * 负责 Access Token / Refresh Token 的生成、验证、吊销等操作。
 * </p>
 *
 * <pre>
 * 改造变更点（相比原 com.gpnu.common.provider.jwt.JwtTokenProvider）：
 *   1. 包路径迁移至 com.gpnu.auth.common.provider
 *   2. import JwtProperties 指向 auth-common 模块
 *   3. 所有硬编码常量替换为 AuthConstants.XXX
 *   4. 业务逻辑零改动，保持原有行为
 * </pre>
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    @Resource
    private RedisService redisService;

    private Key key;

    @PostConstruct
    public void init() {
        String secret = jwtProperties.getSecret();
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // ==================== 生成 Token ====================

    /**
     * 生成 Access Token
     */
    public String generateAccessToken(Long userId, Integer  userType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(AuthConstants.CLAIM_USER_ID, userId);
        claims.put(AuthConstants.CLAIM_USER_TYPE, userType);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getAccessTokenExpirationMs()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 生成 Refresh Token 并存入 Redis
     */
    public String generateRefreshToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(AuthConstants.CLAIM_USER_ID, userId);

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()
                        + jwtProperties.getRefreshTokenExpirationDays() * 24 * 60 * 60 * 1000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        storeRefreshToken(refreshToken, String.valueOf(userId));
        return refreshToken;
    }

    // ==================== Refresh Token 管理 ====================

    /**
     * 将 Refresh Token 存入 Redis
     */
    public void storeRefreshToken(String refreshToken, String userId) {
        redisService.setCacheObject(
                AuthConstants.REFRESH_TOKEN_PREFIX + userId,
                refreshToken,
                jwtProperties.getRefreshTokenExpirationDays() * 24 * 60 * 60,
                TimeUnit.SECONDS
        );
        log.info("Refresh Token for user {} stored in Redis.", userId);
    }

    /**
     * 吊销指定用户的 Refresh Token
     */
    public void invalidateRefreshToken(Long userId) {
        redisService.deleteObject(AuthConstants.REFRESH_TOKEN_PREFIX + userId);
        log.info("Refresh Token for user {} invalidated from Redis.", userId);
    }

    // ==================== Access Token 黑名单 ====================

    /**
     * 将 Access Token 加入黑名单（注销时使用）
     */
    public void addAccessTokenToBlacklist(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            long ttl = expiration.getTime() - System.currentTimeMillis();
            if (ttl > 0) {
                redisService.setCacheObject(
                        AuthConstants.JWT_BLACKLIST_PREFIX + token,
                        "blacklisted",
                        ttl,
                        TimeUnit.MILLISECONDS
                );
                log.info("Access Token blacklisted: {}", token);
            }
        } catch (Exception e) {
            log.warn("Failed to add token to blacklist: {}", e.getMessage());
        }
    }

    /**
     * 判断 Access Token 是否已被加入黑名单
     */
    public boolean isAccessTokenBlacklisted(String token) {
        return redisService.hasKey(AuthConstants.JWT_BLACKLIST_PREFIX + token);
    }

    // ==================== Token 验证 ====================

    /**
     * 验证 Access Token 的有效性（签名 + 过期 + 黑名单）
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
     * 验证 Refresh Token 的有效性（签名 + Redis 中的一致性）
     */
    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
            String userId = claims.getSubject();
            String storedRefreshToken = redisService.getCacheObject(AuthConstants.REFRESH_TOKEN_PREFIX + userId);
            return storedRefreshToken != null && storedRefreshToken.equals(token);
        } catch (ExpiredJwtException e) {
            log.warn("Refresh Token has expired: {}", e.getMessage());
        } catch (Exception e) {
            log.warn("Refresh Token validation failed: {}", e.getMessage());
        }
        return false;
    }

    // ==================== Claims 解析 ====================

    /**
     * 从 Token 中解析所有 Claims
     */
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public String getUserIdFromAccessToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    public Integer getUserTypeFromAccessToken(String token) {
        return (Integer) getAllClaimsFromToken(token).get(AuthConstants.CLAIM_USER_TYPE);
    }

    public String getUserIdFromRefreshToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    public Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }
}