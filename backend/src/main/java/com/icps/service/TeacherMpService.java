package com.icps.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.icps.entity.mybatisplus.TeacherMp;
import com.icps.mapper.TeacherMpMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教师服务 - MyBatisPlus版本
 */
public interface TeacherMpService {

    /**
     * 获取所有教师列表
     */
    public List<Map<String, Object>> getAllTeachers();
    
    /**
     * 分页查询教师列表
     */
    public Map<String, Object> getTeachersByPage(int pageNum, int pageSize);
    
    /**
     * 根据条件查询教师
     */
    public List<Map<String, Object>> searchTeachers(String name, String dept, String title, Integer status);
    
    /**
     * 根据ID获取教师详情
     */
    public Map<String, Object> getTeacherById(Long teacherId);
    
    /**
     * 更新教师信息
     */
    public Map<String, Object> updateTeacher(Long teacherId, Map<String, Object> teacherData);
     
    /**
     * 添加教师
     */
    public Map<String, Object> addTeacher(TeacherMp teacher);
    
    /**
     * 删除教师
     */
    public Map<String, Object> deleteTeacher(Long teacherId);
    
    /**
     * 获取教师统计信息
     */
    public Map<String, Object> getTeacherStatistics();
}