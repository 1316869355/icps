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

@WebServlet("/login")
public class loginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private StudentDao stuDao = null;
	private TeacherDao teaDao = null;
	
	private Map<String, Object> result = new  HashMap<String, Object>();
    public loginServlet() {
    	
    }

    @Override
    public void init() throws ServletException {
    	super.init();
    	stuDao = new StudentDao();
    	teaDao = new TeacherDao();
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			doPost(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		HttpSession session = request.getSession();
		JSONArray array = new JSONArray();
		PrintWriter pw=  response.getWriter();
		
		String cardNo = request.getParameter("cardNo");//身份证号
		String userName = request.getParameter("userName");//用户名
		String role = request.getParameter("role");
		
		System.out.println("cardNo " + cardNo + "\tusername"+userName + "\trole"+ role);
		if(null==role||role.equals("")){
			System.out.print("role == null");
			response.addHeader("location", "login.html");
			response.setStatus(302);
			return;
		}
		try {
			if("2".equals(role)){//教师
				//登录
				if( stuDao.findStu(cardNo, userName) ){ //登录成功
					
					session.setAttribute("cardno", cardNo);//保存用户身份证
					response.setStatus(200);
					
					result.put("res", "success");
					array.add(result);//将数据转换为json数据
					pw.write(array.toString());
					return;
				}else{
					System.err.println("登录出错");
					response.setStatus(401);//设置头信息
					
					result.put("res", "error");
					result.put("info", "登录名或密码错误");
					array.add(result);//将数据转换为json数据
					pw.write(array.toString());
					return;
				}
			}
			if("1".equals(role)){//教师
				//登录
				if( teaDao.login(cardNo, userName) ){ //登录成功
					session.setAttribute("cardno", cardNo);//保存用户身份证
					
					//System.out.println("登录成功");
					response.setStatus(200);
					result.put("res", "success");
					array.add(result);//将数据转换为json数据
					pw.write(array.toString());
					return;
				}else{
					System.err.println("登录出错");
					response.setStatus(401);//设置头信息
					
					result.put("res", "error");
					result.put("info", "登录超时，请重新登录");
					array.add(result);//将数据转换为json数据
					pw.write(array.toString());
					return;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		response.setStatus(404);
	}
	
	
/**
 * 1.服务器端跳转
 *  RequestDispatcher dispatcher = req.getRequestDispatcher("test-04.jsp"); // 使用req对象获取RequestDispatcher对象
	dispatcher.forward(req, resp); // 使用RequestDispatcher对象在服务器端向目的路径跳转
	
	2.页面跳转
	res.sendRedirect("test.do"); // servlet实现跳转（客户端跳转）
	
	response.addHeader("location", "login.jsp");response.setStatus(302);
 */
}
