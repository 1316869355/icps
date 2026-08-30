package com.icps.service.impl;

import com.icps.entity.CourseMp;
import com.icps.entity.StudentCourseMp;
import com.icps.mapper.CourseMpMapper;
import com.icps.mapper.StudentCourseMpMapper;
import com.icps.service.StudentCourseMpService;

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
public class StudentCourseMpServiceImpl implements StudentCourseMpService {
    
    @Autowired
    private StudentCourseMpMapper studentCourseMpMapper;

    @Autowired
    private CourseMpMapper courseMpMapper;
    
    /**
     * 获取所有选课记录
     */
    @Override
    public List<Map<String, Object>> getAllStudentCourses() {
        List<StudentCourseMp> studentCourses = studentCourseMpMapper.selectList(null);
        return convertStudentCoursesToMap(studentCourses);
    }
    
    /**
     * 根据学生身份证号查询选课记录
     */
    @Override
    public List<Map<String, Object>> getCoursesByStudent(String stuCardNo) {
        List<StudentCourseMp> studentCourses = studentCourseMpMapper.selectByStuCardNo(stuCardNo);
        return convertStudentCoursesToMap(studentCourses);
    }
    
    /**
     * 根据课程ID查询选课记录
     */
    @Override
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
    @Override
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
    @Override
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
    @Override
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
     * 选课：写入选课记录并同步课程已选人数
     */
    @Override
    public Map<String, Object> selectCourse(String stuCardNo, Long courseId) {
        Map<String, Object> response = new HashMap<>();
        if (stuCardNo == null || courseId == null) {
            response.put("success", false);
            response.put("message", "学生标识或课程ID不能为空");
            return response;
        }

        CourseMp course = courseMpMapper.selectById(courseId);
        if (course == null) {
            response.put("success", false);
            response.put("message", "课程不存在");
            return response;
        }
        if (course.getStatus() != null && course.getStatus() != 1) {
            response.put("success", false);
            response.put("message", "课程已停开");
            return response;
        }
        if (studentCourseMpMapper.existsByStuAndCourse(stuCardNo, courseId)) {
            response.put("success", false);
            response.put("message", "该学生已选择此课程");
            return response;
        }
        if (course.getCapacity() != null && course.getCapacity() > 0
                && course.getEnrolled() != null && course.getEnrolled() >= course.getCapacity()) {
            response.put("success", false);
            response.put("message", "课程容量已满");
            return response;
        }

        try {
            StudentCourseMp record = new StudentCourseMp();
            record.setStuCardNo(stuCardNo);
            record.setCourseId(courseId);
            record.setAcademicYear(course.getAcademicYear());
            record.setSemester(course.getSemester());
            record.setStatus(1);

            int result = studentCourseMpMapper.insert(record);
            if (result <= 0) {
                response.put("success", false);
                response.put("message", "选课失败");
                return response;
            }

            adjustEnrolled(courseId, 1);
            response.put("success", true);
            response.put("message", "选课成功");
            response.put("id", record.getId());
            return response;
        } catch (Exception e) {
            log.error("选课失败: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "选课失败: " + e.getMessage());
            return response;
        }
    }

    /**
     * 退课：逻辑删除选课记录并同步课程已选人数
     */
    @Override
    public Map<String, Object> dropCourse(String stuCardNo, Long courseId) {
        Map<String, Object> response = new HashMap<>();
        if (stuCardNo == null || courseId == null) {
            response.put("success", false);
            response.put("message", "学生标识或课程ID不能为空");
            return response;
        }

        StudentCourseMp record = studentCourseMpMapper.selectByStuAndCourse(stuCardNo, courseId);
        if (record == null) {
            response.put("success", false);
            response.put("message", "未选择该课程");
            return response;
        }
        if (record.getGrade() != null) {
            response.put("success", false);
            response.put("message", "该课程已录入成绩，无法退课");
            return response;
        }

        try {
            int result = studentCourseMpMapper.deleteById(record.getId());
            if (result <= 0) {
                response.put("success", false);
                response.put("message", "退课失败");
                return response;
            }
            adjustEnrolled(courseId, -1);
            response.put("success", true);
            response.put("message", "退课成功");
            return response;
        } catch (Exception e) {
            log.error("退课失败: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "退课失败: " + e.getMessage());
            return response;
        }
    }

    /**
     * 按学生身份证号 + 课程ID 定位选课记录
     */
    @Override
    public StudentCourseMp findByStuAndCourse(String stuCardNo, Long courseId) {
        if (stuCardNo == null || courseId == null) {
            return null;
        }
        return studentCourseMpMapper.selectByStuAndCourse(stuCardNo, courseId);
    }

    /**
     * 查询学生成绩明细（关联课程信息）
     */
    @Override
    public List<Map<String, Object>> getGradeDetails(String stuCardNo) {
        return convertGradeDetails(studentCourseMpMapper.selectGradeDetailsByStuCardNo(stuCardNo));
    }

    /**
     * 全局成绩概览
     */
    @Override
    public Map<String, Object> getGradeOverview() {
        Map<String, Object> overview = studentCourseMpMapper.selectGradeOverview();
        return overview == null ? new HashMap<>() : overview;
    }

    /**
     * 查询某门课程的成绩明细（关联学生信息）
     */
    @Override
    public List<Map<String, Object>> getCourseGradeDetails(Long courseId) {
        return convertGradeDetails(studentCourseMpMapper.selectGradeDetailsByCourseId(courseId));
    }

    /**
     * 调整课程已选人数
     */
    private void adjustEnrolled(Long courseId, int delta) {
        CourseMp course = courseMpMapper.selectById(courseId);
        if (course == null) {
            return;
        }
        int enrolled = course.getEnrolled() == null ? 0 : course.getEnrolled();
        course.setEnrolled(Math.max(0, enrolled + delta));
        courseMpMapper.updateById(course);
    }

    /**
     * 选课/成绩明细的 snake_case 列名转换为驼峰
     */
    private List<Map<String, Object>> convertGradeDetails(List<Map<String, Object>> rows) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (rows == null) {
            return result;
        }
        for (Map<String, Object> row : rows) {
            Map<String, Object> item = new HashMap<>();
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                item.put(toCamelCase(entry.getKey()), entry.getValue());
            }
            result.add(item);
        }
        return result;
    }

    private static String toCamelCase(String column) {
        if (column == null || !column.contains("_")) {
            return column;
        }
        StringBuilder sb = new StringBuilder();
        boolean upperNext = false;
        for (char c : column.toCharArray()) {
            if (c == '_') {
                upperNext = true;
            } else if (upperNext) {
                sb.append(Character.toUpperCase(c));
                upperNext = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
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