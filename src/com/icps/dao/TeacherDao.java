package com.icps.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.icps.bean.Teacher;

public class TeacherDao {
	private JdbcTemplate jt = new JdbcTemplate(MyDataSource.dateSource);
	
	public TeacherDao(){
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
	//登陆验证
	public boolean login(String tno, String tname) throws Exception{
		int count = 0;
		String sql = "select count(*) from icps_tecr where tno =? and tname =?";
		
		if (sql != null && sql != "") {
//			int count = jt.queryForObject(sql, Integer.class);
			count = jt.queryForObject(sql,new Object[]{tno, tname}, Integer.class);
			
			if (count == 1) {//查寻到一条数据，证明存在该登录用户，返回 true
				return true;
			} else if(count > 1) {		//查寻到不止一条数据，证明用户不存在，或查询出错，返回 true
				
				return false;
			} else {
				return false;
			}
		} else {
			System.err.println("sql 不能为空");
			throw new Exception("sql 语句为空, 不能执行该查询");
		}
	}
	//获取学生信息
	public Teacher findTeacherById(String id) throws Exception{
		Teacher tea = null;
		String sql = "select * from icps_tecr where tno =?";

		tea = jt.queryForObject(sql, new Object[] {id},new Teacher());
		return tea;
	}
	public List<Map<String, Object>> findStuListMap(String sql, String ... str) throws Exception{
		List<Map<String, Object>> teaList = new ArrayList<Map<String, Object>>();
		teaList = jt.queryForList(sql, str);
		return teaList;
	}
	//获取学生列表信息
	public List<Teacher> findStuListObj(String sql, String ... str){
		List<Teacher> teaList = new ArrayList<>();
		
		teaList = jt.query(sql,new Teacher());
		
		return teaList;
	}
	//修改学生信息
	public void updateStuInfo(String sql, final Object ... str) throws Exception{
		
//		jt.update(sql,str);
		if(null != str && str.length > 0) {
			throw new Exception("sql 参数 不能为空");
		}
		jt.update(sql, new PreparedStatementSetter(){
			Object[] s = str;
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				// TODO Auto-generated method stub
				for (int i = 1; i <= s.length; i++) {
					ps.setObject(i, s[i]);
				}
			}
		});
	}
	//删除学生信息
	public void deleteStu(String sql, String ...str){
		
	}
}
