package com.icps.entity;

import java.io.Serializable;

public class Student implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String sno;           // 学号
    private String sname;          // 姓名
    private Integer ssex;          // 性别 1:男 2:女
    private Integer sage;          // 年龄
    private String stuCardNum;     // 身份证号
    private String stuAddress;     // 地址
    private String shbt;           // 课余活动
    private String sbloodType;     // 血型
    private String sstartSign;     // 星座
    private String sevaledType;    // 评估结果
    private String stuDept;        // 学院
    private String stuMajor;      // 专业
    private String stuClazz;       // 班级
    private String region;         // 地区

    // 构造函数
    public Student() {}

    public Student(String sno, String sname, String stuCardNum, String shbt, 
                   String sbloodType, String sstartSign, String sevaledType) {
        this.sno = sno;
        this.sname = sname;
        this.stuCardNum = stuCardNum;
        this.shbt = shbt;
        this.sbloodType = sbloodType;
        this.sstartSign = sstartSign;
        this.sevaledType = sevaledType;
    }

    public Student(String sno, String sname, Integer ssex, Integer sage, String stuCardNum, 
                   String stuAddress, String shbt, String sbloodType, String sstartSign, 
                   String sevaledType, String stuDept, String stuMajor, String stuClazz, String region) {
        this.sno = sno;
        this.sname = sname;
        this.ssex = ssex;
        this.sage = sage;
        this.stuCardNum = stuCardNum;
        this.stuAddress = stuAddress;
        this.shbt = shbt;
        this.sbloodType = sbloodType;
        this.sstartSign = sstartSign;
        this.sevaledType = sevaledType;
        this.stuDept = stuDept;
        this.stuMajor = stuMajor;
        this.stuClazz = stuClazz;
        this.region = region;
    }

    // Getter和Setter方法
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

    public String getStuCardNum() {
        return stuCardNum;
    }

    public void setStuCardNum(String stuCardNum) {
        this.stuCardNum = stuCardNum;
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

    @Override
    public String toString() {
        return "Student{" +
                "sno='" + sno + '\'' +
                ", sname='" + sname + '\'' +
                ", ssex=" + ssex +
                ", sage=" + sage +
                ", stuCardNum='" + stuCardNum + '\'' +
                ", stuAddress='" + stuAddress + '\'' +
                ", shbt='" + shbt + '\'' +
                ", sbloodType='" + sbloodType + '\'' +
                ", sstartSign='" + sstartSign + '\'' +
                ", sevaledType='" + sevaledType + '\'' +
                ", stuDept='" + stuDept + '\'' +
                ", stuMajor='" + stuMajor + '\'' +
                ", stuClazz='" + stuClazz + '\'' +
                ", region='" + region + '\'' +
                '}';
    }
}