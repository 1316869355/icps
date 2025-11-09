package com.icps.entity.jpa;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 教师实体类 - JPA版本
 */
@Entity
@Table(name = "icps_teacher")
public class TeacherJpa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teacher_id")
    private Long teacherId;             // 教师ID（主键）
    
    @Column(name = "teacher_card_no", length = 50, unique = true)
    private String teacherCardNo;       // 身份证号
    
    @Column(name = "teacher_name", length = 50)
    private String teacherName;         // 姓名
    
    @Column(name = "dept", length = 50)
    private String dept;                // 学院
    
    @Column(name = "title", length = 50)
    private String title;               // 职称
    
    @Column(name = "email", length = 100)
    private String email;               // 邮箱
    
    @Column(name = "phone", length = 20)
    private String phone;               // 电话
    
    @Column(name = "status")
    private Integer status = 1;         // 状态 1:在职 0:离职
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;    // 创建时间
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;    // 更新时间
    
    // 构造函数
    public TeacherJpa() {}
    
    public TeacherJpa(String teacherCardNo, String teacherName, String dept) {
        this.teacherCardNo = teacherCardNo;
        this.teacherName = teacherName;
        this.dept = dept;
    }
    
    // Getter和Setter方法
    public Long getTeacherId() {
        return teacherId;
    }
    
    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }
    
    public String getTeacherCardNo() {
        return teacherCardNo;
    }
    
    public void setTeacherCardNo(String teacherCardNo) {
        this.teacherCardNo = teacherCardNo;
    }
    
    public String getTeacherName() {
        return teacherName;
    }
    
    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
    
    public String getDept() {
        return dept;
    }
    
    public void setDept(String dept) {
        this.dept = dept;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "TeacherJpa{" +
                "teacherId=" + teacherId +
                ", teacherCardNo='" + teacherCardNo + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", dept='" + dept + '\'' +
                ", title='" + title + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}