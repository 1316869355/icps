package com.icps.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;

import com.icps.dao.StudentDao;
import com.icps.dao.TeacherDao;

/**
 * Servlet implementation class Count
 */
@WebServlet(name = "count", urlPatterns = { "/count" })
public class CountStu extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private StudentDao stuDao = null;
	private TeacherDao teaDao = null;
	
	private Map<String, Object> result = new  HashMap<String, Object>();
    public CountStu() {
        super();
    }
    @Override
    public void init() throws ServletException {
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
		
		PrintWriter pw=  resp.getWriter();
		JSONArray array = new JSONArray();
		String countType = req.getParameter("type");
		//stuDao.count(sql);
		StringBuffer sql = new StringBuffer();
		int stu = 0;
		
		String name = req.getParameter("stuname");
		String dept = req.getParameter("dept");
		String major = req.getParameter("major");
		String sex = req.getParameter("sex");
		
		//检测是否为教师
			sql.append("select count(*) from icps_stu");
			if(null != name && !"".equals(name)){
				sql.append("where sname='%"+name+"%' ");
			}
			if(null != dept && !"".equals(dept)&& !"0".equals(dept)){
				if(sql.indexOf("where") > 0)
					sql.append(" and dept='"+dept+"' ");
				else{
					sql.append(" where dept='"+dept+"' ");
				}
			}
			if(null != major && !"".equals(major)&& !"0".equals(major)){
					if(sql.indexOf("where") > 0){
						sql.append(" and major='"+major+"' ");
					}else{
						sql.append(" where major='"+major+"' ");
					}
			}
			if(null != sex){
				if(0!=Integer.parseInt(sex)){
					if(sql.indexOf(" where") > 0){
						sql.append(" and sex="+Integer.parseInt(sex));
					}else{
						sql.append(" where sex="+Integer.parseInt(sex));
					}
				}
			}
			System.out.println(sql.toString());
			stu = stuDao.count(sql.toString());
//			System.out.println(stu);
			result.put("res", "success");
			result.put("info", stu);
			array.add(result);
			pw.write(array.toString());
			resp.setStatus(200);
			return;
	}
}
