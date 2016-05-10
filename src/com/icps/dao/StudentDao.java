package com.icps.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

import sun.org.mozilla.javascript.internal.ast.ThrowStatement;

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
	public boolean findStu(final String cno, final String name) throws Exception{
		Student stu = null;
		String sql = "select * from icps_stu stu where stu.stu_card_no='"+cno+"'  and  stu.sname like '%"+name+"%'";
		try{
			stu = jt.queryForObject(sql, new Student());
		}catch(Exception e){
			System.out.println("login error");;
			return false;
		}
		if(stu.getSname().length() > 0){
			return true;
		}
		System.out.println(stu);
		return false;
	}
	//获取学生信息
	public Student findStuById(String id) throws Exception{
		Student stu = null;
		String sql = "select * from icps_stu where stu_card_num =?";
		stu = jt.queryForObject(sql, new Object[] {id},new Student());
		return stu;
	}
	public List<Student> findStuListMap(String sql, Object ... str) throws Exception{
		List<Student> stuList = new ArrayList<Student>();
		stuList = jt.queryForList(sql, Student.class, str);
		return stuList;
	}
	//获取学生列表信息
	public List<Map<String, Object>> findStuListObj(String sql, Object ... str){
		List<Map<String, Object>> stuList = new ArrayList<>();
		
		stuList = jt.queryForList(sql, str);
		
		return stuList;
	}
	//修改学生信息
	public void updateStuInfo(String sql, Object ... str){
		Student stu = new Student();
		jt.update(sql, str);
	}
	//删除学生信息
	public void deleteStu(String sql, final Object ...str){
		try{
			jt.update(sql,str);
		}catch(Exception e){
			System.err.println("delete error");
		}
	}
	//总数
    public int count(String sql) {
       // String sql = "select count(*) from icps_stu";  
        return jt.queryForObject(sql, Integer.class);  
    }
    //分页
    public List<Map<String, Object>> pageList(String sql, int npage, int numPerPage, Object ... str){
    	Page page = new Page(sql, npage, numPerPage, jt);
    	
    	return page.getResultList();
    }
}
