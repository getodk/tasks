package org.odk.tasker.dao;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import org.odk.tasker.IConstants;
import org.odk.tasker.UserManagerServlet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;

public class DAOUtil {

	private static final Logger LOG = Logger.getLogger(UserManagerServlet.class.getName());
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	private static final String NO_DATE = "";

	@SuppressWarnings("unchecked")
	public static List<PhoneUser> getPhoneUsers() {
		List<PhoneUser> users = new ArrayList<PhoneUser>();

		PersistenceManager pm = PMF.getInstance().getPersistenceManager();

		javax.jdo.Query pmQuery = pm.newQuery(PhoneUser.class);

		List<PhoneUser> dbUsers = (List<PhoneUser>) pmQuery.execute();

		for (PhoneUser user : dbUsers) {
			users.add(user);
		}

		pm.close();

		return users;
	}

	@SuppressWarnings("unchecked")
	public static PhoneUser loadPhoneUserBySIM(String sim) {
		PersistenceManager pm = PMF.getInstance().getPersistenceManager();

		javax.jdo.Query pmQuery = pm.newQuery(PhoneUser.class);
		pmQuery.setFilter("SIM == requestedSim");
		pmQuery.declareParameters("String requestedSim");

		List<PhoneUser> results = (List<PhoneUser>) pmQuery.execute(sim);

		PhoneUser user;

		if (results.size() > 0) {
			user = results.get(0);
		} else {
			user = new PhoneUser();
			user.setSIM(sim);
		}

		pm.close();

		return user;
	}

	public static List<ArchivedUserTask> getArchivedTasks(int startingIndex, int span) {
		List<ArchivedUserTask> tasks = new ArrayList<ArchivedUserTask>();

		DatastoreService dss = DatastoreServiceFactory.getDatastoreService();
		List<Entity> entityList = dss.prepare(new Query(ArchivedUserTask.class.getSimpleName())).asList(
				withLimit(span).offset(startingIndex));

		for (Entity entity : entityList) {
			tasks.add(createUserTaskFromEntity(entity));
		}

		return tasks;
	}

	private static ArchivedUserTask createUserTaskFromEntity(Entity entity) {
		ArchivedUserTask task = new ArchivedUserTask(null, null);
		task.setId((Long) entity.getProperty("id"));
		task.setUserId((Long) entity.getProperty("userId"));
		task.setTask((String) entity.getProperty("task"));
		task.setNotes((String) entity.getProperty("notes"));
		task.setDueDate((Date) entity.getProperty("dueDate"));
		task.setAlertDate((Date) entity.getProperty("alertDate"));
		task.setAssignDate((Date) entity.getProperty("assignDate"));
		task.setDoneDate((Date) entity.getProperty("doneDate"));
		task.setDone((Boolean) entity.getProperty("done"));
		task.setFormTask((Boolean) entity.getProperty("formTask"));
		task.setUserName((String) entity.getProperty("userName"));
		task.setUserLocation((String) entity.getProperty("userLocation"));

		return task;
	}

	@SuppressWarnings("unchecked")
	public static List<UserTask> getTasksForUser(Long id) {
		List<UserTask> tasks = new ArrayList<UserTask>();

		PersistenceManager pm = PMF.getInstance().getPersistenceManager();

		javax.jdo.Query pmQuery = pm.newQuery(UserTask.class, "userId == userIdParam");
		pmQuery.declareParameters("Long userIdParam");

		List<UserTask> dbTasks = (List<UserTask>) pmQuery.execute(id);

		for (UserTask task : dbTasks) {
			tasks.add(task);
		}

		pm.close();

		return tasks;
	}

	public static Date getDateFromString(String stringDate) {
		Date date = null;

		if (!(stringDate == null || stringDate.isEmpty() || NO_DATE.equals(stringDate))) {

			try {
				date = DATE_FORMAT.parse(stringDate);
			} catch (ParseException e) {
				LOG.info("DAOUtil date parse error" + e.toString());
			}
		}

		return date;
	}

	public static String getDisplayForDate(Date date) {
		return date == null ? NO_DATE : DATE_FORMAT.format(date);
	}

	public static void buildTaskXml(Document doc, String sim) {
		PhoneUser user = loadPhoneUserBySIM(sim);

		if (user != null) {
			Element root = doc.getDocumentElement();

			List<UserTask> tasks = getTasksForUser(user.getId());

			for (UserTask task : tasks) {
				if (!task.isDone()) {
					Element e = doc.createElement("task");
					e.setAttribute("type", IConstants.XML_TODO_TASK);
					e.setAttribute("id", Long.toString(task.getId()));
					e.setAttribute("description", task.getTask());
					e.setAttribute("userNote", task.getNotes());
					e.setAttribute("dueDate", getSafeTime(task.getDueDate()));
					e.setAttribute("alarmDate", getSafeTime(task.getAlertDate()));

					root.appendChild(e);
				}
			}
		}
	}

	private static String getSafeTime(Date date) {
		long time = 0L;
		
		if( date != null ){
			time = date.getTime();
		}
		
		return Long.toString(time);
	}

}
