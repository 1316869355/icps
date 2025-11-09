package com.icps.repository.jpa;

import com.icps.entity.jpa.TeacherJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 教师JPA Repository接口
 */
@Repository
public interface TeacherJpaRepository extends JpaRepository<TeacherJpa, Long>, JpaSpecificationExecutor<TeacherJpa> {
    
    /**
     * 根据身份证号查找教师
     */
    Optional<TeacherJpa> findByTeacherCardNo(String cardNo);
    
    /**
     * 根据姓名模糊查询
     */
    List<TeacherJpa> findByTeacherNameContaining(String name);
    
    /**
     * 根据学院查询
     */
    List<TeacherJpa> findByDept(String dept);
    
    /**
     * 根据状态查询
     */
    List<TeacherJpa> findByStatus(Integer status);
    
    /**
     * 教师登录验证
     */
    @Query("SELECT COUNT(t) > 0 FROM TeacherJpa t WHERE t.teacherCardNo = :cardNo AND t.teacherName LIKE %:name%")
    boolean existsByCardNoAndName(@Param("cardNo") String cardNo, @Param("name") String name);
    
    /**
     * 统计在职教师数量
     */
    @Query("SELECT COUNT(t) FROM TeacherJpa t WHERE t.status = 1")
    long countActiveTeachers();
}