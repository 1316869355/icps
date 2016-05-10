package test.icps.bean;

import org.junit.Test;

import com.icps.dao.TeacherDao;

public class TestTeacher {
	TeacherDao teaDao = new TeacherDao();
	@Test
	public void find(){
		try {
			System.out.println(teaDao.login("1234", "教师1"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
