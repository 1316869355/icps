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

    /**
     * 解析学生标识：纯数字按 user_id 解析，否则依次按学号、身份证号解析（自动过滤逻辑删除）
     */
    StudentMp resolveStudent(String studentId);

    /**
     * 按关键词模糊搜索学生（学号 / 姓名 / 班级 / 专业，四者 OR）
     */
    List<Map<String, Object>> searchStudentsByKeyword(String keyword);

    /**
     * 根据用户ID查询学生（自动过滤逻辑删除）
     */
    StudentMp getByUserId(Long userId);

    /**
     * 根据学号查询学生
     */
    StudentMp getBySno(String sno);

    /**
     * 根据身份证号查询学生
     */
    StudentMp getByCardNo(String cardNo);

    /**
     * 根据用户ID更新学生信息
     */
    Map<String, Object> updateStudentByUserId(Long userId, Map<String, Object> studentData);

    /**
     * 根据用户ID逻辑删除学生
     */
    Map<String, Object> deleteStudentByUserId(Long userId);
}