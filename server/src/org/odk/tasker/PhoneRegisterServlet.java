package org.odk.tasker;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.odk.tasker.dao.DAOUtil;
import org.odk.tasker.dao.PMF;
import org.odk.tasker.dao.PhoneUser;

public class PhoneRegisterServlet extends HttpServlet {


	private static final long serialVersionUID = 1L;

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("    This is ");
		out.print(this.getClass());
		out.println(", using the GET method");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String sim = request.getParameter("sim");
		
		PhoneUser phoneUser = DAOUtil.loadPhoneUserBySIM( sim );
		phoneUser.setUserId(request.getParameter("userid"));
		phoneUser.setIMEI(request.getParameter("imei"));
		phoneUser.setPhoneNumber(request.getParameter("phonenumber"));
		phoneUser.setIMSI(request.getParameter("imsi"));
		phoneUser.setName(request.getParameter("userName"));
		phoneUser.setLocation(request.getParameter("userLocation"));
		
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();

		pm.makePersistent(phoneUser);

		pm.close();
		
	}

}
