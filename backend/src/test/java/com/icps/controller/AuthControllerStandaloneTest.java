package com.icps.controller;

import com.icps.security.JwtTokenProvider;
import com.icps.service.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AuthController 控制层测试：使用 standalone MockMvc，不启动 Spring 上下文与数据库
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerStandaloneTest {

    @Mock
    private AuthService authService;

    private JwtTokenProvider jwtTokenProvider;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider("controller-standalone-secret", 3600_000L);
        AuthController controller = new AuthController();
        ReflectionTestUtils.setField(controller, "authService", authService);
        ReflectionTestUtils.setField(controller, "jwtTokenProvider", jwtTokenProvider);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void healthEndpointIsAvailable() throws Exception {
        mockMvc.perform(get("/auth/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void loginReturns400WhenCredentialsMissing() throws Exception {
        // 缺省字段（反序列化为 null）应走 400 分支；空字符串会按「账号或密码错误」走 401
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void loginReturns401ForEmptyCredentials() throws Exception {
        Map<String, Object> failure = new HashMap<>();
        failure.put("success", false);
        failure.put("message", "账号或密码错误");
        when(authService.login(eq(""), eq(""), any())).thenReturn(failure);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\",\"password\":\"\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void loginReturns400ForUnsupportedRole() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"u\",\"password\":\"p\",\"role\":\"hacker\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void loginReturns200WithTokenOnSuccess() throws Exception {
        Map<String, Object> loginResult = new HashMap<>();
        loginResult.put("success", true);
        loginResult.put("token", "issued-token");
        loginResult.put("role", "student");
        when(authService.login("2023001001", "123456", "student")).thenReturn(loginResult);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"2023001001\",\"password\":\"123456\",\"role\":\"student\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").value("issued-token"));
    }

    @Test
    void loginReturns401WhenServiceRejects() throws Exception {
        Map<String, Object> loginResult = new HashMap<>();
        loginResult.put("success", false);
        loginResult.put("message", "账号或密码错误");
        when(authService.login(anyString(), anyString(), anyString())).thenReturn(loginResult);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"2023001001\",\"password\":\"bad\",\"role\":\"student\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void userInfoReturns401WithoutAuthentication() throws Exception {
        mockMvc.perform(get("/auth/user-info"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void userInfoReturnsCurrentPrincipal() throws Exception {
        String token = jwtTokenProvider.generateToken(1011L, "2023001001", "student");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                jwtTokenProvider.parseToken(token), null, Collections.emptyList()));

        Map<String, Object> detail = new HashMap<>();
        detail.put("success", true);
        Map<String, Object> user = new HashMap<>();
        user.put("userId", 1011L);
        user.put("name", "张三");
        detail.put("user", user);
        when(authService.getUserInfo(eq("1011"), eq("student"))).thenReturn(detail);

        mockMvc.perform(get("/auth/user-info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.role").value("student"))
                .andExpect(jsonPath("$.user.username").value("2023001001"));
    }

    @Test
    void refreshTokenRequiresBearerHeader() throws Exception {
        mockMvc.perform(post("/auth/refresh-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void refreshTokenIssuesNewTokenWhenSignatureValid() throws Exception {
        JwtTokenProvider expiredProvider = new JwtTokenProvider("controller-standalone-secret", -1000L);
        String expiredToken = expiredProvider.generateToken(1L, "admin", "admin");

        mockMvc.perform(post("/auth/refresh-token").header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void refreshTokenRejectsForgedToken() throws Exception {
        mockMvc.perform(post("/auth/refresh-token").header("Authorization", "Bearer aaa.bbb.ccc"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void logoutAlwaysSucceeds() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
