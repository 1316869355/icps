package test.icps.bean;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.icps.dao.StudentDao;
import com.icps.util.Evalued;

public class TestStudent {
	StudentDao stdao = new StudentDao();
	@Test
	public void testStudent(){
		String str = "str";
	}
	
	@Test
	public void updateStudent(){
		String sql = "update icps_stu set sname=?, ssex=?, region=?,sblood=?, shbt=?, start_sign=?, evalued_type=? where stu_card_no = '123' ";
		stdao.updateStuInfo(sql, "test", 1, "0801", "0501", "0401", "0606", Evalued.exeute("0401","0501","0606"));
	}
	@Test
	public void getPageList(){
		List<Map<String, Object>> list = stdao.pageList("select sname,ssex,sblood from icps_stu where stu_dept='0204'", 1, 4);
		System.out.println(list);
	}
}
