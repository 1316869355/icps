package com.icps.bean;

public class Hobit {

	private String hname;
	private String remark;
	public Hobit(){
		
	}
	public Hobit(String hname, String remark){
		this.hname = hname;
		this.remark = remark;
	}
	public String getHname() {
		return hname;
	}
	public void setHname(String hname) {
		this.hname = hname;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}
