package com.icps.service;

import java.util.List;
import java.util.Map;

import com.icps.entity.CourseMp;

/**
 * 课程服务 - MyBatisPlus版本
 */
public interface CourseMpService {

    /**
     * 获取所有课程列表
     */
    public List<Map<String, Object>> getAllCourses();

    /**
     * 按 courseId 加载课程实体（用于教师-课程归属校验）。
     * 逻辑删除由 @TableLogic 自动过滤。
     *
     * @return 不存在或已逻辑删除时返回 null
     */
    public CourseMp getCourseEntityById(Long courseId);
    
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

    /**
     * 按选课记录重算所有课程的已选人数
     */
    public Map<String, Object> syncEnrolled();
}