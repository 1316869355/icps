/**
 * 系统数据字典
 */
package com.icps.util;

/**
 * @author Administrator
 *
 */

public enum SystemCodeEnum {
	  ADROLE( "0101",  "系统管理员")
	, TEROLE( "0102",  "教师")
	, STUROLE("0103",  "学生")
	, OBLOOD( "0401",  "O")
	, ABLOOD( "0402",  "A")
	, BBLOOD( "0403",  "B")
	, ABBLOOD("0404",  "AB")
	, SPORT  ("0301",  "运动健身")
	, ENTERTE("0302",  "休闲娱乐")
	, SOCIAL ("0303",  "逛街聚会")
	, ZHAI   ("0304",  "网购影视")
	, MJSTRAT("0501",  "摩羯座")
	, SPSTART("0502",  "水平座")
	, SYSTRAT("0503",  "双鱼座")
	, BYSTART("0504",  "白羊座")
	, JNSTART("0505",  "金牛座")
	, SAZSTRAT("0506", "双子座")
	, JXSTART("0507",  "巨蟹座")
	, SIZSTRAT("0508", "狮子座")
	, CNSART("0509",   "处女座")
	, TPSTRAT("0510",  "天秤座")
	, TXSTART("0511",  "天蝎座")
	, SSSTART("0512",  "射手座")
	, WJTYPE ("0601",  "稳健")
	, STTYPE ("0602",  "随性洒脱")
	, BWTYPE ("0603",  "班委")
	, GZTYPE ("0604",  "特殊关注")
	;
	public String codeName;
	public String codeIndex;
	private SystemCodeEnum(String codeName, String codeIndex){
		this.codeName = codeName;
		this.codeIndex = codeIndex;
	}
	public String getCodeName() {
		return codeName;
	}
	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}
	public String getCodeIndex() {
		return codeIndex;
	}
	public void setCodeIndex(String codeIndex) {
		this.codeIndex = codeIndex;
	}
	
	

}
