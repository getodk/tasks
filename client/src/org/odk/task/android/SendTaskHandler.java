package org.odk.task.android;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.odk.task.android.comm.HttpAdapter;
import org.odk.task.android.model.DbAdapter;
import org.odk.task.android.model.Task;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * This handler is responsible for sending modified tasks to odkServer
 * Manage server.
 * 
 * @author alerer@google.com (Adam Lerer)
 * 
 */
public class SendTaskHandler {

	private Context ctx;
	private PhonePropertiesAdapter propsAdapter;
	private SharedPreferencesAdapter prefsAdapter;

	public SendTaskHandler(Context ctx) {
		this.ctx = ctx;
		propsAdapter = new PhonePropertiesAdapter(ctx);
		prefsAdapter = new SharedPreferencesAdapter(ctx);
	}


	/**
	 * Attempts to register the device with the server.
	 * 
	 * @param returnToast
	 *            If true, a Toast will display with the result of the
	 *            registration.
	 */
	public void send(boolean returnToast) {
			sendByHttp(returnToast);
	}

	
	/**
	 * Attempts to send modified tasks to the server. TODO(alerer): should be
	 * done asynchronously.
	 * 
	 * @param returnToast
	 */
	public void sendByHttp(boolean returnToast) {
		Map<String, String> regMap = createTaskMap();

		Log.d(Constants.TAG, "looking for  URL ");
		if (prefsAdapter.getString(Constants.PREF_URL_KEY, null) != null) {
			Log.d(Constants.TAG, "got  URL ");
			
			String url = prefsAdapter.getString(Constants.PREF_URL_KEY, null) + "/" + Constants.TASK_UPDATE_PATH;
			Log.d(Constants.TAG, "URL " + url);
			// we're not going to update the registered IMSI, because an HTTP
			// registration is not sufficient
			// (it doesn't give the server a # validator)
			boolean success = new HttpAdapter().doPost(url, regMap);
			if (returnToast) {
				if (success) {
					Toast.makeText(ctx, "Registration by HTTP was successful.", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(
							ctx,
							"Registration by HTTP was unsuccessful. "
									+ "Try connecting to wifi or entering data range.", Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	private Map<String, String> createTaskMap() {
		String xml = buildXmlForModifiedTasks();
		
		Map<String, String> taskMap = new HashMap<String, String>();
		newProperty("sim", propsAdapter.getSimSerialNumber(), taskMap);
		newProperty("xml", xml, taskMap);
		return taskMap;
	}

	private String buildXmlForModifiedTasks() {
		DbAdapter dbAdaptor = new DbAdapter( ctx, Constants.DB_NAME );
		dbAdaptor.open();
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		buffer.append("<Tasks>");
		
		List<Task> allTasks = dbAdaptor.getAllTasks();
		Log.d(Constants.TAG, "all tasks: " + allTasks.size());

		for(Task task : allTasks){
			Log.d(Constants.TAG, "modified task? " + task.isModified() );
			if( task.isModified()){
				Log.d(Constants.TAG, "modified task " );
				addTaskToXml( task, buffer);
			}
		}
		
		buffer.append("</Tasks>");
		dbAdaptor.close();
		return buffer.toString();
	}

	private void addTaskToXml(Task task, StringBuffer buffer) {
		buffer.append("<Task id=\"");
		buffer.append( task.getUniqueId());
		buffer.append( "\" note=\"");
		buffer.append( task.getUserNote());
		buffer.append("\" done=\"");
		buffer.append(task.isDone() ? "true" : "false");
		buffer.append("\" />");
	}

	private void newProperty(String name, String value, Map<String, String> paramMap) {
		if (name == null || value == null)
			return;
		Log.d("OdkManage", "New registration property: <" + name + "," + value + ">");
		paramMap.put(name, value);
	}
}
