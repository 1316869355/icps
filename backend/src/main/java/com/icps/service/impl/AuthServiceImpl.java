package com.icps.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.icps.aspect.Log;
import com.icps.entity.StudentMp;
import com.icps.entity.TeacherMp;
import com.icps.entity.UserMp;
import com.icps.mapper.StudentMpMapper;
import com.icps.mapper.TeacherMpMapper;
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
    /**
     * 用户登录
     */
    @Log(value = "用户登录接口", printParams = true, printResult = true, printExecutionTime = true)
    @Override
    public Map<String, Object> login(String username, String password, String role) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean loginSuccess = userMpService.getUserByUsernameAndPassword(username, password);
            if (loginSuccess) {
                Map<String, Object> userMap = null;
                UserMp user = userMpService.getOne(Wrappers.<UserMp>lambdaQuery()
                        .eq(UserMp::getUsername, username));
                // 使用数据库中的实际角色，而非前端传入的role
                String actualRole = user.getRole();
                if ("student".equals(actualRole)) {
                    StudentMp student = studentMapper.selectByUserId(user.getUserId());
                    userMap = studentMpService.convertStudentToMap(student);
                } else if ("teacher".equals(actualRole)) {
                    TeacherMp teacher = teacherMapper.selectByUserId(user.getUserId());
                    userMap = teacherMpService.convertTeacherToMap(teacher);
                } else {
                    userMap = userMpService.convertUserToMap(user);
                }
                if (userMap != null) {
                    result.put("success", true);
                    result.put("message", "登录成功");
                    result.put("role", actualRole);
                    result.put("user", userMap);
                    result.put("token", generateToken(userMap.get("userId").toString(), actualRole));
                } else {
                    result.put("success", false);
                    result.put("message", "信息不存在");
                }
            } else {
                result.put("success", false);
                result.put("message", "账号或密码错误");
            }
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
    
    /**
     * 生成简单token（实际项目中应使用JWT）
     */
    private String generateToken(String userId, String role) {
        return "icps_" + role + "_" + userId + "_" + System.currentTimeMillis();
    }

}