package com.icps.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icps.entity.UserMp;
import com.icps.mapper.UserMpMapper;
import com.icps.security.SecurityUtils;
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

    /**
     * 学生 user_id 保留段下界（含）。
     *
     * <p>当前学生档案 user_id 落在 2023001011..2023001020，后续新建学生将继续沿用
     * 该段。任何「非学生创建流程」产生的新用户若被分配到该段的 user_id，
     * 会与 {@code icps_stu.user_id} 撞车，经 {@code SecurityUtils.canAccessStudent}
     * （按 user_id 比对）误判为本人而接管真实学生档案——水平越权。</p>
     */
    public static final long STUDENT_USERID_RESERVED_LOW = 2023001011L;

    /**
     * 学生 user_id 保留段上界（含）。给后续新建学生留出余量。
     */
    public static final long STUDENT_USERID_RESERVED_HIGH = 2023001099L;

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
     *
     * <p>含 user_id 水平越权防御：</p>
     * <ol>
     *   <li>插入后读取 MyBatis-Plus 回填的 user_id；</li>
     *   <li>若新用户 role != "student" 且 user_id 落入学生保留段
     *       {@code 2023001011..2023001099}，立即逻辑删除该用户并拒绝——
     *       否则会与 {@code icps_stu.user_id} 撞车，经
     *       {@code SecurityUtils.canAccessStudent}（按 user_id 比对）误判为
     *       本人而接管真实学生档案；</li>
     *   <li>对所有非学生用户，额外断言其 user_id 不等于任何已存在
     *       （含逻辑删除）的 {@code icps_stu.user_id}，防接管历史学生档案。</li>
     * </ol>
     * <p>正常情况下 V99 迁移已把 {@code icps_user.AUTO_INCREMENT} 调到
     * 2023002001（避开学生段），auto 值不会落入学生段；本守护用于迁移未应用、
     * 手动重置、或运维异常的场景。</p>
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
                Long newUserId = user.getUserId();
                String role = user.getRole();

                // 越权防御：非学生用户 user_id 不得落入学生保留段，且不得撞上已有 icps_stu.user_id
                if (newUserId != null && !SecurityUtils.ROLE_STUDENT.equalsIgnoreCase(role)) {
                    boolean inStudentRange =
                            newUserId >= STUDENT_USERID_RESERVED_LOW
                                    && newUserId <= STUDENT_USERID_RESERVED_HIGH;
                    long collisionCount =
                            userMpMapper.countStudentsByUserIdIncludingDeleted(newUserId);
                    if (inStudentRange || collisionCount > 0) {
                        // 回滚刚插入的用户，避免被越权利用
                        userMpMapper.deleteById(newUserId);
                        log.error("拒绝创建非学生用户：user_id={} 落入学生保留段或与 icps_stu 冲突"
                                + "（inRange={}, stuCollision={}）。请检查 icps_user.AUTO_INCREMENT 是否"
                                + " 已迁移到安全值（参见 V99 迁移）。",
                                newUserId, inStudentRange, collisionCount);
                        response.put("success", false);
                        response.put("message", "用户创建失败：分配到的 user_id 与学生档案冲突，"
                                + "请联系管理员检查 AUTO_INCREMENT 设置");
                        return response;
                    }
                }

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