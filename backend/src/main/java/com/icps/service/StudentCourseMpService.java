package com.icps.service;

import com.icps.entity.mybatisplus.StudentCourseMp;

import java.util.List;
import java.util.Map;

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
}