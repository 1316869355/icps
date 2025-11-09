package com.icps.repository.jpa;

import com.icps.entity.jpa.UserJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户认证JPA Repository
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserJpa, Long> {
    
    /**
     * 根据用户名查找用户
     */
    Optional<UserJpa> findByUsername(String username);
    
    /**
     * 根据角色查找用户列表
     */
    List<UserJpa> findByRole(String role);
    
    /**
     * 根据学生身份证号查找用户
     */
    Optional<UserJpa> findByStuCardNo(String stuCardNo);
    
    /**
     * 根据教师ID查找用户
     */
    Optional<UserJpa> findByTeacherId(Long teacherId);
    
    /**
     * 根据用户名和状态查找用户
     */
    @Query("SELECT u FROM UserJpa u WHERE u.username = :username AND u.status = :status")
    Optional<UserJpa> findByUsernameAndStatus(@Param("username") String username, @Param("status") Integer status);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 根据角色和状态查找用户
     */
    List<UserJpa> findByRoleAndStatus(String role, Integer status);
    
    /**
     * 根据学生身份证号和角色查找用户
     */
    Optional<UserJpa> findByStuCardNoAndRole(String stuCardNo, String role);
    
    /**
     * 根据教师ID和角色查找用户
     */
    Optional<UserJpa> findByTeacherIdAndRole(Long teacherId, String role);
}