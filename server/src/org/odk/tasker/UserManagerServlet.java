package org.odk.tasker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.odk.tasker.dao.DAOUtil;
import org.odk.tasker.dao.PMF;
import org.odk.tasker.dao.PhoneUser;


@SuppressWarnings("serial")
public class UserManagerServlet extends HttpServlet {

	private static final Logger LOG = Logger.getLogger(UserManagerServlet.class.getName());
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		List<PhoneUser> users = getPhoneUsersFromRequest( request );

		persistUsers( users );

		doGet( request, response);
	}

	private List<PhoneUser> getPhoneUsersFromRequest(HttpServletRequest request) {
		List<PhoneUser> users = new ArrayList<PhoneUser>();
		int index = 0;
		
		PhoneUser user = getPhoneUser( index, request );
		
		while( user != null){
			users.add(user);
			index++;
			user = getPhoneUser( index, request );
		}
		
		return users;
	}

	private PhoneUser getPhoneUser(int index, HttpServletRequest request) {
		PhoneUser user = null;
		
		String id = request.getParameter(IConstants.NAME_ID + index);
		
		if( id != null ){
			user = new PhoneUser();
			user.setId(Long.decode(id));
			user.setName(request.getParameter(IConstants.NAME_NAME + index));
			user.setLocation(request.getParameter(IConstants.NAME_LOCATION + index));
			user.setIMEI(request.getParameter(IConstants.NAME_IMEI + index));
			user.setIMSI(request.getParameter(IConstants.NAME_IMSI + index));
			user.setSIM(request.getParameter(IConstants.NAME_SIM + index));
			user.setPhoneNumber(request.getParameter(IConstants.NAME_USER_PHONE_NUMBER + index));
			user.setUserId(request.getParameter(IConstants.NAME_USER_USERID + index));
			user.setActive(request.getParameter(IConstants.NAME_ACTIVE + index) != null);
			user.setAvailable(request.getParameter(IConstants.NAME_AVAILABLE + index) != null);
		}
		return user;
	}

	private void persistUsers(List<PhoneUser> users) {
		
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();
		
		//N.B. use PersistentAll
		for( PhoneUser user : users ){
			pm.makePersistent(user);
		}

		
		pm.close();
		
	}

	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		List<PhoneUser> phoneUsers = DAOUtil.getPhoneUsers();
		
		request.setAttribute(IConstants.SERVLET_PHONE_USERS, phoneUsers);
		
		try {
			getServletConfig().getServletContext().getRequestDispatcher("/userManage.jsp").forward(request, response);
		} catch (ServletException e) {
			LOG.severe("Error forwarding request " + e.toString());
		}
	}
}
