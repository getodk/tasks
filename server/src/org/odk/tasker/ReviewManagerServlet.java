package org.odk.tasker;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.odk.tasker.dao.ArchivedUserTask;
import org.odk.tasker.dao.DAOUtil;


@SuppressWarnings("serial")
public class ReviewManagerServlet extends HttpServlet {
	
	private static final Logger LOG = Logger.getLogger(ReviewManagerServlet.class.getName());
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		
		String index = (String) request.getParameter(IConstants.NAME_PAGE_INDEX);
		Integer startingIndex = index != null ? Integer.valueOf(index) : new Integer(0);
		
		List<ArchivedUserTask> tasks = getTasks( startingIndex );
		request.setAttribute(IConstants.SERVLET_USER_TASKS, tasks);
		
		boolean morePages = tasks.size() > IConstants.PAGE_SIZE;
		request.setAttribute(IConstants.SERVLET_MORE_PAGES, morePages);
		
		if( morePages){
			tasks.remove(tasks.size() - 1);
		}
		
		request.setAttribute(IConstants.SERVLET_STARTING_INDEX, startingIndex );
		
		try {
			getServletConfig().getServletContext().getRequestDispatcher("/reviewManage.jsp").forward(request, response);
		} catch (ServletException e) {
			LOG.severe("Error forwarding request " + e.toString());
		}

	}


	private List<ArchivedUserTask> getTasks(Integer startingIndex) {
		return DAOUtil.getArchivedTasks( startingIndex.intValue(), IConstants.PAGE_SIZE + 1);
	}
}
