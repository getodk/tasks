package org.odk.tasker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.odk.tasker.dao.ArchivedUserTask;
import org.odk.tasker.dao.DAOUtil;
import org.odk.tasker.dao.PMF;
import org.odk.tasker.dao.PhoneUser;
import org.odk.tasker.dao.UserTask;

@SuppressWarnings("serial")
public class UploadManagerServlet extends HttpServlet {

	private static final Logger LOG = Logger.getLogger(UploadManagerServlet.class.getName());
	// private String mSelectedUserId;
	private PhoneUser mSelectedUser;
	private String errorMsg;
	private UserTask newTask;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		errorMsg = null;
		newTask = null;

		getAndSetUserAttributes(request);

		boolean isAdd = IConstants.VALUE_BUTTON_ADD.equals(request.getParameter(IConstants.NAME_ACTION_BUTTON));

		if (isAdd) {
			addNewTask(request);
		} else {
			updateTasks(request);

			String userTask = request.getParameter(IConstants.NAME_USER_TASK);

			if (userTask != null && !userTask.trim().isEmpty()) {
				addNewTask(request);
			}
		}

		getAndSetTasks(request);

		
		if( errorMsg == null){
			newTask = new UserTask();
		}
		
		request.setAttribute(IConstants.ERROR_MSG, errorMsg);
		request.setAttribute(IConstants.SERVLET_NEW_TASK, newTask);

		try {
			getServletConfig().getServletContext().getRequestDispatcher("/uploadManage.jsp").forward(request, response);
		} catch (ServletException e) {
			LOG.severe("Error forwarding request " + e.toString());
		}
	}

	private void addNewTask(HttpServletRequest request) {
		Date dueDate = DAOUtil.getDateFromString(request.getParameter(IConstants.NAME_DUE_DATE));
		Date alertDate = DAOUtil.getDateFromString(request.getParameter(IConstants.NAME_ALERT_DATE));

		newTask = new UserTask();
		newTask.setUserId(Long.parseLong(request.getParameter(IConstants.NAME_USER_ID)));
		newTask.setAssignDate(new Date());
		newTask.setAlertDate(alertDate);
		newTask.setDueDate(dueDate);
		newTask.setTask(request.getParameter(IConstants.NAME_USER_TASK));
		newTask.setNotes("");
		
		if (datesWellOrdered(dueDate, alertDate)) {
			PersistenceManager pm = PMF.getInstance().getPersistenceManager();

			pm.makePersistent(newTask);

			pm.close();
		}
	}

	private boolean datesWellOrdered(Date dueDate, Date alertDate) {
		/*
		 * The rules are 1. If there is an alarm date there must be a due date
		 * 2. All dates must be greater or equal to now 3. The due due date must
		 * be greater or equal to the alert date
		 */

		long dueTime = dueDate == null ? 0 : dueDate.getTime();
		long alertTime = alertDate == null ? 0 : alertDate.getTime();
		long todayAtMidnight = getTodayAtMidnight();

		StringBuffer errs = null;

		if (alertDate != null && dueDate == null) {
			errs = new StringBuffer();
			errs.append("If there is an alert date there must be a due date");
		}

		if ((alertDate != null && alertTime < todayAtMidnight) || (dueDate != null && dueTime < todayAtMidnight)) {

			if (errs == null) {
				errs = new StringBuffer();
			} else {
				errs.append("\\n");
			}

			errs.append("Both alert date and due date must be in the future");
		}

		if (alertTime > dueTime && dueDate != null) {
			if (errs == null) {
				errs = new StringBuffer();
			} else {
				errs.append("\\n");
			}

			errs.append("Alert date must be before due date");
		}

		if (errs == null) {
			return true;
		} else {
			errorMsg = errs.toString();
			return false;
		}
	}

	private long getTodayAtMidnight() {
		Calendar today = new GregorianCalendar();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		return today.getTimeInMillis();
	}

	private void updateTasks(HttpServletRequest request) {
		List<UserTask> tasks = new ArrayList<UserTask>();
		List<UserTask> archiveTasks = new ArrayList<UserTask>();

		int index = 0;

		UserTask task = getUserTask(index, request);

		while (task != null) {
			boolean archive = request.getParameter(IConstants.NAME_ARCHIVE + index) != null;

			if (archive) {
				archiveTasks.add(task);
			} else {
				tasks.add(task);
			}

			index++;
			task = getUserTask(index, request);
		}

		PersistenceManager pm = PMF.getInstance().getPersistenceManager();

		// N.B. use PersistentAll
		for (UserTask filledTask : tasks) {
			pm.makePersistent(filledTask);
		}

		for (UserTask filledTask : archiveTasks) {
			UserTask dbTask = pm.getObjectById(UserTask.class, filledTask.getId());
			pm.deletePersistent(dbTask);

			ArchivedUserTask archivedTask = new ArchivedUserTask(mSelectedUser, filledTask);
			pm.makePersistent(archivedTask);
		}

		pm.close();
	}

	private UserTask getUserTask(int index, HttpServletRequest request) {
		UserTask task = null;

		String id = request.getParameter(IConstants.NAME_USER_TASK_ID + index);

		if (id != null) {
			task = new UserTask();

			boolean done = request.getParameter(IConstants.NAME_DONE + index) != null;
			Date enteredDoneDate = DAOUtil.getDateFromString(request.getParameter(IConstants.NAME_DONE_DATE + index));

			task.setId(Long.decode(id));
			task.setUserId(Long.parseLong(request.getParameter(IConstants.NAME_USER_ID)));
			task.setAssignDate(DAOUtil.getDateFromString(request.getParameter(IConstants.NAME_ASSIGN_DATE + index)));
			task.setAlertDate(DAOUtil.getDateFromString(request.getParameter(IConstants.NAME_ALERT_DATE + index)));
			task.setDueDate(DAOUtil.getDateFromString(request.getParameter(IConstants.NAME_DUE_DATE + index)));
			if (done) {
				Date effectiveDoneDate = enteredDoneDate == null ? new Date() : enteredDoneDate;
				task.setDoneDate(effectiveDoneDate);
			} else {
				task.setDoneDate(null);
			}
			task.setDone(done);
			task.setTask(request.getParameter(IConstants.NAME_USER_TASK + index));
			task.setNotes(request.getParameter(IConstants.NAME_USER_NOTES + index));
		}

		return task;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		getAndSetUserAttributes(request);
		getAndSetTasks(request);

		request.setAttribute(IConstants.ERROR_MSG, null);
		request.setAttribute(IConstants.SERVLET_NEW_TASK, new UserTask());
		
		try {
			getServletConfig().getServletContext().getRequestDispatcher("/uploadManage.jsp").forward(request, response);
		} catch (ServletException e) {
			LOG.severe("Error forwarding request " + e.toString());
		}
	}

	private void getAndSetUserAttributes(HttpServletRequest request) {
		List<PhoneUser> phoneUsers = DAOUtil.getPhoneUsers();
		request.setAttribute(IConstants.SERVLET_PHONE_USERS, phoneUsers);
		String selectedUserId = request.getParameter(IConstants.NAME_USER_ID);

		if (selectedUserId != null) {
			mSelectedUser = getSelectedUser(phoneUsers, selectedUserId);
			request.setAttribute(IConstants.NAME_USER, mSelectedUser);
		}
	}

	private void getAndSetTasks(HttpServletRequest request) {
		if (mSelectedUser != null) {
			List<UserTask> tasks = getTasksForUser(mSelectedUser);
			request.setAttribute(IConstants.SERVLET_USER_TASKS, tasks);
		}
	}

	private List<UserTask> getTasksForUser(PhoneUser selectedUser) {
		return DAOUtil.getTasksForUser(selectedUser.getId());
	}

	private PhoneUser getSelectedUser(List<PhoneUser> phoneUsers, String selectedUserId) {
		Long userId = Long.parseLong(selectedUserId);
		for (PhoneUser user : phoneUsers) {

			if (user.getId().equals(userId)) {
				return user;
			}
		}

		return null;
	}
}
