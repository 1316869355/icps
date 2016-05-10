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
	, OBLOOD( "0501",  "A")
	, ABLOOD( "0502",  "B")
	, BBLOOD( "0503",  "O")
	, ABBLOOD("0504",  "AB")
	, SPORT  ("0401",  "运动健身")
	, ENTERTE("0402",  "休闲娱乐")
	, SOCIAL ("0403",  "逛街聚会")
	, ZHAI   ("0404",  "网购影视")
	, MJSTRAT("0601",  "摩羯座")
	, SPSTART("0602",  "水平座")
	, SYSTRAT("0603",  "双鱼座")
	, BYSTART("0604",  "白羊座")
	, JNSTART("0605",  "金牛座")
	, SAZSTRAT("0606", "双子座")
	, JXSTART("0607",  "巨蟹座")
	, SIZSTRAT("0608", "狮子座")
	, CNSART("0609",   "处女座")
	, TPSTRAT("0610",  "天秤座")
	, TXSTART("0611",  "天蝎座")
	, SSSTART("0612",  "射手座")
	, WJTYPE ("0701",  "稳健")
	, STTYPE ("0702",  "随性洒脱")
	, BWTYPE ("0703",  "特殊关注")
	, GZTYPE ("0704",  "班委")
	, EAST   ("0801",  "华东地区")
	, SOUTH  ("0802",  "华南地区")
	, CENTER ("0803",  "华中地区")
	, NORTH  ("0804",  "华北地区")
	, NORTHWEST("0805", "西北地区")
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
	public static String getName(String index) {
        for (SystemCodeEnum code : SystemCodeEnum.values()) {
            if (code.getCodeIndex().equals(index)) {
                return code.getCodeName();
            }
        }
        return null;
    }
}
