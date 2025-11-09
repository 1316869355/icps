package com.icps.entity.jpa;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 课程实体类 - JPA版本
 */
@Entity
@Table(name = "icps_course")
public class CourseJpa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;              // 课程ID（主键）
    
    @Column(name = "course_code", length = 20, unique = true)
    private String courseCode;           // 课程代码
    
    @Column(name = "course_name", length = 100)
    private String courseName;           // 课程名称
    
    @Column(name = "credit")
    private Integer credit;              // 学分
    
    @Column(name = "hours")
    private Integer hours;               // 学时
    
    @Column(name = "teacher_id")
    private Long teacherId;              // 授课教师ID
    
    @Column(name = "teacher_name", length = 50)
    private String teacherName;          // 授课教师姓名
    
    @Column(name = "classroom", length = 50)
    private String classroom;            // 教室
    
    @Column(name = "semester", length = 20)
    private String semester;             // 学期
    
    @Column(name = "academic_year", length = 20)
    private String academicYear;         // 学年
    
    @Column(name = "schedule", length = 100)
    private String schedule;             // 上课时间
    
    @Column(name = "status")
    private Integer status = 1;          // 状态 1:正常 0:停开
    
    @Column(name = "capacity")
    private Integer capacity;            // 容量
    
    @Column(name = "enrolled")
    private Integer enrolled = 0;        // 已选人数
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;     // 创建时间
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;     // 更新时间
    
    // 构造函数
    public CourseJpa() {}
    
    public CourseJpa(String courseCode, String courseName, Integer credit) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credit = credit;
    }
    
    // Getter和Setter方法
    public Long getCourseId() {
        return courseId;
    }
    
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
    
    public String getCourseCode() {
        return courseCode;
    }
    
    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }
    
    public String getCourseName() {
        return courseName;
    }
    
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    
    public Integer getCredit() {
        return credit;
    }
    
    public void setCredit(Integer credit) {
        this.credit = credit;
    }
    
    public Integer getHours() {
        return hours;
    }
    
    public void setHours(Integer hours) {
        this.hours = hours;
    }
    
    public Long getTeacherId() {
        return teacherId;
    }
    
    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }
    
    public String getTeacherName() {
        return teacherName;
    }
    
    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
    
    public String getClassroom() {
        return classroom;
    }
    
    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }
    
    public String getSemester() {
        return semester;
    }
    
    public void setSemester(String semester) {
        this.semester = semester;
    }
    
    public String getAcademicYear() {
        return academicYear;
    }
    
    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }
    
    public String getSchedule() {
        return schedule;
    }
    
    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public Integer getCapacity() {
        return capacity;
    }
    
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
    
    public Integer getEnrolled() {
        return enrolled;
    }
    
    public void setEnrolled(Integer enrolled) {
        this.enrolled = enrolled;
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
        return "CourseJpa{" +
                "courseId=" + courseId +
                ", courseCode='" + courseCode + '\'' +
                ", courseName='" + courseName + '\'' +
                ", credit=" + credit +
                ", hours=" + hours +
                ", teacherId=" + teacherId +
                ", teacherName='" + teacherName + '\'' +
                ", classroom='" + classroom + '\'' +
                ", semester='" + semester + '\'' +
                ", academicYear='" + academicYear + '\'' +
                ", schedule='" + schedule + '\'' +
                ", status=" + status +
                ", capacity=" + capacity +
                ", enrolled=" + enrolled +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}