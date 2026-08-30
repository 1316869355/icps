package com.icps.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.icps.entity.UserMp;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 用户Mapper接口 - MyBatisPlus版本
 */
@Mapper
public interface UserMpMapper extends BaseMapper<UserMp> {
    
    /**
     * 根据用户名查找用户
     */
    @Select("SELECT * FROM icps_user WHERE username = #{username} AND deleted = 0")
    UserMp selectByUsername(@Param("username") String username);
    
    /**
     * 根据角色查询用户
     */
    @Select("SELECT * FROM icps_user WHERE role = #{role} AND deleted = 0")
    List<UserMp> selectByRole(@Param("role") String role);

    /**
     * 验证用户登录
     */
    @Select("SELECT COUNT(*) > 0 FROM icps_user WHERE username = #{username} AND password = #{password} AND deleted = 0")
    boolean validateLogin(@Param("username") String username, @Param("password") String password);
    
    /**
     * 统计用户总数
     */
    @Select("SELECT COUNT(*) FROM icps_user")
    long countUsers();
    
    /**
     * 按角色统计用户数量
     */
    @Select("SELECT role, COUNT(*) as count FROM icps_user GROUP BY role")
    List<Map<String, Object>> countByRole();
}