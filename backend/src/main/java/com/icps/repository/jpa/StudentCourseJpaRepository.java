package com.icps.repository.jpa;

import com.icps.entity.jpa.StudentCourseJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 学生选课关联JPA Repository
 */
@Repository
public interface StudentCourseJpaRepository extends JpaRepository<StudentCourseJpa, Long> {
    
    /**
     * 根据学生身份证号查找选课记录
     */
    List<StudentCourseJpa> findByStuCardNo(String stuCardNo);
    
    /**
     * 根据课程ID查找选课记录
     */
    List<StudentCourseJpa> findByCourseId(Long courseId);
    
    /**
     * 根据学生身份证号和课程ID查找特定选课记录
     */
    StudentCourseJpa findByStuCardNoAndCourseId(String stuCardNo, Long courseId);
    
    /**
     * 根据学生身份证号、课程ID、学年和学期查找选课记录
     */
    StudentCourseJpa findByStuCardNoAndCourseIdAndAcademicYearAndSemester(
            @Param("stuCardNo") String stuCardNo,
            @Param("courseId") Long courseId,
            @Param("academicYear") String academicYear,
            @Param("semester") String semester);
    
    /**
     * 统计指定课程的选课人数
     */
    @Query("SELECT COUNT(sc) FROM StudentCourseJpa sc WHERE sc.courseId = :courseId AND sc.status = 1")
    long countByCourseId(@Param("courseId") Long courseId);
    
    /**
     * 根据学生身份证号和学年学期查找选课记录
     */
    List<StudentCourseJpa> findByStuCardNoAndAcademicYearAndSemester(
            @Param("stuCardNo") String stuCardNo,
            @Param("academicYear") String academicYear,
            @Param("semester") String semester);
    
    /**
     * 根据课程ID和学年学期查找选课记录
     */
    List<StudentCourseJpa> findByCourseIdAndAcademicYearAndSemester(
            @Param("courseId") Long courseId,
            @Param("academicYear") String academicYear,
            @Param("semester") String semester);
}