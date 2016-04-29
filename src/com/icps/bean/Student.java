package com.icps.bean;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class Student implements RowMapper<Student> , Serializable{
	private String sno;//学号
	private String sname;
	private int ssex;
	private int sage;
	private String stuCardNum;
	private String stuAdderss;
	private String shbt;
	private String sbloodType;
	private String sstartSign;
	private String sevaledType;
	
	private String stuDept;
	private String stuMajor;
	private String stuClazz;
	private String utype;
	
	public Student(){};
	
	public Student(String sno, String sname, String stuCardNum, String shbt,String sbloodType, 
			String sstartSign, String utype){
		this(sno, sname, 0, 18, stuCardNum, "", shbt, sbloodType, sstartSign, "", "", "","",utype);
	}
	
	public Student(String sno, String sname, int ssex, int sage, String stuCardNum, String stuAdderss, String shbt,
			String sbloodType, String sstartSign, String sevaledType, String stuDept, String stuMajor,
			String stuClazz,String utype) {
		super();
		this.sno = sno;
		this.sname = sname;
		this.ssex = ssex;
		this.sage = sage;
		this.stuCardNum = stuCardNum;
		this.stuAdderss = stuAdderss;
		this.shbt = shbt;
		this.sbloodType = sbloodType;
		this.sstartSign = sstartSign;
		this.sevaledType = sevaledType;
		this.stuDept = stuDept;
		this.stuMajor = stuMajor;
		this.stuClazz = stuClazz;
	}
	/* Getter & Setter */
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
	public int getSsex() {
		return ssex;
	}
	public void setSsex(int ssex) {
		this.ssex = ssex;
	}
	public int getSage() {
		return sage;
	}
	public void setSage(int sage) {
		this.sage = sage;
	}
	public String getStuCardNum() {
		return stuCardNum;
	}
	public void setStuCardNum(String stuCardNum) {
		this.stuCardNum = stuCardNum;
	}
	public String getStuAdderss() {
		return stuAdderss;
	}
	public void setStuAdderss(String stuAdderss) {
		this.stuAdderss = stuAdderss;
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
	public String getUtype() {
		return utype;
	}
	public void setUtype(String utype) {
		this.utype = utype;
	}

	@Override
	public Student mapRow(ResultSet rs, int index) throws SQLException {
		Student student = new Student();
		
		student.setStuCardNum(rs.getString("stu_card_num"));
		student.setSno(rs.getString("sno"));
		student.setSage(rs.getInt("sage"));
		student.setSsex(rs.getInt("ssex"));
		student.setSname(rs.getString("sname"));
		student.setStuAdderss(rs.getString("stu_address"));
		student.setStuDept(rs.getString("stu_dept"));
		student.setStuMajor(rs.getString("stu_major"));
		student.setStuClazz(rs.getString("stu_clazz"));
		
		student.setShbt(rs.getString("s_hbt"));//课余活动
		student.setSbloodType(rs.getString("s_blood_type"));//血型
		student.setSstartSign(rs.getString("s_start_sign"));//星座
		student.setSevaledType(rs.getString("s_evaled_type"));//评分结果
		
		return student;
	}
}
