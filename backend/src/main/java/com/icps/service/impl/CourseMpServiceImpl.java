package com.icps.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.icps.entity.CourseMp;
import com.icps.mapper.CourseMpMapper;
import com.icps.service.CourseMpService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程服务 - MyBatisPlus版本
 */
@Slf4j
@Service
public class CourseMpServiceImpl implements CourseMpService {
    
    @Autowired
    private CourseMpMapper courseMpMapper;
    
    /**
     * 获取所有课程列表
     */
    @Override
    public List<Map<String, Object>> getAllCourses() {
        List<CourseMp> courses = courseMpMapper.selectList(null);
        return convertCoursesToMap(courses);
    }
    
    /**
     * 分页查询课程列表
     */
    @Override
    public Map<String, Object> getCoursesByPage(int pageNum, int pageSize) {
        Page<CourseMp> page = new Page<>(pageNum, pageSize);
        IPage<CourseMp> coursePage = courseMpMapper.selectPage(page, 
            new LambdaQueryWrapper<CourseMp>().orderByDesc(CourseMp::getCreatedAt));
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", convertCoursesToMap(coursePage.getRecords()));
        result.put("total", coursePage.getTotal());
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", coursePage.getPages());
        
        return result;
    }
    
    /**
     * 根据条件查询课程
     */
    @Override
    public List<Map<String, Object>> searchCourses(String name, String teacherName, String semester, Integer status) {
        LambdaQueryWrapper<CourseMp> wrapper = new LambdaQueryWrapper<>();
        
        if (name != null && !name.trim().isEmpty()) {
            wrapper.like(CourseMp::getCourseName, name);
        }
        if (teacherName != null && !teacherName.trim().isEmpty()) {
            wrapper.like(CourseMp::getTeacherName, teacherName);
        }
        if (semester != null && !semester.trim().isEmpty()) {
            wrapper.eq(CourseMp::getSemester, semester);
        }
        if (status != null) {
            wrapper.eq(CourseMp::getStatus, status);
        }
        
        List<CourseMp> courses = courseMpMapper.selectList(wrapper);
        return convertCoursesToMap(courses);
    }
    
    /**
     * 根据ID获取课程详情
     */
    @Override
    public Map<String, Object> getCourseById(Long courseId) {
        CourseMp course = courseMpMapper.selectById(courseId);
        if (course != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("course", convertCourseToMap(course));
            return result;
        } else {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "课程不存在");
            return result;
        }
    }
    
    /**
     * 更新课程信息
     */
    @Override
    public Map<String, Object> updateCourse(Long courseId, Map<String, Object> courseData) {
        CourseMp course = courseMpMapper.selectById(courseId);
        if (course != null) {
            
            // 更新课程信息
            if (courseData.containsKey("courseCode")) {
                course.setCourseCode((String) courseData.get("courseCode"));
            }
            if (courseData.containsKey("courseName")) {
                course.setCourseName((String) courseData.get("courseName"));
            }
            if (courseData.containsKey("credit")) {
                course.setCredit((Integer) courseData.get("credit"));
            }
            if (courseData.containsKey("hours")) {
                course.setHours((Integer) courseData.get("hours"));
            }
            if (courseData.containsKey("teacherId")) {
                course.setTeacherId((Long) courseData.get("teacherId"));
            }
            if (courseData.containsKey("teacherName")) {
                course.setTeacherName((String) courseData.get("teacherName"));
            }
            if (courseData.containsKey("classroom")) {
                course.setClassroom((String) courseData.get("classroom"));
            }
            if (courseData.containsKey("semester")) {
                course.setSemester((String) courseData.get("semester"));
            }
            if (courseData.containsKey("academicYear")) {
                course.setAcademicYear((String) courseData.get("academicYear"));
            }
            if (courseData.containsKey("schedule")) {
                course.setSchedule((String) courseData.get("schedule"));
            }
            if (courseData.containsKey("status")) {
                course.setStatus((Integer) courseData.get("status"));
            }
            if (courseData.containsKey("capacity")) {
                course.setCapacity((Integer) courseData.get("capacity"));
            }
            
            int result = courseMpMapper.updateById(course);
            
            Map<String, Object> response = new HashMap<>();
            if (result > 0) {
                response.put("success", true);
                response.put("message", "课程信息更新成功");
            } else {
                response.put("success", false);
                response.put("message", "课程信息更新失败");
            }
            return response;
        } else {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "课程不存在");
            return result;
        }
    }
    
    /**
     * 添加课程
     */
    @Override
    public Map<String, Object> addCourse(CourseMp course) {
        try {
            int result = courseMpMapper.insert(course);
            
            Map<String, Object> response = new HashMap<>();
            if (result > 0) {
                response.put("success", true);
                response.put("message", "课程添加成功");
            } else {
                response.put("success", false);
                response.put("message", "课程添加失败");
            }
            return response;
        } catch (Exception e) {
            log.error("添加课程失败: {}", e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "课程添加失败: " + e.getMessage());
            return response;
        }
    }
    
    /**
     * 删除课程
     */
    @Override
    public Map<String, Object> deleteCourse(Long courseId) {
        try {
            int result = courseMpMapper.deleteById(courseId);
            
            Map<String, Object> response = new HashMap<>();
            if (result > 0) {
                response.put("success", true);
                response.put("message", "课程删除成功");
            } else {
                response.put("success", false);
                response.put("message", "课程不存在或删除失败");
            }
            return response;
        } catch (Exception e) {
            log.error("删除课程失败: {}", e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "课程删除失败: " + e.getMessage());
            return response;
        }
    }
    
    /**
     * 获取课程统计信息
     */
    @Override
    public Map<String, Object> getCourseStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总课程数
        long totalCourses = courseMpMapper.selectCount(null);
        statistics.put("totalCourses", totalCourses);
        
        // 按学期统计
        List<Map<String, Object>> semesterStats = courseMpMapper.countBySemester();
        Map<String, Long> semesterCount = new HashMap<>();
        for (Map<String, Object> stat : semesterStats) {
            semesterCount.put((String) stat.get("semester"), (Long) stat.get("count"));
        }
        statistics.put("semesterStats", semesterCount);
        
        // 按教师统计
        List<Map<String, Object>> teacherStats = courseMpMapper.countByTeacher();
        Map<String, Long> teacherCount = new HashMap<>();
        for (Map<String, Object> stat : teacherStats) {
            teacherCount.put((String) stat.get("teacher_name"), (Long) stat.get("count"));
        }
        statistics.put("teacherStats", teacherCount);
        
        return statistics;
    }
    
    /**
     * 转换课程列表为Map列表
     */
    private List<Map<String, Object>> convertCoursesToMap(List<CourseMp> courses) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (CourseMp course : courses) {
            result.add(convertCourseToMap(course));
        }
        return result;
    }
    
    /**
     * 转换单个课程实体为Map
     */
    private Map<String, Object> convertCourseToMap(CourseMp course) {
        Map<String, Object> courseMap = new HashMap<>();
        courseMap.put("id", course.getCourseId());
        courseMap.put("code", course.getCourseCode());
        courseMap.put("name", course.getCourseName());
        courseMap.put("credit", course.getCredit());
        courseMap.put("hours", course.getHours());
        courseMap.put("teacherId", course.getTeacherId());
        courseMap.put("teacherName", course.getTeacherName());
        courseMap.put("classroom", course.getClassroom());
        courseMap.put("semester", course.getSemester());
        courseMap.put("academicYear", course.getAcademicYear());
        courseMap.put("schedule", course.getSchedule());
        courseMap.put("status", course.getStatus());
        courseMap.put("capacity", course.getCapacity());
        courseMap.put("enrolled", course.getEnrolled());
        courseMap.put("createdAt", course.getCreatedAt());
        courseMap.put("updatedAt", course.getUpdatedAt());
        return courseMap;
    }
}