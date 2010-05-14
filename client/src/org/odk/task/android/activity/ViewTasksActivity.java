package org.odk.task.android.activity;

import java.util.List;

import org.odk.task.android.Constants;
import org.odk.task.android.R;
import org.odk.task.android.model.DbAdapter;
import org.odk.task.android.model.Task;
import org.odk.task.android.view.TaskListViewAdapter;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemSelectedListener;

public class ViewTasksActivity extends ListActivity {

	private TaskListViewAdapter listAdapter;
	private static final int TASK_EDIT = 0;
	private static final String TAG = "odkTask";
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_tasks);

		setListView();
	}

	private void setListView() {
		DbAdapter dbAdapter = new DbAdapter(this, Constants.DB_NAME);

		dbAdapter.open();

		List<Task> taskList = dbAdapter.getAllTasks();
		
		listAdapter = new TaskListViewAdapter( this, taskList);
		
		setListAdapter(listAdapter);

		dbAdapter.close();
	}


	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Intent intent = new Intent( ViewTasksActivity.this, EditTaskActivity.class);
		intent.putExtra(DbAdapter.KEY_TASKS_ID, id);
		startActivityForResult(intent, TASK_EDIT );

	}


	
}
