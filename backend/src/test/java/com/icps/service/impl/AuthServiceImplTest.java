package com.icps.service.impl;

import com.icps.entity.StudentMp;
import com.icps.entity.TeacherMp;
import com.icps.entity.UserMp;
import com.icps.mapper.StudentMpMapper;
import com.icps.mapper.TeacherMpMapper;
import com.icps.security.JwtTokenProvider;
import com.icps.service.StudentMpService;
import com.icps.service.TeacherMpService;
import com.icps.service.UserMpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 登录链路单元测试（纯 Mockito，不启 Spring 上下文、不连数据库）
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private StudentMpMapper studentMapper;

    @Mock
    private TeacherMpMapper teacherMapper;

    @Mock
    private UserMpService userMpService;

    @Mock
    private StudentMpService studentMpService;

    @Mock
    private TeacherMpService teacherMpService;

    private JwtTokenProvider jwtTokenProvider;
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider("auth-unit-test-secret-0123456789", 3600_000L);
        authService = new AuthServiceImpl(jwtTokenProvider);
        ReflectionTestUtils.setField(authService, "studentMapper", studentMapper);
        ReflectionTestUtils.setField(authService, "teacherMapper", teacherMapper);
        ReflectionTestUtils.setField(authService, "userMpService", userMpService);
        ReflectionTestUtils.setField(authService, "studentMpService", studentMpService);
        ReflectionTestUtils.setField(authService, "teacherMpService", teacherMpService);
    }

    @Test
    void studentLoginSucceedsAndIssuesToken() {
        UserMp user = user(1011L, "2023001001", "student", 1);
        when(userMpService.getByUsername("2023001001")).thenReturn(user);
        when(userMpService.matchesPassword("123456", user.getPassword())).thenReturn(true);

        StudentMp student = new StudentMp();
        student.setUserId(1011L);
        student.setSno("2023001001");
        when(studentMapper.selectByUserId(1011L)).thenReturn(student);

        Map<String, Object> studentMap = new HashMap<>();
        studentMap.put("userId", 1011L);
        studentMap.put("studentId", "2023001001");
        when(studentMpService.convertStudentToMap(student)).thenReturn(studentMap);

        Map<String, Object> result = authService.login("2023001001", "123456", "student");

        assertTrue((Boolean) result.get("success"));
        assertEquals("student", result.get("role"));

        String token = (String) result.get("token");
        assertNotNull(token);
        JwtTokenProvider.JwtPrincipal principal = jwtTokenProvider.parseToken(token);
        assertEquals(1011L, principal.getUserId());
        assertEquals("student", principal.getRole());

        assertEquals("2023001001", ((Map<?, ?>) result.get("user")).get("username"));
        verify(userMpService).updateLastLogin(1011L);
    }

    @Test
    void teacherLoginLoadsTeacherProfile() {
        UserMp user = user(4L, "teacher1", "teacher", 1);
        when(userMpService.getByUsername("teacher1")).thenReturn(user);
        when(userMpService.matchesPassword("123456", user.getPassword())).thenReturn(true);

        TeacherMp teacher = new TeacherMp();
        teacher.setUserId(4L);
        when(teacherMapper.selectByUserId(4L)).thenReturn(teacher);

        Map<String, Object> teacherMap = new HashMap<>();
        teacherMap.put("userId", 4L);
        when(teacherMpService.convertTeacherToMap(teacher)).thenReturn(teacherMap);

        Map<String, Object> result = authService.login("teacher1", "123456", "teacher");

        assertTrue((Boolean) result.get("success"));
        assertEquals("teacher", result.get("role"));
        verify(studentMapper, never()).selectByUserId(anyLong());
    }

    @Test
    void adminLoginSkipsProfileLookup() {
        UserMp user = user(1L, "admin", "admin", 1);
        when(userMpService.getByUsername("admin")).thenReturn(user);
        when(userMpService.matchesPassword("123456", user.getPassword())).thenReturn(true);
        when(userMpService.convertUserToMap(user)).thenReturn(new HashMap<>());

        Map<String, Object> result = authService.login("admin", "123456", "admin");

        assertTrue((Boolean) result.get("success"));
        assertEquals("admin", result.get("role"));
        assertNotNull(result.get("token"));
    }

    @Test
    void wrongPasswordIsRejected() {
        UserMp user = user(1011L, "2023001001", "student", 1);
        when(userMpService.getByUsername("2023001001")).thenReturn(user);
        when(userMpService.matchesPassword("bad", user.getPassword())).thenReturn(false);

        Map<String, Object> result = authService.login("2023001001", "bad", "student");

        assertFalse((Boolean) result.get("success"));
        assertEquals("账号或密码错误", result.get("message"));
        assertNull(result.get("token"));
        verify(userMpService, never()).updateLastLogin(anyLong());
    }

    @Test
    void unknownUserIsRejected() {
        when(userMpService.getByUsername("nobody")).thenReturn(null);

        Map<String, Object> result = authService.login("nobody", "123456", "student");

        assertFalse((Boolean) result.get("success"));
        assertEquals("账号或密码错误", result.get("message"));
    }

    @Test
    void disabledAccountIsRejected() {
        UserMp user = user(1013L, "disabled01", "student", 0);
        when(userMpService.getByUsername("disabled01")).thenReturn(user);
        when(userMpService.matchesPassword("123456", user.getPassword())).thenReturn(true);

        Map<String, Object> result = authService.login("disabled01", "123456", "student");

        assertFalse((Boolean) result.get("success"));
        assertEquals("账号已停用", result.get("message"));
    }

    @Test
    void roleMismatchIsRejected() {
        UserMp user = user(1011L, "2023001001", "student", 1);
        when(userMpService.getByUsername("2023001001")).thenReturn(user);
        when(userMpService.matchesPassword("123456", user.getPassword())).thenReturn(true);

        Map<String, Object> result = authService.login("2023001001", "123456", "teacher");

        assertFalse((Boolean) result.get("success"));
        assertTrue(String.valueOf(result.get("message")).contains("角色不匹配"));
        assertNull(result.get("token"));
    }

    @Test
    void missingStudentProfileIsRejected() {
        UserMp user = user(1011L, "2023001001", "student", 1);
        when(userMpService.getByUsername("2023001001")).thenReturn(user);
        when(userMpService.matchesPassword("123456", user.getPassword())).thenReturn(true);
        when(studentMapper.selectByUserId(1011L)).thenReturn(null);

        Map<String, Object> result = authService.login("2023001001", "123456", "student");

        assertFalse((Boolean) result.get("success"));
        assertEquals("学生信息不存在", result.get("message"));
        assertNull(result.get("token"));
    }

    @Test
    void roleIsTakenFromDatabaseNotFromRequest() {
        UserMp user = user(1L, "admin", "admin", 1);
        when(userMpService.getByUsername("admin")).thenReturn(user);
        when(userMpService.matchesPassword("123456", user.getPassword())).thenReturn(true);
        when(userMpService.convertUserToMap(user)).thenReturn(new HashMap<>());

        // 不传 role 时以库内角色为准
        Map<String, Object> result = authService.login("admin", "123456", null);

        assertTrue((Boolean) result.get("success"));
        assertEquals("admin", result.get("role"));

        verify(userMpService).convertUserToMap(eq(user));
        assertEquals("admin", jwtTokenProvider.parseToken((String) result.get("token")).getRole());
    }

    private UserMp user(Long userId, String username, String role, Integer status) {
        UserMp user = new UserMp();
        user.setUserId(userId);
        user.setUsername(username);
        user.setPassword("$2a$10$hashedpasswordplaceholder");
        user.setRole(role);
        user.setStatus(status);
        return user;
    }
}
