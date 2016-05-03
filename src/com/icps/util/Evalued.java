package com.icps.util;

public class Evalued {
	public Evalued(){}
	
	public static String exeute(String relax, String blood, String start){
		String rn = "";
		if(relax == null) return "norelax";
		if(blood == null || "".equals(blood)) return "noblood";
		if(start == null || "".equals(start)) return "nostart";
		switch (relax) {
		case "0401"://运动健身
			switch (blood) {
			case "0501":
				return getEvalued4151(start);
			case "0502":
				return getEvalued4152(start);
			case "0503":
				return getEvalued4153(start);
			case "0504":
				return getEvalued4154(start);
			}
			break;
		case "0402"://休闲娱乐
			switch (blood) {
			case "0501"://A
				return getEvalued4251(start);
			case "0502"://B
				return getEvalued4252(start);
			case "0503"://O
				return getEvalued4253(start);
			case "0504"://AB
				return getEvalued4254(start);
			}
			break;
		case "0403"://逛街聚会
			switch (blood) {
			case "0501":
				return getEvalued4351(start);
			case "0502":
				return getEvalued4352(start);
			case "0503":
				return getEvalued4353(start);
			case "0504":
				return getEvalued4354(start);
			}
			break;
		case "0404"://网购影视
			switch (blood) {
			case "0501":
				return getEvalued4451(start);
			case "0502":
				return getEvalued4452(start);
			case "0503":
				return getEvalued4453(start);
			case "0504":
				return getEvalued4454(start);
			}
			break;
		}
		return "";
	}

	public static String getEvalued4454(String start) {
		switch ( start) {
		case "0601":
			return "0701";
		case "0602":
			return "0702";
		case "0603":
			return "0702";
		case "0604":
			return "0701";
		case "0605":
			return "0701";
		case "0606":
			return "0701";
		case "0607":
			return "0701";
		case "0608":
			return "0701";
		case "0609":
			return "0701";
		case "0610":
			return "0702";
		case "0611":
			return "0703";
		case "0612":
			return "0701";
		}
		return null;
	}

	private static String getEvalued4453(String start) {
		switch ( start) {
		case "0601":
			return "0701";
		case "0602":
			return "0701";
		case "0603":
			return "0702";
		case "0604":
			return "0701";
		case "0605":
			return "0701";
		case "0606":
			return "0702";
		case "0607":
			return "0702";
		case "0608":
			return "0702";
		case "0609":
			return "0702";
		case "0610":
			return "0701";
		case "0611":
			return "0701";
		case "0612":
			return "0702";
		}
		return null;
	}

	private static String getEvalued4452(String start) {
		switch ( start) {
		case "0601":
			return "0701";
		case "0602":
			return "0701";
		case "0603":
			return "0702";
		case "0604":
			return "0701";
		case "0605":
			return "0701";
		case "0606":
			return "0702";
		case "0607":
			return "0701";
		case "0608":
			return "0701";
		case "0609":
			return "0701";
		case "0610":
			return "0701";
		case "0611":
			return "0701";
		case "0612":
			return "0702";
		}
		return null;
	}

	private static String getEvalued4451(String start) {
		switch ( start) {
		case "0601":
			return "0701";
		case "0602":
			return "0703";
		case "0603":
			return "0702";
		case "0604":
			return "0702";
		case "0605":
			return "0701";
		case "0606":
			return "0703";
		case "0607":
			return "0701";
		case "0608":
			return "0703";
		case "0609":
			return "0702";
		case "0610":
			return "0702";
		case "0611":
			return "0701";
		case "0612":
			return "0702";
		}
		return null;
	}

	private static String getEvalued4354(String start) {
		switch ( start) {
		case "0601":
			return "0701";
		case "0602":
			return "0702";
		case "0603":
			return "0702";
		case "0604":
			return "0701";
		case "0605":
			return "0701";
		case "0606":
			return "0701";
		case "0607":
			return "0701";
		case "0608":
			return "0701";
		case "0609":
			return "0704";
		case "0610":
			return "0702";
		case "0611":
			return "0702";
		case "0612":
			return "0701";
		}
		return null;
	}

	private static String getEvalued4353(String start) {
		switch ( start) {
		case "0601":
			return "0701";
		case "0602":
			return "0704";
		case "0603":
			return "0702";
		case "0604":
			return "0704";
		case "0605":
			return "0704";
		case "0606":
			return "0702";
		case "0607":
			return "0702";
		case "0608":
			return "0704";
		case "0609":
			return "0702";
		case "0610":
			return "0701";
		case "0611":
			return "0704";
		case "0612":
			return "0702";
		}
		return null;
	}

	private static String getEvalued4352(String start) {
		switch ( start) {
		case "0601":
			return "0701";
		case "0602":
			return "0704";
		case "0603":
			return "0702";
		case "0604":
			return "0704";
		case "0605":
			return "0701";
		case "0606":
			return "0702";
		case "0607":
			return "0701";
		case "0608":
			return "0701";
		case "0609":
			return "0701";
		case "0610":
			return "0704";
		case "0611":
			return "0701";
		case "0612":
			return "0702";
		}
		return null;
	}

	private static String getEvalued4351(String start) {
		switch ( start) {
		case "0601":
			return "0701";
		case "0602":
			return "0703";
		case "0603":
			return "0702";
		case "0604":
			return "0703";
		case "0605":
			return "0701";
		case "0606":
			return "0703";
		case "0607":
			return "0701";
		case "0608":
			return "0703";
		case "0609":
			return "0702";
		case "0610":
			return "0702";
		case "0611":
			return "0701";
		case "0612":
			return "0703";
		}
		return null;
	}

	private static String getEvalued4254(String start) {
		switch ( start) {
		case "0601":
			return "0701";
		case "0602":
			return "0702";
		case "0603":
			return "0702";
		case "0604":
			return "0702";
		case "0605":
			return "0701";
		case "0606":
			return "0702";
		case "0607":
			return "0701";
		case "0608":
			return "0704";
		case "0609":
			return "0704";
		case "0610":
			return "0702";
		case "0611":
			return "0702";
		case "0612":
			return "0701";
		}
		return null;
	}

	private static String getEvalued4253(String start) {
		switch ( start) {
		case "0601":
			return "0701";
		case "0602":
			return "0704";
		case "0603":
			return "0702";
		case "0604":
			return "0704";
		case "0605":
			return "0704";
		case "0606":
			return "0702";
		case "0607":
			return "0704";
		case "0608":
			return "0704";
		case "0609":
			return "0702";
		case "0610":
			return "0701";
		case "0611":
			return "0704";
		case "0612":
			return "0702";
		}
		return null;
	}

	private static String getEvalued4252(String start) {
		switch ( start) {
		case "0601":
			return "0701";
		case "0602":
			return "0704";
		case "0603":
			return "0702";
		case "0604":
			return "0704";
		case "0605":
			return "0701";
		case "0606":
			return "0702";
		case "0607":
			return "0701";
		case "0608":
			return "0702";
		case "0609":
			return "0702";
		case "0610":
			return "0704";
		case "0611":
			return "0701";
		case "0612":
			return "0702";
		}
		return null;
	}

	private static String getEvalued4251(String start) {
		switch ( start) {
		case "0601":
			return "0701";
		case "0602":
			return "0703";
		case "0603":
			return "0702";
		case "0604":
			return "0702";
		case "0605":
			return "0701";
		case "0606":
			return "0703";
		case "0607":
			return "0701";
		case "0608":
			return "0701";
		case "0609":
			return "0702";
		case "0610":
			return "0702";
		case "0611":
			return "";
		case "0612":
			return "";
		}
		return null;
	}

	private static String getEvalued4154(String start) {
		switch ( start) {
		case "0601":
			return "0701";
		case "0602":
			return "0702";
		case "0603":
			return "0702";
		case "0604":
			return "0704";
		case "0605":
			return "0701";
		case "0606":
			return "0702";
		case "0607":
			return "0702";
		case "0608":
			return "0704";
		case "0609":
			return "0704";
		case "0610":
			return "0702";
		case "0611":
			return "0702";
		case "0612":
			return "0701";
		}
		return null;
	}

	private static String getEvalued4152(String start) {
		switch ( start) {
		case "0601":
			return "0701";
		case "0602":
			return "0704";
		case "0603":
			return "0702";
		case "0604":
			return "0704";
		case "0605":
			return "0704";
		case "0606":
			return "0702";
		case "0607":
			return "0704";
		case "0608":
			return "0704";
		case "0609":
			return "0702";
		case "0610":
			return "0701";
		case "0611":
			return "0704";
		case "0612":
			return "0702s";
		}
		return null;
	}

	private static String getEvalued4153(String start) {
		switch ( start) {
		case "0601":
			return "0701";
		case "0602":
			return "0704";
		case "0603":
			return "0702";
		case "0604":
			return "0704";
		case "0605":
			return "0701";
		case "0606":
			return "0702";
		case "0607":
			return "0701";
		case "0608":
			return "0701";
		case "0609":
			return "0702";
		case "0610":
			return "0704";
		case "0611":
			return "0701";
		case "0612":
			return "0702";
		}
		return null;
	}

	private static String getEvalued4151(String start) {
		switch ( start) {
		case "0601":
			return "0701";
		case "0602":
			return "0701";
		case "0603":
			return "0702";
		case "0604":
			return "0702";
		case "0605":
			return "0701";
		case "0606":
			return "0701";
		case "0607":
			return "0701";
		case "0608":
			return "0701";
		case "0609":
			return "0702";
		case "0610":
			return "0702";
		case "0611":
			return "0701";
		case "0612":
			return "0701";
		}
		return null;
	}
}
