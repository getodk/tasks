package org.odk.tasker.dao;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class PhoneUser {
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	
	@Persistent
	private String name = " ";

	@Persistent
	private String IMEI;

	@Persistent
	private String SIM;
	
	@Persistent
	private String userId;
	
	@Persistent
	private String phoneNumber;
	
	@Persistent
	private String IMSI;
	
	@Persistent
	private String location = " ";

	@Persistent
	private boolean available;

	@Persistent
	private boolean active;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIMEI() {
		return IMEI;
	}

	public void setIMEI(String imei) {
		IMEI = imei;
	}

	public void setSIM(String sIM) {
		SIM = sIM;
	}

	public String getSIM() {
		return SIM;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setIMSI(String iMSI) {
		IMSI = iMSI;
	}

	public String getIMSI() {
		return IMSI;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocation() {
		return location;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean inactive) {
		this.active = inactive;
	}
}

