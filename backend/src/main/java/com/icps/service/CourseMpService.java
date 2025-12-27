package com.icps.service;

import com.icps.entity.mybatisplus.CourseMp;
import java.util.List;
import java.util.Map;

/**
 * 课程服务 - MyBatisPlus版本
 */
public interface CourseMpService {
    
    /**
     * 获取所有课程列表
     */
    public List<Map<String, Object>> getAllCourses();
    
    /**
     * 分页查询课程列表
     */
    public Map<String, Object> getCoursesByPage(int pageNum, int pageSize);
    
    /**
     * 根据条件查询课程
     */
    public List<Map<String, Object>> searchCourses(String name, String teacherName, String semester, Integer status);
    
    /**
     * 根据ID获取课程详情
     */
    public Map<String, Object> getCourseById(Long courseId);
    
    /**
     * 更新课程信息
     */
    public Map<String, Object> updateCourse(Long courseId, Map<String, Object> courseData);
    
    /**
     * 添加课程
     */
    public Map<String, Object> addCourse(CourseMp course);
    
    /**
     * 删除课程
     */
    public Map<String, Object> deleteCourse(Long courseId);
    
    /**
     * 获取课程统计信息
     */
    public Map<String, Object> getCourseStatistics();
}