package com.icps.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icps.entity.UserMp;
import com.icps.mapper.UserMpMapper;
import com.icps.service.UserMpService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户服务 - MyBatisPlus版本
 */
@Slf4j
@Service
public class UserMpServiceImpl extends ServiceImpl<UserMpMapper, UserMp> implements UserMpService {

    
    @Autowired
    private UserMpMapper userMpMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 获取所有用户列表
     */
    @Override
    public List<Map<String, Object>> getAllUsers() {
        List<UserMp> users = userMpMapper.selectList(null);
        return convertUsersToMap(users);
    }
    
    /**
     * 根据用户名和密码查询用户
     */
    @Override
    public boolean getUserByUsernameAndPassword(String username, String password) {
        UserMp user = userMpMapper.selectByUsername(username);
        if (user != null) {
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }

    /**
     * 根据用户名查询用户实体
     */
    @Override
    public UserMp getByUsername(String username) {
        return userMpMapper.selectByUsername(username);
    }

    /**
     * 明文密码与BCrypt密文比对
     */
    @Override
    public boolean matchesPassword(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 根据角色查询用户
     */
    @Override
    public List<Map<String, Object>> getUsersByRole(String role) {
        List<UserMp> users = userMpMapper.selectByRole(role);
        return convertUsersToMap(users);
    }
    
    /**
     * 根据用户名查找用户
     */
    @Override
    public Map<String, Object> getUserByUsername(String username) {
        UserMp user = userMpMapper.selectByUsername(username);
        if (user != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("user", convertUserToMap(user));
            return result;
        } else {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }
    }
    
    /**
     * 更新最后登录时间
     */
    @Override
    public void updateLastLogin(Long userId) {
        if (userId == null) {
            return;
        }
        // 只更新 last_login 一列，避免实体字段默认值（如 role 默认 student）被一并写回
        userMpMapper.update(null, new LambdaUpdateWrapper<UserMp>()
                .eq(UserMp::getUserId, userId)
                .set(UserMp::getLastLogin, LocalDateTime.now()));
    }
    
    /**
     * 更新用户信息
     */
    @Override
    public Map<String, Object> updateUser(Long userId, Map<String, Object> userData) {
        UserMp user = userMpMapper.selectById(userId);
        if (user != null) {
            
            // 更新用户信息
            if (userData.containsKey("username")) {
                user.setUsername((String) userData.get("username"));
            }
            if (userData.containsKey("password") && userData.get("password") != null) {
                user.setPassword(passwordEncoder.encode((String) userData.get("password")));
            }
            if (userData.containsKey("role")) {
                user.setRole((String) userData.get("role"));
            }
            if (userData.containsKey("status")) {
                user.setStatus((Integer) userData.get("status"));
            }
            
            int result = userMpMapper.updateById(user);
            
            Map<String, Object> response = new HashMap<>();
            if (result > 0) {
                response.put("success", true);
                response.put("message", "用户信息更新成功");
            } else {
                response.put("success", false);
                response.put("message", "用户信息更新失败");
            }
            return response;
        } else {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "用户不存在");
            return result;
        }
    }
    
    /**
     * 添加用户
     */
    @Override
    public Map<String, Object> addUser(UserMp user) {
        try {
            // 检查用户名是否已存在
            UserMp existingUser = userMpMapper.selectByUsername(user.getUsername());
            if (existingUser != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "用户名已存在");
                return response;
            }
            
            // 密码加密存储
            if (user.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            int result = userMpMapper.insert(user);
            
            Map<String, Object> response = new HashMap<>();
            if (result > 0) {
                response.put("success", true);
                response.put("message", "用户添加成功");
            } else {
                response.put("success", false);
                response.put("message", "用户添加失败");
            }
            return response;
        } catch (Exception e) {
            log.error("添加用户失败: {}", e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "用户添加失败: " + e.getMessage());
            return response;
        }
    }
    
    /**
     * 删除用户
     */
    @Override
    public Map<String, Object> deleteUser(Long userId) {
        try {
            int result = userMpMapper.deleteById(userId);
            
            Map<String, Object> response = new HashMap<>();
            if (result > 0) {
                response.put("success", true);
                response.put("message", "用户删除成功");
            } else {
                response.put("success", false);
                response.put("message", "用户不存在或删除失败");
            }
            return response;
        } catch (Exception e) {
            log.error("删除用户失败: {}", e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "用户删除失败: " + e.getMessage());
            return response;
        }
    }
    
    /**
     * 获取用户统计信息
     */
    @Override
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总用户数
        long totalUsers = userMpMapper.selectCount(null);
        statistics.put("totalUsers", totalUsers);
        
        // 按角色统计
        List<Map<String, Object>> roleStats = userMpMapper.countByRole();
        Map<String, Long> roleCount = new HashMap<>();
        for (Map<String, Object> stat : roleStats) {
            roleCount.put((String) stat.get("role"), (Long) stat.get("count"));
        }
        statistics.put("roleStats", roleCount);
        
        return statistics;
    }
    
    /**
     * 转换用户列表为Map列表
     */
    private List<Map<String, Object>> convertUsersToMap(List<UserMp> users) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (UserMp user : users) {
            result.add(convertUserToMap(user));
        }
        return result;
    }
    
    /**
     * 转换单个用户实体为Map
     */
    @Override
    public Map<String, Object> convertUserToMap(UserMp user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("userId", user.getUserId());
        userMap.put("username", user.getUsername());
        userMap.put("role", user.getRole());
        userMap.put("status", user.getStatus());
        userMap.put("lastLogin", user.getLastLogin());
        userMap.put("createdAt", user.getCreatedAt());
        userMap.put("updatedAt", user.getUpdatedAt());
        return userMap;
    }
}