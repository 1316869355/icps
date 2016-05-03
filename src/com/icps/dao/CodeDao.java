package com.icps.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.icps.bean.SysCode;

public class CodeDao {
	private JdbcTemplate jt = new JdbcTemplate(MyDataSource.dateSource);
	
	public CodeDao(){
	}
	
	public List<Map<String, Object>> getDeptList(){
	
		return jt.queryForList("select code, code_name cname from icps_sys_code where code_type=2");
	}
	
	public List<Map<String, Object>> getMajorList(String dept){
		return jt.queryForList("select code, code_name cname from icps_sys_code where code_type=3 and parent_code = '"+dept+"'");
	}
}
