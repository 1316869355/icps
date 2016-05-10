package com.icps.bean;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
//db name icps_tecr
public class Teacher implements RowMapper<Teacher> , Serializable{
	private String tno;
	private String tname;
	private String tphone;
	private String tqq;
	private String temail;
	private String pawd;
	private String remark;
	private String utype;
	private int tsex;
	public Teacher(){}
	
	public Teacher(String tno, String tname){
		this(tno, tname, "", "", "", "", "", "",0);
	}
	
	public Teacher(String tno, String tname, String tphone, String tqq, String temail, String pawd, String remark,
			String utype, int tsex) {
		super();
		this.tno = tno;
		this.tname = tname;
		this.tphone = tphone;
		this.tqq = tqq;
		this.temail = temail;
		this.pawd = pawd;
		this.remark = remark;
		this.utype = utype;
		this.tsex = tsex;
	}
	public String getTno() {
		return tno;
	}
	public void setTno(String tno) {
		this.tno = tno;
	}
	public String getTname() {
		return tname;
	}
	public void setTname(String tname) {
		this.tname = tname;
	}
	public String getTphone() {
		return tphone;
	}
	public void setTphone(String tphone) {
		this.tphone = tphone;
	}
	public String getTqq() {
		return tqq;
	}
	public void setTqq(String tqq) {
		this.tqq = tqq;
	}
	public String getTemail() {
		return temail;
	}
	public void setTemail(String temail) {
		this.temail = temail;
	}
	public String getPawd() {
		return pawd;
	}
	public void setPawd(String pawd) {
		this.pawd = pawd;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getUtype() {
		return utype;
	}
	public void setUtype(String utype) {
		this.utype = utype;
	}
	public int getTsex() {
		return tsex;
	}
	public void setTsex(int tsex) {
		this.tsex = tsex;
	}

	@Override
	public Teacher mapRow(ResultSet rs, int index) throws SQLException {
		// TODO Auto-generated method stub
		Teacher tea = new Teacher(
				rs.getString("tno"),
				rs.getString("tname"),
				rs.getString("tphone"),
				rs.getString("tqq"),
				rs.getString("temail"),
				rs.getString("pawd"),
				rs.getString("remark"),
				rs.getString("utype"),
				rs.getInt("tsex")
				);
		
		return tea;
	}
	
}
