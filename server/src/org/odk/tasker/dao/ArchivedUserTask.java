package org.odk.tasker.dao;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ArchivedUserTask extends UserTask {

	@Persistent
	private String userName;

	@Persistent
	private String userLocation;

	public ArchivedUserTask(PhoneUser user, UserTask userTask) {
		if (user != null) {
			setUserName(user.getName());
			setUserLocation(user.getLocation());
		}

		if (userTask != null) {
			setTask(userTask.getTask());
			setNotes( userTask.getNotes());
			setDueDate(userTask.getDueDate());
			setAlertDate(userTask.getAlertDate());
			setAssignDate(userTask.getAssignDate());
			setDoneDate(userTask.getDoneDate());
			setDone(userTask.isDone());
			setFormTask(userTask.isFormTask());
		}
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserLocation() {
		return userLocation;
	}

	public void setUserLocation(String userLocation) {
		this.userLocation = userLocation;
	}
}
