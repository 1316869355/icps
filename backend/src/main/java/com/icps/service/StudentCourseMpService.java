package com.icps.service;

import java.util.List;
import java.util.Map;

import com.icps.entity.StudentCourseMp;

/**
 * 学生选课服务接口 - MyBatisPlus版本
 */
public interface StudentCourseMpService {
    
    /**
     * 获取所有选课记录
     */
    List<Map<String, Object>> getAllStudentCourses();
    
    /**
     * 根据学生身份证号查询选课记录
     */
    List<Map<String, Object>> getCoursesByStudent(String stuCardNo);
    
    /**
     * 根据课程ID查询选课记录
     */
    List<Map<String, Object>> getStudentsByCourse(Long courseId);
    
    /**
     * 添加选课记录
     */
    Map<String, Object> addStudentCourse(StudentCourseMp studentCourse);
    
    /**
     * 更新成绩
     */
    Map<String, Object> updateGrade(Long id, Double grade, Double gradePoint);
    
    /**
     * 删除选课记录
     */
    Map<String, Object> deleteStudentCourse(Long id);
    
    /**
     * 获取选课统计信息
     */
    Map<String, Object> getStudentCourseStatistics();

    /**
     * 选课（同步课程已选人数）
     */
    Map<String, Object> selectCourse(String stuCardNo, Long courseId);

    /**
     * 退课（同步课程已选人数）
     */
    Map<String, Object> dropCourse(String stuCardNo, Long courseId);

    /**
     * 查询学生成绩明细（关联课程信息）
     */
    List<Map<String, Object>> getGradeDetails(String stuCardNo);

    /**
     * 查询某门课程的成绩明细（关联学生信息）
     */
    List<Map<String, Object>> getCourseGradeDetails(Long courseId);
}