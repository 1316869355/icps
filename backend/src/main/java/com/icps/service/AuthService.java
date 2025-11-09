package com.icps.service;

import com.icps.entity.jpa.StudentJpa;
import com.icps.entity.jpa.TeacherJpa;
import com.icps.repository.jpa.StudentJpaRepository;
import com.icps.repository.jpa.TeacherJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 认证服务
 */
@Service
public class AuthService {
    
    @Autowired
    private StudentJpaRepository studentRepository;
    
    @Autowired
    private TeacherJpaRepository teacherRepository;
    
    /**
     * 用户登录
     */
    public Map<String, Object> login(String username, String password, String role) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            if ("student".equals(role)) {
                // 学生登录验证
                boolean loginSuccess = studentRepository.existsByCardNoAndName(password, username);
                if (loginSuccess) {
                    Optional<StudentJpa> studentOpt = studentRepository.findById(password);
                    if (studentOpt.isPresent()) {
                        StudentJpa student = studentOpt.get();
                        result.put("success", true);
                        result.put("message", "登录成功");
                        result.put("role", "student");
                        result.put("user", convertStudentToMap(student));
                        result.put("token", generateToken(student.getStuCardNo(), "student"));
                    } else {
                        result.put("success", false);
                        result.put("message", "学生信息不存在");
                    }
                } else {
                    result.put("success", false);
                    result.put("message", "身份证号或姓名错误");
                }
            } else if ("teacher".equals(role)) {
                // 教师登录验证
                boolean loginSuccess = teacherRepository.existsByCardNoAndName(password, username);
                if (loginSuccess) {
                    Optional<TeacherJpa> teacherOpt = teacherRepository.findByTeacherCardNo(password);
                    if (teacherOpt.isPresent()) {
                        TeacherJpa teacher = teacherOpt.get();
                        result.put("success", true);
                        result.put("message", "登录成功");
                        result.put("role", "teacher");
                        result.put("user", convertTeacherToMap(teacher));
                        result.put("token", generateToken(teacher.getTeacherCardNo(), "teacher"));
                    } else {
                        result.put("success", false);
                        result.put("message", "教师信息不存在");
                    }
                } else {
                    result.put("success", false);
                    result.put("message", "身份证号或姓名错误");
                }
            } else {
                result.put("success", false);
                result.put("message", "无效的角色类型");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "登录过程中发生错误: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 获取用户信息
     */
    public Map<String, Object> getUserInfo(String userId, String role) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            if ("student".equals(role)) {
                Optional<StudentJpa> studentOpt = studentRepository.findById(userId);
                if (studentOpt.isPresent()) {
                    result.put("success", true);
                    result.put("user", convertStudentToMap(studentOpt.get()));
                } else {
                    result.put("success", false);
                    result.put("message", "学生信息不存在");
                }
            } else if ("teacher".equals(role)) {
                Optional<TeacherJpa> teacherOpt = teacherRepository.findByTeacherCardNo(userId);
                if (teacherOpt.isPresent()) {
                    result.put("success", true);
                    result.put("user", convertTeacherToMap(teacherOpt.get()));
                } else {
                    result.put("success", false);
                    result.put("message", "教师信息不存在");
                }
            } else {
                result.put("success", false);
                result.put("message", "无效的角色类型");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取用户信息失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 生成简单token（实际项目中应使用JWT）
     */
    private String generateToken(String userId, String role) {
        return "icps_" + role + "_" + userId + "_" + System.currentTimeMillis();
    }
    
    /**
     * 转换学生实体为Map
     */
    private Map<String, Object> convertStudentToMap(StudentJpa student) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", student.getStuCardNo());
        userMap.put("studentId", student.getSno());
        userMap.put("name", student.getSname());
        userMap.put("gender", student.getSsex() == 1 ? "男" : "女");
        userMap.put("age", student.getSage());
        userMap.put("cardNo", student.getStuCardNo());
        userMap.put("address", student.getStuAddress());
        userMap.put("hobby", student.getShbt());
        userMap.put("bloodType", student.getSbloodType());
        userMap.put("zodiac", student.getSstartSign());
        userMap.put("evaluation", student.getSevaledType());
        userMap.put("department", student.getStuDept());
        userMap.put("major", student.getStuMajor());
        userMap.put("clazz", student.getStuClazz());
        userMap.put("region", student.getRegion());
        return userMap;
    }
    
    /**
     * 转换教师实体为Map
     */
    private Map<String, Object> convertTeacherToMap(TeacherJpa teacher) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", teacher.getTeacherId());
        userMap.put("cardNo", teacher.getTeacherCardNo());
        userMap.put("name", teacher.getTeacherName());
        userMap.put("department", teacher.getDept());
        userMap.put("title", teacher.getTitle());
        userMap.put("email", teacher.getEmail());
        userMap.put("phone", teacher.getPhone());
        userMap.put("status", teacher.getStatus() == 1 ? "在职" : "离职");
        return userMap;
    }
}