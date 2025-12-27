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
    @Select("SELECT * FROM icps_student_course WHERE stu_card_no = #{stuCardNo}")
    List<StudentCourseMp> selectByStuCardNo(@Param("stuCardNo") String stuCardNo);
    
    /**
     * 根据课程ID查询选课记录
     */
    @Select("SELECT * FROM icps_student_course WHERE course_id = #{courseId}")
    List<StudentCourseMp> selectByCourseId(@Param("courseId") Long courseId);
    
    /**
     * 根据学年学期查询选课记录
     */
    @Select("SELECT * FROM icps_student_course WHERE academic_year = #{academicYear} AND semester = #{semester}")
    List<StudentCourseMp> selectByAcademicYearAndSemester(@Param("academicYear") String academicYear, 
                                                         @Param("semester") String semester);
    
    /**
     * 查询学生是否已选择某课程
     */
    @Select("SELECT COUNT(*) > 0 FROM icps_student_course WHERE stu_card_no = #{stuCardNo} AND course_id = #{courseId}")
    boolean existsByStuAndCourse(@Param("stuCardNo") String stuCardNo, @Param("courseId") Long courseId);
    
    /**
     * 统计学生已选课程数量
     */
    @Select("SELECT stu_card_no, COUNT(*) as count FROM icps_student_course GROUP BY stu_card_no")
    List<Map<String, Object>> countCoursesByStudent();
    
    /**
     * 统计课程已选人数
     */
    @Select("SELECT course_id, COUNT(*) as count FROM icps_student_course GROUP BY course_id")
    List<Map<String, Object>> countStudentsByCourse();
    
    /**
     * 按学年统计选课人数
     */
    @Select("SELECT academic_year, COUNT(*) as count FROM icps_student_course GROUP BY academic_year")
    List<Map<String, Object>> countByAcademicYear();
    
    /**
     * 查询学生成绩统计
     */
    @Select("SELECT stu_card_no, AVG(grade) as avg_grade, AVG(grade_point) as avg_grade_point, COUNT(*) as course_count FROM icps_student_course GROUP BY stu_card_no")
    List<Map<String, Object>> getStudentGradeStats();
}