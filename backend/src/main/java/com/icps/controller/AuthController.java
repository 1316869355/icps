package com.icps.controller;

import com.icps.aspect.Log;
import com.icps.security.JwtTokenProvider;
import com.icps.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @PostMapping("/login")
    @Log(value = "用户登录接口", printParams = true, printResult = true, printExecutionTime = true)
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        String roleType = loginRequest.get("role");
        
        Map<String, Object> response = new HashMap<>();
        
        if (username == null || password == null) {
            response.put("success", false);
            response.put("message", "缺少必要的登录参数");
            return ResponseEntity.badRequest().body(response);
        }

        // 角色为可选项：不传时以数据库中的实际角色为准
        if (roleType != null && !isSupportedRole(roleType)) {
            response.put("success", false);
            response.put("message", "无效的角色类型");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            Map<String, Object> loginResult = authService.login(username, password, roleType);
            
            if (Boolean.TRUE.equals(loginResult.get("success"))) {
                return ResponseEntity.ok(loginResult);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResult);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "登录过程中发生错误");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/logout")
    @Log(value = "用户退出登录接口", printParams = false, printResult = true, printExecutionTime = true)
    public ResponseEntity<Map<String, Object>> logout() {
        SecurityContextHolder.clearContext();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "退出登录成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 获取当前登录用户信息（基于 JWT 解析结果）
     */
    @GetMapping("/user-info")
    @Log(value = "获取当前用户信息", printParams = false, printResult = true, printExecutionTime = true)
    public ResponseEntity<Map<String, Object>> userInfo() {
        Map<String, Object> response = new HashMap<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtTokenProvider.JwtPrincipal)) {
            response.put("success", false);
            response.put("message", "未认证或令牌无效");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        JwtTokenProvider.JwtPrincipal principal = (JwtTokenProvider.JwtPrincipal) authentication.getPrincipal();
        Map<String, Object> detail = authService.getUserInfo(String.valueOf(principal.getUserId()), principal.getRole());

        if (Boolean.TRUE.equals(detail.get("success"))) {
            response.put("success", true);
            @SuppressWarnings("unchecked")
            Map<String, Object> user = (Map<String, Object>) detail.get("user");
            user.put("username", principal.getUsername());
            user.put("role", principal.getRole());
            response.put("user", user);
            response.put("role", principal.getRole());
            return ResponseEntity.ok(response);
        }

        response.put("success", false);
        response.put("message", detail.get("message"));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * 刷新令牌：签名有效即可换取新令牌（允许旧令牌已过期）
     */
    @PostMapping("/refresh-token")
    @Log(value = "刷新令牌", printParams = false, printResult = true, printExecutionTime = true)
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        Map<String, Object> response = new HashMap<>();

        String token = jwtTokenProvider.resolveToken(authorizationHeader);
        if (token == null) {
            response.put("success", false);
            response.put("message", "缺少 Bearer 令牌");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            JwtTokenProvider.JwtPrincipal principal = jwtTokenProvider.parseTokenIgnoreExpiration(token);
            String newToken = jwtTokenProvider.generateToken(principal.getUserId(), principal.getUsername(), principal.getRole());

            response.put("success", true);
            response.put("token", newToken);
            response.put("expiresIn", jwtTokenProvider.getExpirationMillis() / 1000);
            return ResponseEntity.ok(response);
        } catch (JwtTokenProvider.JwtValidationException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    @GetMapping("/health")
    @Log(value = "健康检查接口", printParams = false, printResult = true, printExecutionTime = true)
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "ICPS后端服务运行正常");
        return ResponseEntity.ok(response);
    }

    private boolean isSupportedRole(String role) {
        return "student".equals(role) || "teacher".equals(role) || "admin".equals(role);
    }
}
