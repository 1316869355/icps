import org.junit.Test;

import com.icps.util.SystemCodeEnum;

public class testEnum {

	@Test
	public void printEnum(){
		System.out.println("Enum:\n"+
				"role: "+SystemCodeEnum.ADROLE.codeIndex+"\troleName: "+ SystemCodeEnum.ADROLE.codeName);
	}
}
