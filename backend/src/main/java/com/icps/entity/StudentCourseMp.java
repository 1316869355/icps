package com.icps.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 学生选课关联实体类 - MyBatisPlus版本
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("icps_student_course")
public class StudentCourseMp {
    
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 学生身份证号
     */
    @TableField("stu_card_no")
    private String stuCardNo;
    
    /**
     * 课程ID
     */
    @TableField("course_id")
    private Long courseId;
    
    /**
     * 成绩
     */
    @TableField("grade")
    private Double grade;
    
    /**
     * 绩点
     */
    @TableField("grade_point")
    private Double gradePoint;
    
    /**
     * 学年
     */
    @TableField("academic_year")
    private String academicYear;
    
    /**
     * 学期
     */
    @TableField("semester")
    private String semester;
    
    /**
     * 状态
     */
    @TableField("status")
    private Integer status = 1;
    
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