package org.odk.task.android;

import java.util.Date;

/**
 * Static utilities for ODK Manage client.
 * 
 * @author alerer@google.com (Adam Lerer)
 * 
 */
public class Utils {
	public static String getDurationString(long ms) {
		long mins = ms / 60000;
		long hrs = mins / 60;
		long days = hrs / 24;
		if (days != 0)
			return days + " days";
		if (hrs != 0)
			return hrs + " hours";
		return mins + " minutes";
	}

	public static String getShortDateForTime(long time) {

		if (time > 0) {
			Date date = new Date();
			date.setTime(time);

			return date.toString();
		} else {
			return "";
		}
	}
}
