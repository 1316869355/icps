package com.icps.repository.jpa;

import com.icps.entity.jpa.CourseJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 课程JPA Repository接口
 */
@Repository
public interface CourseJpaRepository extends JpaRepository<CourseJpa, Long>, JpaSpecificationExecutor<CourseJpa> {
    
    /**
     * 根据课程代码查找课程
     */
    Optional<CourseJpa> findByCourseCode(String courseCode);
    
    /**
     * 根据课程名称模糊查询
     */
    List<CourseJpa> findByCourseNameContaining(String name);
    
    /**
     * 根据教师ID查询课程
     */
    List<CourseJpa> findByTeacherId(Long teacherId);
    
    /**
     * 根据学期查询课程
     */
    List<CourseJpa> findBySemester(String semester);
    
    /**
     * 根据学年查询课程
     */
    List<CourseJpa> findByAcademicYear(String academicYear);
    
    /**
     * 根据状态查询课程
     */
    List<CourseJpa> findByStatus(Integer status);
    
    /**
     * 查询有剩余容量的课程
     */
    @Query("SELECT c FROM CourseJpa c WHERE c.enrolled < c.capacity AND c.status = 1")
    List<CourseJpa> findAvailableCourses();
    
    /**
     * 按学期统计课程数量
     */
    @Query("SELECT c.semester, COUNT(c) FROM CourseJpa c GROUP BY c.semester")
    List<Object[]> countBySemester();
}