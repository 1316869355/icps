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
import com.icps.util.Evalued;
import com.icps.util.SystemCodeEnum;

/**
 * Servlet implementation class BaseServlet
 */
@WebServlet("/upload")
public class uploadInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private StudentDao stuDao = null;
	private TeacherDao teaDao = null;
	private Map<String, Object> result = new  HashMap<String, Object>();

	public uploadInfoServlet(StudentDao suDao, TeacherDao teaDao){
		this.stuDao = suDao;
		this.teaDao = teaDao;
	}
	/**
     * Default constructor. 
     */
    public uploadInfoServlet() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void init() throws ServletException {
    	// TODO Auto-generated method stub
    	super.init();
    	stuDao = new StudentDao();
    	teaDao = new TeacherDao();
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONArray array = new JSONArray();
		String sql="";
		HttpSession session = request.getSession();
		String cardno = (String) session.getAttribute("cardno");
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json");
		PrintWriter pw = response.getWriter();
		
		if(cardno == null || "".equals(cardno)){
			response.setStatus(403);
			result.put("res", "error");
			result.put("info", "登录名或密码错误");
			array.add(result);//将数据转换为json数据
			pw.write(array.toString());
			return;
		}
		String name = request.getParameter("name");//姓名
		String sex = request.getParameter("sex");//性别
		String region = request.getParameter("region");//生源地
		String blood = request.getParameter("blood");//血型
		String relax = request.getParameter("relax");//放松方式
		String start = request.getParameter("start");//星座
		String evalued = Evalued.exeute(relax, blood, start);
		try {
			sql = "update icps_stu set sname=?, ssex=?, region=?,sblood=?, shbt=?, start_sign=?,evalued_type=? where stu_card_no=?";
			stuDao.updateStuInfo(sql, name, sex, region, blood, relax, start, evalued, cardno);
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
