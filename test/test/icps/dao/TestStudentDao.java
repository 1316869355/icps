package test.icps.dao;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.icps.bean.Student;
import com.icps.dao.StudentDao;

public class TestStudentDao {
	StudentDao stdao = new StudentDao();
	
	@Test
	public void testExits() throws Exception{
		stdao.isExits("", "");
	}
	@Test
	public void testLogin() throws Exception{
		stdao.findStu("123", "测试");
	}
	@Test
	public void findStuById() throws Exception{
		
		Student stu = stdao.findStuById("123");
	}
	@Test
	public void findStuList() throws Exception{
		String sql = "select * from icps_stu";
		List<Map<String, Object>> stuList = stdao.findStuListObj(sql);
		
	}
	
	@Test
	public void findStuListMap() throws Exception{
		StringBuffer sbf = new StringBuffer();
		sbf.append(" select stu_card_num cardNo, sno, sname, ssex,");
		sbf.append(" stu_address address, ic1.code_name sdept, ic2.code_name major, stu_clazz clazz,");
		sbf.append(" s_hbt hbt, s_blood_type blood, s_start_sign start, s_evaled_type type");
		sbf.append(" from icps_stu stu, icps_code ic1, icps_code ic2 ");
		sbf.append(" where stu.stu_dept = ic1.code and stu.stu_major = ic2.code");
		
		List<Student> stuList = stdao.findStuListMap(sbf.toString(), null);
		Set sk = null;
//		value 
		
	}
	@Test
	public void delStu(){
		stdao.deleteStu("delete from icps_stu where sno=?", "123");
	}
}
