package org.odk.tasker;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.odk.tasker.dao.DAOUtil;
import org.odk.tasker.dao.PMF;
import org.odk.tasker.dao.PhoneUser;
import org.odk.tasker.dao.UserTask;
import org.odk.tasker.utils.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

public class TaskSychronizerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger("TaskSychronizerServlet");

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/xml");
		PrintWriter out = response.getWriter();
		Document doc = XmlUtils.createXmlDoc(null, "tasklist");

		String imei = request.getParameter("imei");
		String sim = request.getParameter("sim");
		if (imei == null && sim == null) {
			response.sendError(400);
			LOG.log(Level.WARNING, "no imei or sim sent with request");
			return;
		}

		DAOUtil.buildTaskXml(doc, sim);

		XmlUtils.serialiseXml(doc, out);
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String xml = request.getParameter("xml");
		String sim = request.getParameter("sim");

		PhoneUser user = DAOUtil.loadPhoneUserBySIM(sim);
		List<UserTask> tasks = DAOUtil.getTasksForUser(user.getId());

		byte[] bytes = xml.getBytes("UTF-8");
		Document doc = XmlUtils.getXmlDocument(new ByteArrayInputStream(bytes));

		NodeList taskNodes = doc.getElementsByTagName("Task");

		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		
		for (int i = 0; i < taskNodes.getLength(); i++) {
						
			if (!(taskNodes.item(i) instanceof Element)) {
				continue;
			}

			Element taskEl = (Element) taskNodes.item(i);

			NamedNodeMap taskAttributes = taskEl.getAttributes();

			String id = taskAttributes.getNamedItem("id").getNodeValue();

			UserTask userTask = getUserTaskForId(tasks, Long.parseLong(id));

			userTask.setNotes(taskAttributes.getNamedItem("note").getNodeValue());
			userTask.setDone("true".equals(taskAttributes.getNamedItem("done").getNodeValue()));
			if (userTask.isDone()) {
				userTask.setDoneDate(new Date());
			}
			
			pm.makePersistent(userTask);
		}

		pm.close();
	}

	private UserTask getUserTaskForId(List<UserTask> tasks, long id) {
		for (UserTask task : tasks) {
			if (id == task.getId()) {
				return task;
			}
		}

		return null;
	}
}
