package com.icps.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.icps.entity.UserMp;

/**
 * 用户服务 - MyBatisPlus版本
 */
public interface UserMpService extends IService<UserMp> {

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

    /**
     * 根据用户名和密码查询用户（使用BCrypt校验）
     */
    boolean getUserByUsernameAndPassword(String username, String password);

    /**
     * 根据用户名查询用户实体（自动过滤逻辑删除）
     */
    UserMp getByUsername(String username);

    /**
     * 明文密码与BCrypt密文比对
     */
    boolean matchesPassword(String rawPassword, String encodedPassword);

    /**
     * 更新最后登录时间
     */
    void updateLastLogin(Long userId);

    /**
     * 转换单个用户实体为Map
     */
    Map<String, Object> convertUserToMap(UserMp user);
}