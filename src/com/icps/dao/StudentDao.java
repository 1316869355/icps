package com.icps.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.StatementCallback;

import com.icps.bean.Student;

public class StudentDao {
	private JdbcTemplate jt = new JdbcTemplate(MyDataSource.dateSource);
	
	public StudentDao(){
	}
	//监测用户是否存在
	public boolean isExits(String sql, String ... str) throws Exception{
		// sql = "select * from icps_code where code =?";
		if (sql != null && sql != "") {
			int count = jt.queryForObject(sql, Integer.class);
			if (count > 0) {
				return true;
			} else {
				return false;
			}
		} else {
			System.err.println("sql 不能为空");
			throw new Exception("sql 语句为空, 不能执行该查询");
		}
	}
	public boolean findStu(final String cno, final String name){
		 List<Map<String, Object>> stu = null;
		String sql = "select sname,ssex,sage from icps_stu stu where stu.stu_card_num='"+cno+"'  and  stu.sname like '%"+name+"%'";
		System.out.println(sql);
		stu = jt.queryForList(sql);
		System.out.println(stu.size());
		return false;
	}
	//获取学生信息
	public Student findStuById(String id) throws Exception{
		Student stu = null;
		String sql = "select * from icps_stu where stu_card_num =?";

		stu = jt.queryForObject(sql, new Object[] {id},new Student());
		return stu;
	}
	public List<Map<String, Object>> findStuListMap(String sql, String ... str) throws Exception{
		List<Map<String, Object>> stuList = new ArrayList<Map<String, Object>>();
		stuList = jt.queryForList(sql, str);
		return stuList;
	}
	//获取学生列表信息
	public List<Student> findStuListObj(String sql, String ... str){
		List<Student> stuList = new ArrayList<>();
		
		stuList = jt.query(sql,new Student());
		
		return stuList;
	}
	//修改学生信息
	public void updateStuInfo(String sql, String ... str){
		Student stu = new Student();
		
	}
	//删除学生信息
	public void deleteStu(String sql, String ...str){
		
	}
}
