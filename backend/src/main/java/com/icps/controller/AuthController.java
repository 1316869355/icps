package com.icps.controller;

import com.icps.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String userName = loginRequest.get("username");
        String cardNo = loginRequest.get("password");
        String roleType = loginRequest.get("role");
        
        Map<String, Object> response = new HashMap<>();
        
        if (userName == null || cardNo == null || roleType == null) {
            response.put("success", false);
            response.put("message", "缺少必要的登录参数");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            // 直接使用前端传递的角色类型
            String role = roleType;
            if (!"teacher".equals(role) && !"student".equals(role)) {
                response.put("success", false);
                response.put("message", "无效的角色类型");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 调用AuthService进行登录验证
            Map<String, Object> loginResult = authService.login(userName, cardNo, role);
            
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
    public ResponseEntity<Map<String, Object>> logout() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "退出登录成功");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "ICPS后端服务运行正常");
        return ResponseEntity.ok(response);
    }
}