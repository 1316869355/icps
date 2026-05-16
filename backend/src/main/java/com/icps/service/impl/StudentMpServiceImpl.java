package com.icps.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icps.entity.StudentMp;
import com.icps.mapper.StudentMpMapper;
import com.icps.service.StudentMpService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生服务 - MyBatisPlus版本
 */
@Slf4j
@Service
public class StudentMpServiceImpl extends ServiceImpl<StudentMpMapper, StudentMp> implements StudentMpService {
    
    @Autowired
    private StudentMpMapper studentMpMapper;
    
    /**
     * 获取所有学生列表
     */
    @Override
    public List<Map<String, Object>> getAllStudents() {
        List<StudentMp> students = studentMpMapper.selectList(null);
        return convertStudentsToMap(students);
    }
    
    /**
     * 分页查询学生列表
     */
    @Override
    public Map<String, Object> getStudentsByPage(int pageNum, int pageSize) {
        Page<StudentMp> page = new Page<>(pageNum, pageSize);
        IPage<StudentMp> studentPage = studentMpMapper.selectPage(page, 
            new LambdaQueryWrapper<StudentMp>().orderByDesc(StudentMp::getCreatedAt));
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", convertStudentsToMap(studentPage.getRecords()));
        result.put("total", studentPage.getTotal());
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", studentPage.getPages());
        
        return result;
    }
    
    /**
     * 根据条件查询学生
     */
    @Override
    public List<Map<String, Object>> searchStudents(String name, String dept, String major, Integer sex) {
        LambdaQueryWrapper<StudentMp> wrapper = new LambdaQueryWrapper<>();
        
        if (name != null && !name.trim().isEmpty()) {
            wrapper.like(StudentMp::getSname, name);
        }
        if (!"0".equals(dept)) {
            wrapper.eq(StudentMp::getStuDept, dept);
        }
        if (!"0".equals(major)) {
            wrapper.eq(StudentMp::getStuMajor, major);
        }
        if (sex != null && sex != 0) {
            wrapper.eq(StudentMp::getSsex, sex);
        }
        
        List<StudentMp> students = studentMpMapper.selectList(wrapper);
        return convertStudentsToMap(students);
    }
    
    /**
     * 根据ID获取学生详情
     */
    @Override
    public Map<String, Object> getStudentById(String studentId) {
        try {
            Long userId = Long.parseLong(studentId);
            StudentMp student = studentMpMapper.selectByUserId(userId);
            if (student != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("student", convertStudentToMap(student));
                return result;
            } else {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "学生不存在");
                return result;
            }
        } catch (NumberFormatException e) {
            log.error("无效的学生ID格式: {}", studentId);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "无效的学生ID格式");
            return result;
        }
    }
    
    /**
     * 更新学生信息
     */
    @Override
    public Map<String, Object> updateStudent(String studentId, Map<String, Object> studentData) {
        StudentMp student = studentMpMapper.selectById(studentId);
        if (student != null) {
            
            // 更新学生信息
            if (studentData.containsKey("sname")) {
                student.setSname((String) studentData.get("sname"));
            }
            if (studentData.containsKey("ssex")) {
                student.setSsex((Integer) studentData.get("ssex"));
            }
            if (studentData.containsKey("sage")) {
                student.setSage((Integer) studentData.get("sage"));
            }
            if (studentData.containsKey("stuAddress")) {
                student.setStuAddress((String) studentData.get("stuAddress"));
            }
            if (studentData.containsKey("shbt")) {
                student.setShbt((String) studentData.get("shbt"));
            }
            if (studentData.containsKey("sbloodType")) {
                student.setSbloodType((String) studentData.get("sbloodType"));
            }
            if (studentData.containsKey("sstartSign")) {
                student.setSstartSign((String) studentData.get("sstartSign"));
            }
            if (studentData.containsKey("sevaledType")) {
                student.setSevaledType((String) studentData.get("sevaledType"));
            }
            if (studentData.containsKey("stuDept")) {
                student.setStuDept((String) studentData.get("stuDept"));
            }
            if (studentData.containsKey("stuMajor")) {
                student.setStuMajor((String) studentData.get("stuMajor"));
            }
            if (studentData.containsKey("stuClazz")) {
                student.setStuClazz((String) studentData.get("stuClazz"));
            }
            if (studentData.containsKey("region")) {
                student.setRegion((String) studentData.get("region"));
            }
            
            int result = studentMpMapper.updateById(student);
            
            Map<String, Object> response = new HashMap<>();
            if (result > 0) {
                response.put("success", true);
                response.put("message", "学生信息更新成功");
            } else {
                response.put("success", false);
                response.put("message", "学生信息更新失败");
            }
            return response;
        } else {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "学生不存在");
            return result;
        }
    }
    
    /**
     * 添加学生
     */
    @Override
    public Map<String, Object> addStudent(StudentMp student) {
        try {
            int result = studentMpMapper.insert(student);
            
            Map<String, Object> response = new HashMap<>();
            if (result > 0) {
                response.put("success", true);
                response.put("message", "学生添加成功");
            } else {
                response.put("success", false);
                response.put("message", "学生添加失败");
            }
            return response;
        } catch (Exception e) {
            log.error("添加学生失败: {}", e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "学生添加失败: " + e.getMessage());
            return response;
        }
    }
    
    /**
     * 删除学生
     */
    @Override
    public Map<String, Object> deleteStudent(String studentId) {
        try {
            int result = studentMpMapper.deleteById(studentId);
            
            Map<String, Object> response = new HashMap<>();
            if (result > 0) {
                response.put("success", true);
                response.put("message", "学生删除成功");
            } else {
                response.put("success", false);
                response.put("message", "学生不存在或删除失败");
            }
            return response;
        } catch (Exception e) {
            log.error("删除学生失败: {}", e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "学生删除失败: " + e.getMessage());
            return response;
        }
    }
    
    /**
     * 获取学生统计信息
     */
    @Override
    public Map<String, Object> getStudentStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总学生数
        long totalStudents = studentMpMapper.selectCount(null);
        statistics.put("totalStudents", totalStudents);
        
        // 按学院统计
        List<Map<String, Object>> deptStats = studentMpMapper.countByDepartment();
        Map<String, Long> deptCount = new HashMap<>();
        for (Map<String, Object> stat : deptStats) {
            deptCount.put((String) stat.get("stu_dept"), (Long) stat.get("count"));
        }
        statistics.put("departmentStats", deptCount);
        
        return statistics;
    }
    
    /**
     * 转换学生列表为Map列表
     */
    private List<Map<String, Object>> convertStudentsToMap(List<StudentMp> students) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (StudentMp student : students) {
            result.add(convertStudentToMap(student));
        }
        return result;
    }
    
    /**
     * 转换单个学生实体为Map
     */
    @Override
    public Map<String, Object> convertStudentToMap(StudentMp student) {
        Map<String, Object> studentMap = new HashMap<>();
        studentMap.put("userId", student.getUserId());
        studentMap.put("studentId", student.getSno());
        studentMap.put("name", student.getSname());
        studentMap.put("gender", student.getSsex() == 1 ? "男" : "女");
        studentMap.put("age", student.getSage());
        studentMap.put("cardNo", student.getStuCardNo());
        studentMap.put("address", student.getStuAddress());
        studentMap.put("hobby", student.getShbt());
        studentMap.put("bloodType", student.getSbloodType());
        studentMap.put("zodiac", student.getSstartSign());
        studentMap.put("evaluation", student.getSevaledType());
        studentMap.put("department", student.getStuDept());
        studentMap.put("major", student.getStuMajor());
        studentMap.put("clazz", student.getStuClazz());
        studentMap.put("region", student.getRegion());
        studentMap.put("createdAt", student.getCreatedAt());
        return studentMap;
    }
}