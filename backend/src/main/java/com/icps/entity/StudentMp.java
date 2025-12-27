package com.icps.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 学生实体类 - MyBatisPlus版本
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("icps_stu")
public class StudentMp {
    
    /**
     * 学生ID（主键）与user表主键对应
     */
    @TableId(value = "user_id", type = IdType.ASSIGN_ID)
    private Long userId;

    /**
     * 身份证号（主键）
     */
    @TableField(value = "stu_card_no")
    private String stuCardNo;
    
    /**
     * 学号
     */
    @TableField("sno")
    private String sno;
    
    /**
     * 姓名
     */
    @TableField("sname")
    private String sname;
    
    /**
     * 性别 1:男 2:女
     */
    @TableField("ssex")
    private Integer ssex;
    
    /**
     * 年龄
     */
    @TableField("sage")
    private Integer sage;
    
    /**
     * 地址
     */
    @TableField("stu_address")
    private String stuAddress;
    
    /**
     * 课余活动
     */
    @TableField("shbt")
    private String shbt;
    
    /**
     * 血型
     */
    @TableField("sblood")
    private String sbloodType;
    
    /**
     * 星座
     */
    @TableField("start_sign")
    private String sstartSign;
    
    /**
     * 评估结果
     */
    @TableField("evaluated_type")
    private String sevaledType;
    
    /**
     * 学院
     */
    @TableField("stu_dept")
    private String stuDept;
    
    /**
     * 专业
     */
    @TableField("stu_major")
    private String stuMajor;
    
    /**
     * 班级
     */
    @TableField("stu_clazz")
    private String stuClazz;
    
    /**
     * 地区
     */
    @TableField("region")
    private String region;
    
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