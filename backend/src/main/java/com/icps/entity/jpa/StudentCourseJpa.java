package com.icps.entity.jpa;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 学生选课关联实体类 - JPA版本
 */
@Entity
@Table(name = "icps_student_course")
public class StudentCourseJpa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "stu_card_no", length = 50)
    private String stuCardNo;
    
    @Column(name = "course_id")
    private Long courseId;
    
    @Column(name = "grade", precision = 4, scale = 1)
    private Double grade;
    
    @Column(name = "grade_point", precision = 3, scale = 2)
    private Double gradePoint;
    
    @Column(name = "academic_year", length = 20)
    private String academicYear;
    
    @Column(name = "semester", length = 20)
    private String semester;
    
    @Column(name = "status")
    private Integer status = 1;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 构造函数
    public StudentCourseJpa() {}
    
    public StudentCourseJpa(String stuCardNo, Long courseId, String academicYear, String semester) {
        this.stuCardNo = stuCardNo;
        this.courseId = courseId;
        this.academicYear = academicYear;
        this.semester = semester;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getStuCardNo() {
        return stuCardNo;
    }
    
    public void setStuCardNo(String stuCardNo) {
        this.stuCardNo = stuCardNo;
    }
    
    public Long getCourseId() {
        return courseId;
    }
    
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
    
    public Double getGrade() {
        return grade;
    }
    
    public void setGrade(Double grade) {
        this.grade = grade;
    }
    
    public Double getGradePoint() {
        return gradePoint;
    }
    
    public void setGradePoint(Double gradePoint) {
        this.gradePoint = gradePoint;
    }
    
    public String getAcademicYear() {
        return academicYear;
    }
    
    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }
    
    public String getSemester() {
        return semester;
    }
    
    public void setSemester(String semester) {
        this.semester = semester;
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
        return "StudentCourseJpa{" +
                "id=" + id +
                ", stuCardNo='" + stuCardNo + '\'' +
                ", courseId=" + courseId +
                ", grade=" + grade +
                ", gradePoint=" + gradePoint +
                ", academicYear='" + academicYear + '\'' +
                ", semester='" + semester + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}