package com.icps.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.icps.bean.Teacher;
import com.icps.dao.StudentDao;
import com.icps.dao.TeacherDao;
import com.sun.media.sound.RealTimeSequencerProvider;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class BaseServlet
 */
@WebServlet("/login")
public class loginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private StudentDao stuDao = null;
	private TeacherDao teaDao = null;
	
	public loginServlet(StudentDao suDao, TeacherDao teaDao){
		this.stuDao = suDao;
		this.teaDao = teaDao;
	}
	private Map<String, Object> result = new  HashMap<String, Object>();
	protected JSONArray array = new JSONArray();
	/**
     * Default constructor. 
     */
    public loginServlet() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void init() throws ServletException {
    	// TODO Auto-generated method stub
    	super.init();
    	stuDao = new StudentDao();
    	teaDao = new TeacherDao();
    }
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

			doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json");
		PrintWriter writer=  response.getWriter();
		
		String cardNo = request.getParameter("cardNo");//身份证号
		String userName = request.getParameter("userName");//用户名
		String role = request.getParameter("role");
		
		System.out.println("cardNo " + cardNo + "\tusername"+userName + "\trole"+ role);
		if(null==role||role.equals("")){
			System.out.print("role == null");
			return;
		}
		try {
			if("1".equals(role)){//教师
				//登录
				if( stuDao.findStu(cardNo, userName) ){ //登录成功
					response.setStatus(302);
					response.addHeader("location", "student.html");//跳转到查询页面
				}else{
					System.err.println("登录出错");
					response.setStatus(401);//设置头信息
					
					result.put("res", "error");
					result.put("info", "登录名或密码错误");
					array.add(result);//将数据转换为json数据
					writer.write(array.toString());
					
					return;
				}
			}
				
			if("2".equals(role)){//学生
				//登录
				if( teaDao.login(cardNo, userName) ){ //登录成功
					response.setStatus(302);
					response.addHeader("location", "search.html");//跳转到查询页面
				}else{
					System.err.println("登录出错");
					response.setStatus(401);//设置头信息
					
					result.put("res", "error");
					result.put("info", "登录名或密码错误");
					array.add(result);//将数据转换为json数据
					writer.write(array.toString());
					
					return;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.setStatus(302);
		//初始化JSONArray对象，并添加数据
//		JSONArray array = new JSONArray();
//		array.add();
		//writer.write("");
		//response.sendRedirect("login.html");
		//response.addHeader("location", "search.html");
		//response.setStatus(302);
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
