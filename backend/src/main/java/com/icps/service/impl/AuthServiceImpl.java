package com.icps.service.impl;

import com.icps.aspect.Log;
import com.icps.entity.mybatisplus.StudentMp;
import com.icps.entity.mybatisplus.TeacherMp;
import com.icps.mapper.StudentMpMapper;
import com.icps.mapper.TeacherMpMapper;
import com.icps.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


/**
 * 认证服务
 */
@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private StudentMpMapper studentMapper;
    
    @Autowired
    private TeacherMpMapper teacherMapper;
    
    /**
     * 用户登录
     */
    @Log(value = "用户登录接口", printParams = true, printResult = true, printExecutionTime = true)
    @Override
    public Map<String, Object> login(String username, String password, String role) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            if ("student".equals(role)) {
                // 学生登录验证
                boolean loginSuccess = studentMapper.existsByCardNoAndName(password, username);
                if (loginSuccess) {
                    StudentMp student = studentMapper.selectById(password);
                    if (student != null) {
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
                boolean loginSuccess = teacherMapper.existsByCardNoAndName(password, username);
                if (loginSuccess) {
                    TeacherMp teacher = teacherMapper.selectByCardNo(password);
                    if (teacher != null) {
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
    @Log(value = "获取用户信息接口", printParams = true, printResult = true, printExecutionTime = true)
    @Override
    public Map<String, Object> getUserInfo(String userId, String role) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            if ("student".equals(role)) {
                StudentMp student = studentMapper.selectById(userId);
                if (student != null) {
                    result.put("success", true);
                    result.put("user", convertStudentToMap(student));
                } else {
                    result.put("success", false);
                    result.put("message", "学生信息不存在");
                }
            } else if ("teacher".equals(role)) {
                TeacherMp teacher = teacherMapper.selectByCardNo(userId);
                if (teacher != null) {
                    result.put("success", true);
                    result.put("user", convertTeacherToMap(teacher));
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
    private Map<String, Object> convertStudentToMap(StudentMp student) {
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
    private Map<String, Object> convertTeacherToMap(TeacherMp teacher) {
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