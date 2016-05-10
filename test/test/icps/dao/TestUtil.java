package test.icps.dao;

import org.junit.Test;

import com.icps.util.Evalued;

public class TestUtil {

	@Test
	public void getValued(){
		String str = Evalued.exeute("0401","0501","0606");
		
		System.out.println(str);
	}
}
