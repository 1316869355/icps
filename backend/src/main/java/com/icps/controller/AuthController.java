package com.icps.controller;

import com.icps.entity.Student;
import com.icps.entity.Teacher;
import com.icps.repository.StudentRepository;
import com.icps.repository.TeacherRepository;
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
    private StudentRepository studentRepository;
    
    @Autowired
    private TeacherRepository teacherRepository;
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String cardNo = loginRequest.get("cardNo");
        String userName = loginRequest.get("userName");
        String role = loginRequest.get("role");
        
        Map<String, Object> response = new HashMap<>();
        
        if (cardNo == null || userName == null || role == null) {
            response.put("success", false);
            response.put("message", "缺少必要的登录参数");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            if ("2".equals(role)) { // 学生登录
                boolean loginSuccess = studentRepository.login(cardNo, userName);
                if (loginSuccess) {
                    Student student = studentRepository.findById(cardNo);
                    response.put("success", true);
                    response.put("message", "登录成功");
                    response.put("user", student);
                    response.put("role", "student");
                    return ResponseEntity.ok(response);
                } else {
                    response.put("success", false);
                    response.put("message", "身份证号或用户名错误");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                }
            } else if ("1".equals(role)) { // 教师登录
                boolean loginSuccess = teacherRepository.login(cardNo, userName);
                if (loginSuccess) {
                    Teacher teacher = teacherRepository.findByCardNo(cardNo);
                    response.put("success", true);
                    response.put("message", "登录成功");
                    response.put("user", teacher);
                    response.put("role", "teacher");
                    return ResponseEntity.ok(response);
                } else {
                    response.put("success", false);
                    response.put("message", "身份证号或用户名错误");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                }
            } else {
                response.put("success", false);
                response.put("message", "无效的角色类型");
                return ResponseEntity.badRequest().body(response);
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