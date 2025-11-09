package com.icps.entity;

import java.io.Serializable;

public class Teacher implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String teacherId;     // 教师ID
    private String teacherName;    // 教师姓名
    private String teacherCardNo;  // 身份证号
    private String dept;           // 所属院系
    private String title;          // 职称

    public Teacher() {}

    public Teacher(String teacherId, String teacherName, String teacherCardNo, String dept, String title) {
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.teacherCardNo = teacherCardNo;
        this.dept = dept;
        this.title = title;
    }

    // Getter和Setter方法
    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherCardNo() {
        return teacherCardNo;
    }

    public void setTeacherCardNo(String teacherCardNo) {
        this.teacherCardNo = teacherCardNo;
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

    @Override
    public String toString() {
        return "Teacher{" +
                "teacherId='" + teacherId + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", teacherCardNo='" + teacherCardNo + '\'' +
                ", dept='" + dept + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}