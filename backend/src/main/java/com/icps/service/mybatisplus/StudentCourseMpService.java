package com.icps.service.mybatisplus;

import com.icps.entity.mybatisplus.StudentCourseMp;
import com.icps.mapper.StudentCourseMpMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生选课服务 - MyBatisPlus版本
 */
@Slf4j
@Service
public class StudentCourseMpService {
    
    @Autowired
    private StudentCourseMpMapper studentCourseMpMapper;
    
    /**
     * 获取所有选课记录
     */
    public List<Map<String, Object>> getAllStudentCourses() {
        List<StudentCourseMp> studentCourses = studentCourseMpMapper.selectList(null);
        return convertStudentCoursesToMap(studentCourses);
    }
    
    /**
     * 根据学生身份证号查询选课记录
     */
    public List<Map<String, Object>> getCoursesByStudent(String stuCardNo) {
        List<StudentCourseMp> studentCourses = studentCourseMpMapper.selectByStuCardNo(stuCardNo);
        return convertStudentCoursesToMap(studentCourses);
    }
    
    /**
     * 根据课程ID查询选课记录
     */
    public List<Map<String, Object>> getStudentsByCourse(Long courseId) {
        List<StudentCourseMp> studentCourses = studentCourseMpMapper.selectByCourseId(courseId);
        return convertStudentCoursesToMap(studentCourses);
    }
    
    /**
     * 添加选课记录
     */
    public Map<String, Object> addStudentCourse(StudentCourseMp studentCourse) {
        try {
            // 检查是否已经选过该课程
            boolean exists = studentCourseMpMapper.existsByStuAndCourse(
                studentCourse.getStuCardNo(), studentCourse.getCourseId());
            
            if (exists) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "该学生已选择此课程");
                return response;
            }
            
            int result = studentCourseMpMapper.insert(studentCourse);
            
            Map<String, Object> response = new HashMap<>();
            if (result > 0) {
                response.put("success", true);
                response.put("message", "选课成功");
            } else {
                response.put("success", false);
                response.put("message", "选课失败");
            }
            return response;
        } catch (Exception e) {
            log.error("添加选课记录失败: {}", e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "选课失败: " + e.getMessage());
            return response;
        }
    }
    
    /**
     * 更新成绩
     */
    public Map<String, Object> updateGrade(Long id, Double grade, Double gradePoint) {
        StudentCourseMp studentCourse = studentCourseMpMapper.selectById(id);
        if (studentCourse != null) {
            studentCourse.setGrade(grade);
            studentCourse.setGradePoint(gradePoint);
            
            int result = studentCourseMpMapper.updateById(studentCourse);
            
            Map<String, Object> response = new HashMap<>();
            if (result > 0) {
                response.put("success", true);
                response.put("message", "成绩更新成功");
            } else {
                response.put("success", false);
                response.put("message", "成绩更新失败");
            }
            return response;
        } else {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "选课记录不存在");
            return result;
        }
    }
    
    /**
     * 删除选课记录
     */
    public Map<String, Object> deleteStudentCourse(Long id) {
        try {
            int result = studentCourseMpMapper.deleteById(id);
            
            Map<String, Object> response = new HashMap<>();
            if (result > 0) {
                response.put("success", true);
                response.put("message", "选课记录删除成功");
            } else {
                response.put("success", false);
                response.put("message", "选课记录不存在或删除失败");
            }
            return response;
        } catch (Exception e) {
            log.error("删除选课记录失败: {}", e.getMessage());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "选课记录删除失败: " + e.getMessage());
            return response;
        }
    }
    
    /**
     * 获取选课统计信息
     */
    public Map<String, Object> getStudentCourseStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总选课记录数
        long totalRecords = studentCourseMpMapper.selectCount(null);
        statistics.put("totalRecords", totalRecords);
        
        // 按学生统计选课数量
        List<Map<String, Object>> studentStats = studentCourseMpMapper.countCoursesByStudent();
        Map<String, Long> studentCourseCount = new HashMap<>();
        for (Map<String, Object> stat : studentStats) {
            studentCourseCount.put((String) stat.get("stu_card_no"), (Long) stat.get("count"));
        }
        statistics.put("studentCourseStats", studentCourseCount);
        
        // 按课程统计选课人数
        List<Map<String, Object>> courseStats = studentCourseMpMapper.countStudentsByCourse();
        Map<String, Long> courseStudentCount = new HashMap<>();
        for (Map<String, Object> stat : courseStats) {
            courseStudentCount.put(stat.get("course_id").toString(), (Long) stat.get("count"));
        }
        statistics.put("courseStudentStats", courseStudentCount);
        
        // 按学年统计
        List<Map<String, Object>> yearStats = studentCourseMpMapper.countByAcademicYear();
        Map<String, Long> yearCount = new HashMap<>();
        for (Map<String, Object> stat : yearStats) {
            yearCount.put((String) stat.get("academic_year"), (Long) stat.get("count"));
        }
        statistics.put("academicYearStats", yearCount);
        
        // 学生成绩统计
        List<Map<String, Object>> gradeStats = studentCourseMpMapper.getStudentGradeStats();
        List<Map<String, Object>> gradeList = new ArrayList<>();
        for (Map<String, Object> stat : gradeStats) {
            Map<String, Object> gradeMap = new HashMap<>();
            gradeMap.put("stuCardNo", stat.get("stu_card_no"));
            gradeMap.put("avgGrade", stat.get("avg_grade"));
            gradeMap.put("avgGradePoint", stat.get("avg_grade_point"));
            gradeMap.put("courseCount", stat.get("course_count"));
            gradeList.add(gradeMap);
        }
        statistics.put("gradeStats", gradeList);
        
        return statistics;
    }
    
    /**
     * 转换选课记录列表为Map列表
     */
    private List<Map<String, Object>> convertStudentCoursesToMap(List<StudentCourseMp> studentCourses) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (StudentCourseMp studentCourse : studentCourses) {
            result.add(convertStudentCourseToMap(studentCourse));
        }
        return result;
    }
    
    /**
     * 转换单个选课记录实体为Map
     */
    private Map<String, Object> convertStudentCourseToMap(StudentCourseMp studentCourse) {
        Map<String, Object> scMap = new HashMap<>();
        scMap.put("id", studentCourse.getId());
        scMap.put("stuCardNo", studentCourse.getStuCardNo());
        scMap.put("courseId", studentCourse.getCourseId());
        scMap.put("grade", studentCourse.getGrade());
        scMap.put("gradePoint", studentCourse.getGradePoint());
        scMap.put("academicYear", studentCourse.getAcademicYear());
        scMap.put("semester", studentCourse.getSemester());
        scMap.put("status", studentCourse.getStatus());
        scMap.put("createdAt", studentCourse.getCreatedAt());
        scMap.put("updatedAt", studentCourse.getUpdatedAt());
        return scMap;
    }
}