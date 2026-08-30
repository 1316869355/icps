package com.icps.security;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JWT 签发与校验的单元测试（纯单测，不依赖 Spring 上下文与数据库）
 */
class JwtTokenProviderTest {

    private static final String SECRET = "unit-test-secret-key-0123456789abcdef";
    private static final long ONE_HOUR = 3600_000L;

    private final JwtTokenProvider provider = new JwtTokenProvider(SECRET, ONE_HOUR);

    @Test
    void generatedTokenCanBeParsed() {
        String token = provider.generateToken(1011L, "2023001001", "student");

        JwtTokenProvider.JwtPrincipal principal = provider.parseToken(token);
        assertEquals(1011L, principal.getUserId());
        assertEquals("2023001001", principal.getUsername());
        assertEquals("student", principal.getRole());
        assertTrue(principal.getExpiresAt() > System.currentTimeMillis());
    }

    @Test
    void tokenFollowsJwtThreeSegmentFormat() {
        String token = provider.generateToken(1L, "u", "student");
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void tamperedPayloadIsRejected() {
        String token = provider.generateToken(1011L, "2023001001", "student");
        String[] parts = token.split("\\.");

        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        String escalated = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payloadJson.replace("student", "admin").getBytes(StandardCharsets.UTF_8));
        String forged = parts[0] + "." + escalated + "." + parts[2];

        assertThrows(JwtTokenProvider.JwtValidationException.class, () -> provider.parseToken(forged));
    }

    @Test
    void tokenSignedWithAnotherSecretIsRejected() {
        JwtTokenProvider other = new JwtTokenProvider("totally-different-secret", ONE_HOUR);
        String token = provider.generateToken(1L, "u", "student");

        assertThrows(JwtTokenProvider.JwtValidationException.class, () -> other.parseToken(token));
    }

    @Test
    void expiredTokenIsRejectedButStillRefreshable() {
        JwtTokenProvider expiredProvider = new JwtTokenProvider(SECRET, -1000L);
        String token = expiredProvider.generateToken(1011L, "2023001001", "student");

        assertThrows(JwtTokenProvider.JwtValidationException.class, () -> expiredProvider.parseToken(token));

        // refresh-token 场景：忽略过期，仅校验签名
        JwtTokenProvider.JwtPrincipal principal = expiredProvider.parseTokenIgnoreExpiration(token);
        assertEquals(1011L, principal.getUserId());
        assertEquals("2023001001", principal.getUsername());
    }

    @Test
    void malformedTokenIsRejected() {
        assertThrows(JwtTokenProvider.JwtValidationException.class, () -> provider.parseToken(null));
        assertThrows(JwtTokenProvider.JwtValidationException.class, () -> provider.parseToken(""));
        assertThrows(JwtTokenProvider.JwtValidationException.class, () -> provider.parseToken("only-one-segment"));
        assertThrows(JwtTokenProvider.JwtValidationException.class, () -> provider.parseToken("a.b"));
        assertThrows(JwtTokenProvider.JwtValidationException.class,
                () -> provider.parseToken(Base64.getUrlEncoder().withoutPadding()
                        .encodeToString("not-json".getBytes(StandardCharsets.UTF_8)) + ".x.y"));
    }

    @Test
    void resolveTokenHandlesAuthorizationHeader() {
        assertNull(provider.resolveToken(null));
        assertNull(provider.resolveToken(""));
        assertNull(provider.resolveToken("Basic dXNlcjpwYXNz"));
        assertEquals("abc.def.ghi", provider.resolveToken("Bearer abc.def.ghi"));
        assertEquals("abc.def.ghi", provider.resolveToken("Bearer  abc.def.ghi  "));
    }

    @Test
    void expirationIsExposedForLoginResponse() {
        assertNotNull(provider.getExpirationMillis());
        assertEquals(3600L, provider.getExpirationMillis() / 1000);
    }
}
