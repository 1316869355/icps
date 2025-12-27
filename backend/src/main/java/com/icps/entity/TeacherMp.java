package com.icps.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 教师实体类 - MyBatisPlus版本
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("icps_teacher")
public class TeacherMp {
    
    /**
     * 教师ID（主键）
     */
    @TableId(value = "teacher_id", type = IdType.AUTO)
    private Long teacherId;
    
    /**
     * 身份证号
     */
    @TableField("teacher_card_no")
    private String teacherCardNo;
    
    /**
     * 姓名
     */
    @TableField("teacher_name")
    private String teacherName;
    
    /**
     * 学院
     */
    @TableField("dept")
    private String dept;
    
    /**
     * 职称
     */
    @TableField("title")
    private String title;
    
    /**
     * 邮箱
     */
    @TableField("email")
    private String email;
    
    /**
     * 电话
     */
    @TableField("phone")
    private String phone;
    
    /**
     * 状态 1:在职 0:离职
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