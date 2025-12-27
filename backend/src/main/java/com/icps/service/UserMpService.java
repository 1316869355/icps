package com.icps.service;

import com.icps.entity.mybatisplus.UserMp;
import com.icps.mapper.UserMpMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户服务 - MyBatisPlus版本
 */
public interface UserMpService {

    /**
     * 获取所有用户列表
     */
    public List<Map<String, Object>> getAllUsers();
    
    /**
     * 根据角色查询用户
     */
    public List<Map<String, Object>> getUsersByRole(String role);
    
    /**
     * 根据用户名查找用户
     */
    public Map<String, Object> getUserByUsername(String username);
    
    /**
     * 用户登录验证
     */
    public Map<String, Object> login(String username, String password);
    
    /**
     * 更新用户信息
     */
    public Map<String, Object> updateUser(Long userId, Map<String, Object> userData);
      
    
    /**
     * 添加用户
     */
    public Map<String, Object> addUser(UserMp user);
    
    /**
     * 删除用户
     */
    public Map<String, Object> deleteUser(Long userId);
   
    
    /**
     * 获取用户统计信息
     */
    public Map<String, Object> getUserStatistics();
}