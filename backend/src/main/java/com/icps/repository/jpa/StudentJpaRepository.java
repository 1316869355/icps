package com.icps.repository.jpa;

import com.icps.entity.jpa.StudentJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 学生JPA Repository接口
 */
@Repository
public interface StudentJpaRepository extends JpaRepository<StudentJpa, String>, JpaSpecificationExecutor<StudentJpa> {
    
    /**
     * 根据学号查找学生
     */
    Optional<StudentJpa> findBySno(String sno);
    
    /**
     * 根据姓名模糊查询
     */
    List<StudentJpa> findBySnameContaining(String name);
    
    /**
     * 根据学院查询
     */
    List<StudentJpa> findByStuDept(String dept);
    
    /**
     * 根据专业查询
     */
    List<StudentJpa> findByStuMajor(String major);
    
    /**
     * 根据班级查询
     */
    List<StudentJpa> findByStuClazz(String clazz);
    
    /**
     * 根据性别查询
     */
    List<StudentJpa> findBySsex(Integer sex);
    
    /**
     * 学生登录验证
     */
    @Query("SELECT COUNT(s) > 0 FROM StudentJpa s WHERE s.stuCardNo = :cardNo AND s.sname LIKE %:name%")
    boolean existsByCardNoAndName(@Param("cardNo") String cardNo, @Param("name") String name);
    
    /**
     * 统计学生总数
     */
    @Query("SELECT COUNT(s) FROM StudentJpa s")
    long countStudents();
    
    /**
     * 按学院统计学生数量
     */
    @Query("SELECT s.stuDept, COUNT(s) FROM StudentJpa s GROUP BY s.stuDept")
    List<Object[]> countByDepartment();
}