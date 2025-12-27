package com.icps.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.icps.entity.TeacherMp;
import com.icps.mapper.TeacherMpMapper;
import com.icps.service.TeacherMpService;

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
@Slf4j
@Service
public class TeacherMpServiceImpl implements TeacherMpService {
    
    @Autowired
    private TeacherMpMapper teacherMpMapper;
    
    /**
     * 获取所有教师列表
     */
    @Override
    public List<Map<String, Object>> getAllTeachers() {
        List<TeacherMp> teachers = teacherMpMapper.selectList(null);
        return convertTeachersToMap(teachers);
    }
    
    /**
     * 分页查询教师列表
     */
    @Override
    public Map<String, Object> getTeachersByPage(int pageNum, int pageSize) {
        Page<TeacherMp> page = new Page<>(pageNum, pageSize);
        IPage<TeacherMp> teacherPage = teacherMpMapper.selectPage(page, 
            new LambdaQueryWrapper<TeacherMp>().orderByDesc(TeacherMp::getCreatedAt));
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", convertTeachersToMap(teacherPage.getRecords()));
        result.put("total", teacherPage.getTotal());
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", teacherPage.getPages());
        
        return result;
    }
    
    /**
     * 根据条件查询教师
     */
    @Override
    public List<Map<String, Object>> searchTeachers(String name, String dept, String title, Integer status) {
        LambdaQueryWrapper<TeacherMp> wrapper = new LambdaQueryWrapper<>();
        
        if (name != null && !name.trim().isEmpty()) {
            wrapper.like(TeacherMp::getTeacherName, name);
        }
        if (dept != null && !dept.trim().isEmpty()) {
            wrapper.eq(TeacherMp::getDept, dept);
        }
        if (title != null && !title.trim().isEmpty()) {
            wrapper.eq(TeacherMp::getTitle, title);
        }
        if (status != null) {
            wrapper.eq(TeacherMp::getStatus, status);
        }
        
        List<TeacherMp> teachers = teacherMpMapper.selectList(wrapper);
        return convertTeachersToMap(teachers);
    }
    
    /**
     * 根据ID获取教师详情
     */
    @Override
    public Map<String, Object> getTeacherById(Long teacherId) {
        TeacherMp teacher = teacherMpMapper.selectById(teacherId);
        if (teacher != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("teacher", convertTeacherToMap(teacher));
            return result;
        } else {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "教师不存在");
            return result;
        }
    }
    
    /**
     * 更新教师信息
     */
    @Override
    public Map<String, Object> updateTeacher(Long teacherId, Map<String, Object> teacherData) {
        TeacherMp teacher = teacherMpMapper.selectById(teacherId);
        if (teacher != null) {
            
            // 更新教师信息
            if (teacherData.containsKey("teacherName")) {
                teacher.setTeacherName((String) teacherData.get("teacherName"));
            }
            if (teacherData.containsKey("teacherCardNo")) {
                teacher.setTeacherCardNo((String) teacherData.get("teacherCardNo"));
            }
            if (teacherData.containsKey("dept")) {
                teacher.setDept((String) teacherData.get("dept"));
            }
            if (teacherData.containsKey("title")) {
                teacher.setTitle((String) teacherData.get("title"));
            }
            if (teacherData.containsKey("email")) {
                teacher.setEmail((String) teacherData.get("email"));
            }
            if (teacherData.containsKey("phone")) {
                teacher.setPhone((String) teacherData.get("phone"));
            }
            if (teacherData.containsKey("status")) {
                teacher.setStatus((Integer) teacherData.get("status"));
            }
            
            int result = teacherMpMapper.updateById(teacher);
            
            Map<String, Object> response = new HashMap<>();
            if (result > 0) {
                response.put("success", true);
                response.put("message", "教师信息更新成功");
            } else {
                response.put("success", false);
                response.put("message", "教师信息更新失败");
            }
            return response;
        } else {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "教师不存在");
            return result;
        }
    }
    
    /**
     * 添加教师
     */
    @Override
    public Map<String, Object> addTeacher(TeacherMp teacher) {
        try {
            int result = teacherMpMapper.insert(teacher);
            
            Map<String, Object> response = new HashMap<>();
            if (result > 0) {
                response.put("success", true);
                response.put("message", "教师添加成功");
            } else {
                response.put("success", false);
                response.put("message", "教师添加失败");
            }
            return response;
        } catch (Exception e) {
            log.error("添加教师失败: {}", e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "教师添加失败: " + e.getMessage());
            return response;
        }
    }
    
    /**
     * 删除教师
     */
    @Override
    public Map<String, Object> deleteTeacher(Long teacherId) {
        try {
            int result = teacherMpMapper.deleteById(teacherId);
            
            Map<String, Object> response = new HashMap<>();
            if (result > 0) {
                response.put("success", true);
                response.put("message", "教师删除成功");
            } else {
                response.put("success", false);
                response.put("message", "教师不存在或删除失败");
            }
            return response;
        } catch (Exception e) {
            log.error("删除教师失败: {}", e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "教师删除失败: " + e.getMessage());
            return response;
        }
    }
    
    /**
     * 获取教师统计信息
     */
    @Override
    public Map<String, Object> getTeacherStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总教师数
        long totalTeachers = teacherMpMapper.selectCount(null);
        statistics.put("totalTeachers", totalTeachers);
        
        // 按学院统计
        List<Map<String, Object>> deptStats = teacherMpMapper.countByDepartment();
        Map<String, Long> deptCount = new HashMap<>();
        for (Map<String, Object> stat : deptStats) {
            deptCount.put((String) stat.get("dept"), (Long) stat.get("count"));
        }
        statistics.put("departmentStats", deptCount);
        
        // 按职称统计
        List<Map<String, Object>> titleStats = teacherMpMapper.countByTitle();
        Map<String, Long> titleCount = new HashMap<>();
        for (Map<String, Object> stat : titleStats) {
            titleCount.put((String) stat.get("title"), (Long) stat.get("count"));
        }
        statistics.put("titleStats", titleCount);
        
        return statistics;
    }
    
    /**
     * 转换教师列表为Map列表
     */
    private List<Map<String, Object>> convertTeachersToMap(List<TeacherMp> teachers) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (TeacherMp teacher : teachers) {
            result.add(convertTeacherToMap(teacher));
        }
        return result;
    }
    
    /**
     * 转换单个教师实体为Map
     */
    private Map<String, Object> convertTeacherToMap(TeacherMp teacher) {
        Map<String, Object> teacherMap = new HashMap<>();
        teacherMap.put("id", teacher.getTeacherId());
        teacherMap.put("cardNo", teacher.getTeacherCardNo());
        teacherMap.put("name", teacher.getTeacherName());
        teacherMap.put("dept", teacher.getDept());
        teacherMap.put("title", teacher.getTitle());
        teacherMap.put("email", teacher.getEmail());
        teacherMap.put("phone", teacher.getPhone());
        teacherMap.put("status", teacher.getStatus());
        teacherMap.put("createdAt", teacher.getCreatedAt());
        teacherMap.put("updatedAt", teacher.getUpdatedAt());
        return teacherMap;
    }
}