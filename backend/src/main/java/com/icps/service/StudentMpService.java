package com.icps.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.icps.entity.StudentMp;

/**
 * 学生服务 - MyBatisPlus版本
 */
public interface StudentMpService extends IService<StudentMp> {
    
    /**
     * 获取所有学生列表
     */
    public List<Map<String, Object>> getAllStudents();
    
    /**
     * 分页查询学生列表
     */
    public Map<String, Object> getStudentsByPage(int pageNum, int pageSize);
    
    /**
     * 根据条件查询学生
     */
    public List<Map<String, Object>> searchStudents(String name, String dept, String major, Integer sex);
    
    /**
     * 根据ID获取学生详情
     */
    public Map<String, Object> getStudentById(String studentId);
    
    /**
     * 更新学生信息
     */
    public Map<String, Object> updateStudent(String studentId, Map<String, Object> studentData);
    
    /**
     * 添加学生
     */
    public Map<String, Object> addStudent(StudentMp student);
    
    /**
     * 删除学生
     */
    public Map<String, Object> deleteStudent(String studentId);
    
    /**
     * 获取学生统计信息
     */
    public Map<String, Object> getStudentStatistics();

    /**
     * 转换单个学生实体为Map
     */
    Map<String, Object> convertStudentToMap(StudentMp student);
    
}