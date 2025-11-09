package com.icps.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.icps.entity.mybatisplus.TeacherMp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 教师Mapper接口 - MyBatisPlus版本
 */
@Mapper
public interface TeacherMpMapper extends BaseMapper<TeacherMp> {
    
    /**
     * 根据身份证号查找教师
     */
    @Select("SELECT * FROM icps_teacher WHERE teacher_card_no = #{cardNo}")
    TeacherMp selectByCardNo(@Param("cardNo") String cardNo);
    
    /**
     * 根据姓名模糊查询
     */
    @Select("SELECT * FROM icps_teacher WHERE teacher_name LIKE CONCAT('%', #{name}, '%')")
    List<TeacherMp> selectByNameLike(@Param("name") String name);
    
    /**
     * 根据学院查询
     */
    @Select("SELECT * FROM icps_teacher WHERE dept = #{dept}")
    List<TeacherMp> selectByDept(@Param("dept") String dept);
    
    /**
     * 根据职称查询
     */
    @Select("SELECT * FROM icps_teacher WHERE title = #{title}")
    List<TeacherMp> selectByTitle(@Param("title") String title);
    
    /**
     * 根据状态查询教师
     */
    @Select("SELECT * FROM icps_teacher WHERE status = #{status}")
    List<TeacherMp> selectByStatus(@Param("status") Integer status);
    
    /**
     * 统计教师总数
     */
    @Select("SELECT COUNT(*) FROM icps_teacher")
    long countTeachers();
    
    /**
     * 按学院统计教师数量
     */
    @Select("SELECT dept, COUNT(*) as count FROM icps_teacher GROUP BY dept")
    List<Map<String, Object>> countByDepartment();
    
    /**
     * 按职称统计教师数量
     */
    @Select("SELECT title, COUNT(*) as count FROM icps_teacher GROUP BY title")
    List<Map<String, Object>> countByTitle();
    
    /**
     * 分页查询教师列表
     */
    @Select("SELECT * FROM icps_teacher WHERE 1=1")
    IPage<TeacherMp> selectPageByCondition(Page<TeacherMp> page, 
                                           @Param("name") String name, 
                                           @Param("dept") String dept,
                                           @Param("title") String title,
                                           @Param("status") Integer status);
}