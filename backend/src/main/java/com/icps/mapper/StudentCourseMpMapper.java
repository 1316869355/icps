package com.icps.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.icps.entity.StudentCourseMp;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 学生选课Mapper接口 - MyBatisPlus版本
 */
@Mapper
public interface StudentCourseMpMapper extends BaseMapper<StudentCourseMp> {
    
    /**
     * 根据学生身份证号查询选课记录
     */
    @Select("SELECT * FROM icps_student_course WHERE stu_card_no = #{stuCardNo} AND deleted = 0")
    List<StudentCourseMp> selectByStuCardNo(@Param("stuCardNo") String stuCardNo);
    
    /**
     * 根据课程ID查询选课记录
     */
    @Select("SELECT * FROM icps_student_course WHERE course_id = #{courseId} AND deleted = 0")
    List<StudentCourseMp> selectByCourseId(@Param("courseId") Long courseId);
    
    /**
     * 根据学年学期查询选课记录
     */
    @Select("SELECT * FROM icps_student_course WHERE academic_year = #{academicYear} AND semester = #{semester} AND deleted = 0")
    List<StudentCourseMp> selectByAcademicYearAndSemester(@Param("academicYear") String academicYear, 
                                                         @Param("semester") String semester);
    
    /**
     * 查询学生是否已选择某课程
     */
    @Select("SELECT COUNT(*) > 0 FROM icps_student_course WHERE stu_card_no = #{stuCardNo} AND course_id = #{courseId} AND deleted = 0")
    boolean existsByStuAndCourse(@Param("stuCardNo") String stuCardNo, @Param("courseId") Long courseId);
    
    /**
     * 查询学生某条选课记录（用于退课/改成绩）
     */
    @Select("SELECT * FROM icps_student_course WHERE stu_card_no = #{stuCardNo} AND course_id = #{courseId} AND deleted = 0 LIMIT 1")
    StudentCourseMp selectByStuAndCourse(@Param("stuCardNo") String stuCardNo, @Param("courseId") Long courseId);
    
    /**
     * 统计学生已选课程数量
     */
    @Select("SELECT stu_card_no, COUNT(*) as count FROM icps_student_course WHERE deleted = 0 GROUP BY stu_card_no")
    List<Map<String, Object>> countCoursesByStudent();
    
    /**
     * 统计课程已选人数
     */
    @Select("SELECT course_id, COUNT(*) as count FROM icps_student_course WHERE deleted = 0 GROUP BY course_id")
    List<Map<String, Object>> countStudentsByCourse();
    
    /**
     * 按学年统计选课人数
     */
    @Select("SELECT academic_year, COUNT(*) as count FROM icps_student_course WHERE deleted = 0 GROUP BY academic_year")
    List<Map<String, Object>> countByAcademicYear();
    
    /**
     * 查询学生成绩统计
     */
    @Select("SELECT stu_card_no, AVG(grade) as avg_grade, AVG(grade_point) as avg_grade_point, COUNT(*) as course_count FROM icps_student_course WHERE deleted = 0 GROUP BY stu_card_no")
    List<Map<String, Object>> getStudentGradeStats();

    /**
     * 全局成绩概览：选课总人次、已录入成绩人次、平均分、平均绩点
     */
    @Select("SELECT COUNT(*) AS record_count, COUNT(grade) AS graded_count, " +
            "AVG(grade) AS avg_grade, AVG(grade_point) AS avg_grade_point " +
            "FROM icps_student_course WHERE deleted = 0")
    Map<String, Object> selectGradeOverview();

    /**
     * 查询学生成绩明细（关联课程信息）
     */
    @Select("SELECT sc.id, sc.course_id, sc.grade, sc.grade_point, sc.academic_year, sc.semester, sc.status, " +
            "c.course_code, c.course_name, c.credit, c.hours, c.teacher_name, c.classroom, c.schedule, " +
            "c.capacity, c.enrolled, c.status AS course_status " +
            "FROM icps_student_course sc " +
            "LEFT JOIN icps_course c ON sc.course_id = c.course_id AND c.deleted = 0 " +
            "WHERE sc.stu_card_no = #{stuCardNo} AND sc.deleted = 0 " +
            "ORDER BY sc.academic_year DESC, sc.semester DESC, c.course_code ASC")
    List<Map<String, Object>> selectGradeDetailsByStuCardNo(@Param("stuCardNo") String stuCardNo);

    /**
     * 查询某门课程的成绩明细（关联学生信息）
     */
    @Select("SELECT sc.id, sc.stu_card_no, sc.grade, sc.grade_point, sc.academic_year, sc.semester, " +
            "s.sno, s.sname, s.stu_clazz " +
            "FROM icps_student_course sc " +
            "LEFT JOIN icps_stu s ON sc.stu_card_no = s.stu_card_no AND s.deleted = 0 " +
            "WHERE sc.course_id = #{courseId} AND sc.deleted = 0 " +
            "ORDER BY s.sno ASC")
    List<Map<String, Object>> selectGradeDetailsByCourseId(@Param("courseId") Long courseId);
}