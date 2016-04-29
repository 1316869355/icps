package com.icps.bean;

public class SysCode {
	private String code;
	private String codeName;
	private String codeDese;
	private String codeParent;
	private int codeType;
	
	public SysCode(){
		
	}

	public SysCode(String code, String codeName, String codeDese, String codeParent, int codeType) {
		super();
		this.code = code;
		this.codeName = codeName;
		this.codeDese = codeDese;
		this.codeParent = codeParent;
		this.codeType = codeType;
	}
	public SysCode(String code, String codeName, int codeType, String codeParent){
		this(code, codeName, "", codeParent, codeType);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}

	public String getCodeDese() {
		return codeDese;
	}

	public void setCodeDese(String codeDese) {
		this.codeDese = codeDese;
	}

	public String getCodeParent() {
		return codeParent;
	}

	public void setCodeParent(String codeParent) {
		this.codeParent = codeParent;
	}

	public int getCodeType() {
		return codeType;
	}

	public void setCodeType(int codeType) {
		this.codeType = codeType;
	}
	
	
}
