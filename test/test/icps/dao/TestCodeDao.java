package test.icps.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.icps.bean.SysCode;
import com.icps.dao.CodeDao;

public class TestCodeDao {
	CodeDao cdao = new CodeDao();
	@Test
	public void getMajor(){
		List<Map<String, Object>> code = cdao.getMajorList("0204");
		
		for (Map<String, Object> sysCode : code) {
			Set key = sysCode.keySet();
			for (Object str : key) {
				System.out.println(str+": \t"+ sysCode.get(str));
				
			}
		}
	}
}
