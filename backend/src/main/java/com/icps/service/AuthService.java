package com.icps.service;

import java.util.Map;


/**
 * 认证服务接口
 */
public interface AuthService {
    
    /**
     * 用户登录
     */
    Map<String, Object> login(String username, String password, String role);
    
    /**
     * 获取用户信息
     */
    Map<String, Object> getUserInfo(String userId, String role);
}