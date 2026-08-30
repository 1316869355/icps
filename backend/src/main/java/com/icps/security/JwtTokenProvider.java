package com.icps.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 令牌签发与校验（HS256）。
 *
 * <p>实现基于 JDK 自带的 {@code javax.crypto.Mac} + {@code java.util.Base64}，
 * 不引入任何第三方依赖，可在 JDK 8/11/17 上一致运行（jjwt 0.9.x 依赖 JAXB，
 * 在 JDK 11+ 上会抛出 NoClassDefFoundError，故未采用）。</p>
 */
@Component
public class JwtTokenProvider {

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final String HEADER_JSON = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";

    private final String secret;
    private final long expirationMillis;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtTokenProvider(@Value("${icps.jwt.secret:icps-default-secret-please-change-in-production}") String secret,
                            @Value("${icps.jwt.expiration:7200000}") long expirationMillis) {
        this.secret = secret;
        this.expirationMillis = expirationMillis;
    }

    /**
     * 签发令牌
     */
    public String generateToken(Long userId, String username, String role) {
        long now = System.currentTimeMillis();
        long exp = now + expirationMillis;

        String header = base64UrlEncode(HEADER_JSON.getBytes(StandardCharsets.UTF_8));

        Map<String, Object> payload = new HashMap<>(8);
        payload.put("sub", String.valueOf(userId));
        payload.put("userId", userId);
        payload.put("username", username);
        payload.put("role", role);
        payload.put("iat", now / 1000);
        payload.put("exp", exp / 1000);

        String body = base64UrlEncode(writeJson(payload));
        String signature = sign(header + "." + body);
        return header + "." + body + "." + signature;
    }

    /**
     * 校验并解析令牌，签名无效或已过期均抛出 {@link JwtValidationException}
     */
    public JwtPrincipal parseToken(String token) {
        JwtPrincipal principal = parseTokenIgnoreExpiration(token);
        if (principal.getExpiresAt() < System.currentTimeMillis()) {
            throw new JwtValidationException("令牌已过期");
        }
        return principal;
    }

    /**
     * 仅校验签名、忽略过期时间（用于 refresh-token 场景）
     */
    @SuppressWarnings("unchecked")
    public JwtPrincipal parseTokenIgnoreExpiration(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new JwtValidationException("令牌为空");
        }
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new JwtValidationException("令牌格式非法");
        }

        String expected = sign(parts[0] + "." + parts[1]);
        if (!MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8),
                parts[2].getBytes(StandardCharsets.UTF_8))) {
            throw new JwtValidationException("令牌签名校验失败");
        }

        Map<String, Object> claims;
        try {
            claims = objectMapper.readValue(base64UrlDecode(parts[1]), Map.class);
        } catch (Exception e) {
            throw new JwtValidationException("令牌载荷解析失败");
        }

        Object userIdValue = claims.get("userId") != null ? claims.get("userId") : claims.get("sub");
        long expiresAt = 0L;
        if (claims.get("exp") != null) {
            expiresAt = ((Number) claims.get("exp")).longValue() * 1000L;
        }

        return new JwtPrincipal(
                toLong(userIdValue),
                (String) claims.get("username"),
                (String) claims.get("role"),
                expiresAt);
    }

    /**
     * 从 {@code Authorization: Bearer xxx} 头中提取令牌
     */
    public String resolveToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7).trim();
        }
        return null;
    }

    public long getExpirationMillis() {
        return expirationMillis;
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256));
            return base64UrlEncode(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("JWT 签名失败", e);
        }
    }

    private byte[] writeJson(Map<String, Object> payload) {
        try {
            return objectMapper.writeValueAsBytes(payload);
        } catch (Exception e) {
            throw new IllegalStateException("JWT 载荷序列化失败", e);
        }
    }

    private static String base64UrlEncode(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }

    private static byte[] base64UrlDecode(String data) {
        return Base64.getUrlDecoder().decode(data);
    }

    private static Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 令牌校验失败异常
     */
    public static class JwtValidationException extends RuntimeException {
        public JwtValidationException(String message) {
            super(message);
        }
    }

    /**
     * 令牌载荷
     */
    public static class JwtPrincipal {
        private final Long userId;
        private final String username;
        private final String role;
        private final long expiresAt;

        public JwtPrincipal(Long userId, String username, String role, long expiresAt) {
            this.userId = userId;
            this.username = username;
            this.role = role;
            this.expiresAt = expiresAt;
        }

        public Long getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        public String getRole() {
            return role;
        }

        public long getExpiresAt() {
            return expiresAt;
        }
    }
}
