package com.icps.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;

import com.icps.bean.Student;
import com.icps.dao.StudentDao;
import com.icps.dao.TeacherDao;

/**
 * Servlet implementation class searchServlet
 */
@WebServlet("/search")
public class searchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private StudentDao stuDao = null;
	private TeacherDao teaDao = null;

	private Map<String, Object> result = new  HashMap<String, Object>();
    public searchServlet() {
        super();
    }
    public void init() throws ServletException {
    	// TODO Auto-generated method stub
    	super.init();
    	stuDao = new StudentDao();
    	teaDao = new TeacherDao();
    }
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("utf-8");
		
		HttpSession session = req.getSession();
		PrintWriter pw=  resp.getWriter();
		JSONArray array = new JSONArray();
		StringBuffer sql = new StringBuffer();
		String cno = (String) session.getAttribute("cardno");
//		List<Object> condation = new ArrayList<Object>();
		
		List<Map<String, Object>> stu = new ArrayList<Map<String, Object>>();
		
		String name = req.getParameter("stuname");
		String dept = req.getParameter("dept");
		String major = req.getParameter("major");
		String sex = req.getParameter("sex");
		//检测是否为教师
		if(null == cno || "".equals(cno)){
			result.put("res", "error");
			result.put("info", "登陆超时");
			array.add(result);//将数据转换为json数据
			resp.setStatus(401);
			pw.write(array.toString());
			return;
		} else {
			try {
				if(teaDao.isExits("select count(*) from icps_tecr where tno=?", cno)){
					sql.append("select sno,sname,ssex sex,s1.code_name dept,s2.code_name major  from icps_stu ");
					sql.append(",icps_sys_code s1,icps_sys_code s2 where icps_stu.stu_dept = s1.code and ");
					sql.append(" stu_major = s2.code");
					if(null != name && !"".equals(name)){
						sql.append(" sname='%"+name+"%' ");
					}
					if(null != dept && !"".equals(dept)&& !"0".equals(dept)){
						if(sql.indexOf("where") > 0)
							sql.append("and dept='"+dept+"'");
						else{
							sql.append("where dept='"+dept+"'");
						}
					}
					if(null != major && !"".equals(major)&& !"0".equals(major)){
							if(sql.indexOf("where") > 0){
								sql.append("and major='"+major+"'");
							}else{
								sql.append("where major='"+major+"'");
							}
					}
					if(null != sex){
						if(0!=Integer.parseInt(sex)){
							if(sql.indexOf("where") > 0){
								sql.append("and sex="+Integer.parseInt(sex));
							}else{
								sql.append("where sex="+Integer.parseInt(sex));
							}
						}
					}
					System.out.println(sql.toString());
					stu = stuDao.findStuListObj(sql.toString());
					
					result.put("res", "success");
					result.put("info", stu);
					array.add(result);
					pw.write(array.toString());
					resp.setStatus(200);
					return;
				}else{
					result.put("res", "error");
					result.put("info", "没有权限");
					array.add(result);//将数据转换为json数据
					resp.setStatus(401);
					pw.write(array.toString());
					return;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
