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
     * 统计用户总数
     */
    @Select("SELECT COUNT(*) FROM icps_user WHERE deleted = 0")
    long countUsers();
    
    /**
     * 按角色统计用户数量
     */
    @Select("SELECT role, COUNT(*) as count FROM icps_user WHERE deleted = 0 GROUP BY role")
    List<Map<String, Object>> countByRole();

    /**
     * 查询某 user_id 是否已被任何（含逻辑删除的）icps_stu 记录占用。
     *
     * <p>用途：用户创建时防御水平越权——若新建非学生用户被分配到与现有学生档案
     * 相同的 user_id，该用户可经 {@code SecurityUtils.canAccessStudent} 接管真实
     * 学生档案（按 user_id 比对）。这里显式查询含 deleted 记录，避免逻辑删除后
     * 历史学生档案被新用户接管。</p>
     */
    @Select("SELECT COUNT(*) FROM icps_stu WHERE user_id = #{userId}")
    long countStudentsByUserIdIncludingDeleted(@Param("userId") Long userId);
}