
public class StringTest {
	public static void main(String[] args) {
		String s0 = "/";
		System.out.println(s0.split("/").length);
		String s1 = "BaseServlet";
		String s2 = "str/BaseServlet";
		String s3 = "/BaseServlet";
		String s4 = "/BaseServlet/";
		String s5 = "/BaseServlet/str";
		String s6 = "str1/BaseServlet/str2";
		String s7 = "/str3?jjj=123&jks=lk";
		System.out.println(s7.indexOf("?"));
		System.out.print(s7.split("\\?")[0]+"\t"+s7.split("\\?")[1] +"\t");System.out.println(s7.split("\\?")[1].split("&")[0]);
		System.out.println(s7.contains("&"));
		System.out.println(s6.endsWith("/"));
		System.out.println("s1\t"+ s1.split("/")[0] + s1.split("/").length);
		System.out.println("s2\t"+ s2.split("/")[0]+ "\t " +s2.split("/")[1]+"\tlength " + s2.split("/").length);
		System.out.println("s3\t"+ s3.split("/")[0]+ " \t" +s3.split("/")[1]+"\tlength " + s3.split("/").length);
		System.out.println("s4\t"+ s4.split("/")[0]+ " \t" +s4.split("/")[1]+"\tlength " + s4.split("/").length);
		System.out.println("s5\t"+ s5.split("/")[0]+ " \t" +s5.split("/")[1] +"\tlength " + s5.split("/").length);
		System.out.println("s6\t"+ s6.split("/")[0]+ " \t" +s6.split("/")[1]+ " \t" +s6.split("/")[2]+"\tlength " + s6.split("/").length);
//		System.out.println("s7"+ s1.split("/"));
		
	}
}
