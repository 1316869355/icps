package com.icps.entity.jpa;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 学生实体类 - JPA版本
 */
@Entity
@Table(name = "icps_stu")
public class StudentJpa {
    
    @Id
    @Column(name = "stu_card_no", length = 50)
    private String stuCardNo;           // 身份证号（主键）
    
    @Column(name = "sno", length = 20)
    private String sno;                 // 学号
    
    @Column(name = "sname", length = 50)
    private String sname;               // 姓名
    
    @Column(name = "ssex")
    private Integer ssex;               // 性别 1:男 2:女
    
    @Column(name = "sage")
    private Integer sage;               // 年龄
    
    @Column(name = "stu_address", length = 200)
    private String stuAddress;          // 地址
    
    @Column(name = "shbt", length = 100)
    private String shbt;                // 课余活动
    
    @Column(name = "sblood", length = 10)
    private String sbloodType;          // 血型
    
    @Column(name = "start_sign", length = 10)
    private String sstartSign;          // 星座
    
    @Column(name = "evaluated_type", length = 20)
    private String sevaledType;        // 评估结果
    
    @Column(name = "stu_dept", length = 50)
    private String stuDept;             // 学院
    
    @Column(name = "stu_major", length = 50)
    private String stuMajor;            // 专业
    
    @Column(name = "stu_clazz", length = 50)
    private String stuClazz;            // 班级
    
    @Column(name = "region", length = 50)
    private String region;               // 地区
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;    // 创建时间
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;    // 更新时间
    
    // 构造函数
    public StudentJpa() {}
    
    public StudentJpa(String stuCardNo, String sno, String sname) {
        this.stuCardNo = stuCardNo;
        this.sno = sno;
        this.sname = sname;
    }
    
    // Getter和Setter方法
    public String getStuCardNo() {
        return stuCardNo;
    }
    
    public void setStuCardNo(String stuCardNo) {
        this.stuCardNo = stuCardNo;
    }
    
    public String getSno() {
        return sno;
    }
    
    public void setSno(String sno) {
        this.sno = sno;
    }
    
    public String getSname() {
        return sname;
    }
    
    public void setSname(String sname) {
        this.sname = sname;
    }
    
    public Integer getSsex() {
        return ssex;
    }
    
    public void setSsex(Integer ssex) {
        this.ssex = ssex;
    }
    
    public Integer getSage() {
        return sage;
    }
    
    public void setSage(Integer sage) {
        this.sage = sage;
    }
    
    public String getStuAddress() {
        return stuAddress;
    }
    
    public void setStuAddress(String stuAddress) {
        this.stuAddress = stuAddress;
    }
    
    public String getShbt() {
        return shbt;
    }
    
    public void setShbt(String shbt) {
        this.shbt = shbt;
    }
    
    public String getSbloodType() {
        return sbloodType;
    }
    
    public void setSbloodType(String sbloodType) {
        this.sbloodType = sbloodType;
    }
    
    public String getSstartSign() {
        return sstartSign;
    }
    
    public void setSstartSign(String sstartSign) {
        this.sstartSign = sstartSign;
    }
    
    public String getSevaledType() {
        return sevaledType;
    }
    
    public void setSevaledType(String sevaledType) {
        this.sevaledType = sevaledType;
    }
    
    public String getStuDept() {
        return stuDept;
    }
    
    public void setStuDept(String stuDept) {
        this.stuDept = stuDept;
    }
    
    public String getStuMajor() {
        return stuMajor;
    }
    
    public void setStuMajor(String stuMajor) {
        this.stuMajor = stuMajor;
    }
    
    public String getStuClazz() {
        return stuClazz;
    }
    
    public void setStuClazz(String stuClazz) {
        this.stuClazz = stuClazz;
    }
    
    public String getRegion() {
        return region;
    }
    
    public void setRegion(String region) {
        this.region = region;
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
        return "StudentJpa{" +
                "stuCardNo='" + stuCardNo + '\'' +
                ", sno='" + sno + '\'' +
                ", sname='" + sname + '\'' +
                ", ssex=" + ssex +
                ", sage=" + sage +
                ", stuAddress='" + stuAddress + '\'' +
                ", shbt='" + shbt + '\'' +
                ", sbloodType='" + sbloodType + '\'' +
                ", sstartSign='" + sstartSign + '\'' +
                ", sevaledType='" + sevaledType + '\'' +
                ", stuDept='" + stuDept + '\'' +
                ", stuMajor='" + stuMajor + '\'' +
                ", stuClazz='" + stuClazz + '\'' +
                ", region='" + region + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}