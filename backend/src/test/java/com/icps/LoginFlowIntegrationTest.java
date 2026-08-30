package com.icps;

import com.icps.security.JwtTokenProvider;
import com.icps.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 完整登录链路集成测试（H2 真实数据 + 真实 BCrypt 校验 + 真实 JWT 签发，事务内回滚）
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LoginFlowIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void studentLoginSucceedsAndIssuesRealToken() {
        Map<String, Object> result = authService.login("2023001001", "123456", "student");

        assertTrue((Boolean) result.get("success"));
        assertEquals("student", result.get("role"));

        String token = (String) result.get("token");
        JwtTokenProvider.JwtPrincipal principal = jwtTokenProvider.parseToken(token);
        assertEquals(1011L, principal.getUserId());
        assertEquals("2023001001", principal.getUsername());
        assertEquals("student", principal.getRole());

        @SuppressWarnings("unchecked")
        Map<String, Object> user = (Map<String, Object>) result.get("user");
        assertEquals("2023001001", user.get("studentId"));
        assertEquals("2023001001", user.get("username"));
    }

    @Test
    void teacherLoginLoadsTeacherProfile() {
        Map<String, Object> result = authService.login("teacher1", "123456", "teacher");

        assertTrue((Boolean) result.get("success"));
        assertEquals("teacher", result.get("role"));
        @SuppressWarnings("unchecked")
        Map<String, Object> user = (Map<String, Object>) result.get("user");
        assertEquals(4L, ((Number) user.get("userId")).longValue());
    }

    @Test
    void adminLoginSucceedsWithoutProfile() {
        Map<String, Object> result = authService.login("admin", "123456", "admin");

        assertTrue((Boolean) result.get("success"));
        assertEquals("admin", result.get("role"));
        assertNotNull(result.get("token"));
    }

    @Test
    void loginWithoutRoleUsesDatabaseRole() {
        Map<String, Object> result = authService.login("admin", "123456", null);
        assertTrue((Boolean) result.get("success"));
        assertEquals("admin", result.get("role"));
    }

    @Test
    void wrongPasswordIsRejected() {
        Map<String, Object> result = authService.login("2023001001", "wrong-password", "student");

        assertFalse((Boolean) result.get("success"));
        assertEquals("账号或密码错误", result.get("message"));
        assertNull(result.get("token"));
    }

    @Test
    void unknownUserIsRejected() {
        Map<String, Object> result = authService.login("ghost", "123456", "student");
        assertFalse((Boolean) result.get("success"));
    }

    @Test
    void disabledAccountIsRejected() {
        // disabled01 的 status = 0
        Map<String, Object> result = authService.login("disabled01", "123456", "student");

        assertFalse((Boolean) result.get("success"));
        assertEquals("账号已停用", result.get("message"));
    }

    @Test
    void roleMismatchIsRejected() {
        Map<String, Object> result = authService.login("2023001001", "123456", "teacher");

        assertFalse((Boolean) result.get("success"));
        assertTrue(String.valueOf(result.get("message")).contains("角色不匹配"));
    }

    @Test
    void loginUpdatesLastLoginTimestamp() {
        assertNull(jdbcTemplate.queryForObject(
                "SELECT last_login FROM icps_user WHERE username = '2023001001'", java.sql.Timestamp.class));

        authService.login("2023001001", "123456", "student");

        assertNotNull(jdbcTemplate.queryForObject(
                "SELECT last_login FROM icps_user WHERE username = '2023001001'", java.sql.Timestamp.class));
    }

    @Test
    void loginNeverPollutesRoleColumn() {
        // 回归：早期 updateLastLogin 用 new UserMp() + updateById，
        // 实体默认值 role="student" 会被写回，把管理员降级成学生。
        authService.login("admin", "123456", "admin");

        assertEquals("admin", jdbcTemplate.queryForObject(
                "SELECT role FROM icps_user WHERE username = 'admin'", String.class));

        // 再登一次，确认重复登录也不会逐次污染
        authService.login("admin", "123456", "admin");
        assertEquals("admin", jdbcTemplate.queryForObject(
                "SELECT role FROM icps_user WHERE username = 'admin'", String.class));
    }

    @Test
    void loginDoesNotChangePasswordHash() {
        String before = jdbcTemplate.queryForObject(
                "SELECT password FROM icps_user WHERE username = '2023001001'", String.class);

        authService.login("2023001001", "123456", "student");

        String after = jdbcTemplate.queryForObject(
                "SELECT password FROM icps_user WHERE username = '2023001001'", String.class);
        assertEquals(before, after);
    }
}
