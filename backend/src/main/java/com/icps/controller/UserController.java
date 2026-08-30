package com.icps.controller;

import com.icps.entity.UserMp;
import com.icps.service.UserMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户与账号接口
 */
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserMpService userMpService;

    /**
     * 用户列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUsers(@RequestParam(required = false) String role,
                                                        @RequestParam(required = false) String username) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (username != null && !username.trim().isEmpty()) {
                Map<String, Object> result = userMpService.getUserByUsername(username.trim());
                response.put("success", result.get("success"));
                response.put("data", result.get("user"));
                response.put("message", result.get("message"));
                return ResponseEntity.ok(response);
            }

            if (role != null && !role.trim().isEmpty()) {
                response.put("success", true);
                response.put("data", userMpService.getUsersByRole(role.trim()));
                return ResponseEntity.ok(response);
            }

            response.put("success", true);
            response.put("data", userMpService.getAllUsers());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取用户列表失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 用户统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> statistics() {
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("data", userMpService.getUserStatistics());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取用户统计失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 新增用户（密码以 BCrypt 加密存储）
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addUser(@RequestBody UserMp user) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> result = userMpService.addUser(user);
            return ResponseEntity.status(Boolean.TRUE.equals(result.get("success"))
                    ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(result);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "新增用户失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id,
                                                          @RequestBody Map<String, Object> userData) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> result = userMpService.updateUser(id, userData);
            return ResponseEntity.status(Boolean.TRUE.equals(result.get("success"))
                    ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(result);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新用户失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 删除用户（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> result = userMpService.deleteUser(id);
            return ResponseEntity.status(Boolean.TRUE.equals(result.get("success"))
                    ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(result);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除用户失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
