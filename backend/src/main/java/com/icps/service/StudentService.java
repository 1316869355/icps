package com.icps.service;

import java.util.List;
import java.util.Map;

/**
 * 学生服务接口
 */
public interface StudentService {
    
    /**
     * 获取所有学生列表
     */
    List<Map<String, Object>> getAllStudents();
    
    /**
     * 分页查询学生列表
     */
    Map<String, Object> getStudentsByPage(int pageNum, int pageSize);
    
    /**
     * 根据条件查询学生
     */
    List<Map<String, Object>> searchStudents(String name, String dept, String major, Integer sex);
    
    /**
     * 根据ID获取学生详情
     */
    Map<String, Object> getStudentById(String studentId);
    
    /**
     * 更新学生信息
     */
    Map<String, Object> updateStudent(String studentId, Map<String, Object> studentData);
    
    /**
     * 删除学生
     */
    Map<String, Object> deleteStudent(String studentId);
    
    /**
     * 获取学生统计信息
     */
    Map<String, Object> getStudentStatistics();
}