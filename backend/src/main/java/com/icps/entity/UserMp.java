package com.icps.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 用户认证实体类 - MyBatisPlus版本
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("icps_user")
public class UserMp {
    
    /**
     * 用户ID（主键）
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;
    
    /**
     * 用户名
     */
    @TableField("username")
    private String username;
    
    /**
     * 密码
     */
    @TableField("password")
    private String password;
    
    /**
     * 角色 student, teacher, admin
     */
    @TableField("role")
    private String role = "student";
    
    /**
     * 学生身份证号
     */
    @TableField("stu_card_no")
    private String stuCardNo;
    
    /**
     * 教师ID
     */
    @TableField("teacher_id")
    private Long teacherId;
    
    /**
     * 状态
     */
    @TableField("status")
    private Integer status = 1;
    
    /**
     * 最后登录时间
     */
    @TableField("last_login")
    private LocalDateTime lastLogin;
    
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