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

import org.junit.runner.Request;

import net.sf.json.JSONArray;

import com.icps.dao.CodeDao;

/**
 * Servlet implementation class CodeServlet
 */
@WebServlet("/code")
public class CodeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private CodeDao codeDao = null;
    private Map<String, Object> result = new  HashMap<String, Object>();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CodeServlet() {
        super();
    }
    @Override
    public void init() throws ServletException {
    	super.init();
    	codeDao = new CodeDao();
    }
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("utf-8");
		PrintWriter pw=  resp.getWriter();
		JSONArray array = new JSONArray();
		
		String param = req.getParameter("type");
		if("dept".equals(param)){
		List<Map<String, Object>> objs= codeDao.getDeptList();
			System.out.println(objs);
			result.put("res", "success");
			result.put("info", objs);
			array.add(result);
			//array.add(objs);
			pw.write(array.toString());
			resp.setStatus(200);
			return;
		}else if("major".equals(param)){
			String code = req.getParameter("code");
			result.put("info", codeDao.getMajorList(code));
			result.put("res", "success");
			
			array.add(result);
			pw.write(array.toString());
			resp.setStatus(200);
			return;
			
		}
	}

}
