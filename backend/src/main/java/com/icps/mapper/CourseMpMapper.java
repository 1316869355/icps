package com.icps.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.icps.entity.CourseMp;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 课程Mapper接口 - MyBatisPlus版本
 */
@Mapper
public interface CourseMpMapper extends BaseMapper<CourseMp> {
    
    /**
     * 根据课程代码查找课程
     */
    @Select("SELECT * FROM icps_course WHERE course_code = #{courseCode} AND deleted = 0")
    CourseMp selectByCourseCode(@Param("courseCode") String courseCode);
    
    /**
     * 根据课程名称模糊查询
     */
    @Select("SELECT * FROM icps_course WHERE course_name LIKE CONCAT('%', #{name}, '%') AND deleted = 0")
    List<CourseMp> selectByNameLike(@Param("name") String name);
    
    /**
     * 根据教师ID查询课程
     */
    @Select("SELECT * FROM icps_course WHERE teacher_id = #{teacherId} AND deleted = 0")
    List<CourseMp> selectByTeacherId(@Param("teacherId") Long teacherId);
    
    /**
     * 根据学期查询课程
     */
    @Select("SELECT * FROM icps_course WHERE semester = #{semester} AND deleted = 0")
    List<CourseMp> selectBySemester(@Param("semester") String semester);
    
    /**
     * 根据学年查询课程
     */
    @Select("SELECT * FROM icps_course WHERE academic_year = #{academicYear} AND deleted = 0")
    List<CourseMp> selectByAcademicYear(@Param("academicYear") String academicYear);
    
    /**
     * 根据状态查询课程
     */
    @Select("SELECT * FROM icps_course WHERE status = #{status} AND deleted = 0")
    List<CourseMp> selectByStatus(@Param("status") Integer status);
    
    /**
     * 统计课程总数
     */
    @Select("SELECT COUNT(*) FROM icps_course WHERE deleted = 0")
    long countCourses();
    
    /**
     * 按学期统计课程数量
     */
    @Select("SELECT semester, COUNT(*) as count FROM icps_course WHERE deleted = 0 GROUP BY semester")
    List<Map<String, Object>> countBySemester();
    
    /**
     * 按教师统计课程数量
     */
    @Select("SELECT teacher_name, COUNT(*) as count FROM icps_course WHERE deleted = 0 GROUP BY teacher_name")
    List<Map<String, Object>> countByTeacher();
    
    /**
     * 分页查询课程列表
     */
    @Select("SELECT * FROM icps_course WHERE deleted = 0")
    IPage<CourseMp> selectPageByCondition(Page<CourseMp> page, 
                                          @Param("name") String name, 
                                          @Param("teacherName") String teacherName,
                                          @Param("semester") String semester,
                                          @Param("status") Integer status);
}