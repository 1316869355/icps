package com.icps.entity.mybatisplus;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 课程实体类 - MyBatisPlus版本
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("icps_course")
public class CourseMp {
    
    /**
     * 课程ID（主键）
     */
    @TableId(value = "course_id", type = IdType.AUTO)
    private Long courseId;
    
    /**
     * 课程代码
     */
    @TableField("course_code")
    private String courseCode;
    
    /**
     * 课程名称
     */
    @TableField("course_name")
    private String courseName;
    
    /**
     * 学分
     */
    @TableField("credit")
    private Integer credit;
    
    /**
     * 学时
     */
    @TableField("hours")
    private Integer hours;
    
    /**
     * 授课教师ID
     */
    @TableField("teacher_id")
    private Long teacherId;
    
    /**
     * 授课教师姓名
     */
    @TableField("teacher_name")
    private String teacherName;
    
    /**
     * 教室
     */
    @TableField("classroom")
    private String classroom;
    
    /**
     * 学期
     */
    @TableField("semester")
    private String semester;
    
    /**
     * 学年
     */
    @TableField("academic_year")
    private String academicYear;
    
    /**
     * 上课时间
     */
    @TableField("schedule")
    private String schedule;
    
    /**
     * 状态 1:正常 0:停开
     */
    @TableField("status")
    private Integer status = 1;
    
    /**
     * 容量
     */
    @TableField("capacity")
    private Integer capacity;
    
    /**
     * 已选人数
     */
    @TableField("enrolled")
    private Integer enrolled = 0;
    
    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}