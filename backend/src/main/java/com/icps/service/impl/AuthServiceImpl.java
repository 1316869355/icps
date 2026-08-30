package com.icps.service.impl;

import com.icps.aspect.Log;
import com.icps.entity.StudentMp;
import com.icps.entity.TeacherMp;
import com.icps.entity.UserMp;
import com.icps.mapper.StudentMpMapper;
import com.icps.mapper.TeacherMpMapper;
import com.icps.security.JwtTokenProvider;
import com.icps.service.AuthService;
import com.icps.service.StudentMpService;
import com.icps.service.TeacherMpService;
import com.icps.service.UserMpService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;


/**
 * 认证服务
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private StudentMpMapper studentMapper;
    
    @Autowired
    private TeacherMpMapper teacherMapper;
    
    @Resource
    private UserMpService userMpService;

    @Resource
    private StudentMpService studentMpService;
    
    @Resource
    private TeacherMpService teacherMpService;

    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * 用户登录：BCrypt 校验密码，校验通过后签发 JWT
     */
    @Log(value = "用户登录接口", printParams = true, printResult = true, printExecutionTime = true)
    @Override
    public Map<String, Object> login(String username, String password, String role) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            UserMp user = userMpService.getByUsername(username);
            if (user == null || !userMpService.matchesPassword(password, user.getPassword())) {
                result.put("success", false);
                result.put("message", "账号或密码错误");
                return result;
            }
            if (user.getStatus() != null && user.getStatus() != 1) {
                result.put("success", false);
                result.put("message", "账号已停用");
                return result;
            }

            // 使用数据库中的实际角色，而非前端传入的role
            String actualRole = user.getRole();
            if (role != null && !role.trim().isEmpty() && !role.equals(actualRole)) {
                result.put("success", false);
                result.put("message", "角色不匹配，该账号实际角色为：" + actualRole);
                return result;
            }

            Map<String, Object> userMap;
            if ("student".equals(actualRole)) {
                StudentMp student = studentMapper.selectByUserId(user.getUserId());
                if (student == null) {
                    result.put("success", false);
                    result.put("message", "学生信息不存在");
                    return result;
                }
                userMap = studentMpService.convertStudentToMap(student);
            } else if ("teacher".equals(actualRole)) {
                TeacherMp teacher = teacherMapper.selectByUserId(user.getUserId());
                if (teacher == null) {
                    result.put("success", false);
                    result.put("message", "教师信息不存在");
                    return result;
                }
                userMap = teacherMpService.convertTeacherToMap(teacher);
            } else {
                userMap = userMpService.convertUserToMap(user);
            }

            userMap.put("username", user.getUsername());
            userMap.put("role", actualRole);
            userMpService.updateLastLogin(user.getUserId());

            result.put("success", true);
            result.put("message", "登录成功");
            result.put("role", actualRole);
            result.put("user", userMap);
            result.put("token", jwtTokenProvider.generateToken(user.getUserId(), user.getUsername(), actualRole));
            result.put("expiresIn", jwtTokenProvider.getExpirationMillis() / 1000);
        } catch (Exception e) {
            log.error("登录过程中发生错误: ", e);
            result.put("success", false);
            result.put("message", "登录过程中发生错误");
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
            Long userIdLong = Long.parseLong(userId);
            if ("student".equals(role)) {
                StudentMp student = studentMapper.selectByUserId(userIdLong);
                if (student != null) {
                    result.put("success", true);
                    result.put("user", studentMpService.convertStudentToMap(student));
                } else {
                    result.put("success", false);
                    result.put("message", "学生信息不存在");
                }
            } else if ("teacher".equals(role)) {
                TeacherMp teacher = teacherMapper.selectByUserId(userIdLong);
                if (teacher != null) {
                    result.put("success", true);
                    result.put("user", teacherMpService.convertTeacherToMap(teacher));
                } else {
                    result.put("success", false);
                    result.put("message", "教师信息不存在");
                }
            } else {
                result.put("success", false);
                result.put("message", "无效的角色类型");
            }
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "无效的用户ID格式");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取用户信息失败");
        }
        
        return result;
    }
    
}