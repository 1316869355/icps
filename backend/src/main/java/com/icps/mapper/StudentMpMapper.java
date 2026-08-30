package com.icps.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.icps.entity.StudentMp;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 学生Mapper接口 - MyBatisPlus版本
 */
@Mapper
public interface StudentMpMapper extends BaseMapper<StudentMp> {

    /**
     * 根据用户id查询
     */
    @Select("SELECT * FROM icps_stu WHERE user_id = #{userId} AND deleted = 0")
    StudentMp selectByUserId(@Param("userId") Long userId);
    
    /**
     * 根据学号查找学生
     */
    @Select("SELECT * FROM icps_stu WHERE sno = #{sno} AND deleted = 0")
    StudentMp selectBySno(@Param("sno") String sno);
    
    /**
     * 根据姓名模糊查询
     */
    @Select("SELECT * FROM icps_stu WHERE sname LIKE CONCAT('%', #{name}, '%') AND deleted = 0")
    List<StudentMp> selectBySnameLike(@Param("name") String name);
    
    /**
     * 根据学院查询
     */
    @Select("SELECT * FROM icps_stu WHERE stu_dept = #{dept} AND deleted = 0")
    List<StudentMp> selectByDept(@Param("dept") String dept);
    
    /**
     * 根据专业查询
     */
    @Select("SELECT * FROM icps_stu WHERE stu_major = #{major} AND deleted = 0")
    List<StudentMp> selectByMajor(@Param("major") String major);
    
    /**
     * 根据班级查询
     */
    @Select("SELECT * FROM icps_stu WHERE stu_clazz = #{clazz} AND deleted = 0")
    List<StudentMp> selectByClazz(@Param("clazz") String clazz);
    
    /**
     * 根据性别查询
     */
    @Select("SELECT * FROM icps_stu WHERE ssex = #{sex} AND deleted = 0")
    List<StudentMp> selectBySex(@Param("sex") Integer sex);
    
    /**
     * 学生登录验证
     */
    @Select("SELECT COUNT(*) > 0 FROM icps_stu WHERE stu_card_no = #{cardNo} AND sname LIKE CONCAT('%', #{name}, '%') AND deleted = 0")
    boolean existsByCardNoAndName(@Param("cardNo") String cardNo, @Param("name") String name);
    
    /**
     * 统计学生总数
     */
    @Select("SELECT COUNT(*) FROM icps_stu WHERE deleted = 0")
    long countStudents();
    
    /**
     * 按学院统计学生数量
     */
    @Select("SELECT stu_dept, COUNT(*) as count FROM icps_stu WHERE deleted = 0 GROUP BY stu_dept")
    List<Map<String, Object>> countByDepartment();
    
    /**
     * 分页查询学生列表
     */
    @Select("SELECT * FROM icps_stu WHERE deleted = 0 ORDER BY created_at DESC")
    IPage<StudentMp> selectPageByCondition(Page<StudentMp> page, 
                                           @Param("name") String name, 
                                           @Param("dept") String dept,
                                           @Param("major") String major,
                                           @Param("sex") Integer sex);
}