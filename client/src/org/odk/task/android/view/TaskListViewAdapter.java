package org.odk.task.android.view;

import java.util.Date;
import java.util.List;

import org.odk.task.android.R;
import org.odk.task.android.Utils;
import org.odk.task.android.model.Task;

import android.app.Activity;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TaskListViewAdapter extends BaseAdapter {

	private Activity mContext;
	private List<Task> mTasks;
	
	public TaskListViewAdapter( Activity context,  List<Task> tasks){
		mContext = context;
		mTasks = tasks;
	}
	
	@Override
	public int getCount() {
		return mTasks.size();
	}

	@Override
	public Object getItem(int index) {
		return mTasks.get(index);
	}

	@Override
	public long getItemId(int id) {
		return Long.parseLong(mTasks.get(id).getUniqueId());
	}

	@Override
	public View getView(int index, View arg1, ViewGroup arg2) {
		
		View view = mContext.getLayoutInflater().inflate(R.layout.task_row, null);
		 
		TextView descriptionView = (TextView)view.findViewById(R.id.description);
		TextView dateView = (TextView)view.findViewById(R.id.due_date);
		Task currentTask = mTasks.get(index);
		descriptionView.setText(currentTask.getDescription() + "\t\t\t\t\t.");

		dateView.setText("  " + Utils.getShortDateForTime(currentTask.getDueDate()));
		
		if( !pastAlarmOrDueDate(currentTask)){
			dateView.setTextColor(Resources.getSystem().getColor(android.R.color.primary_text_light));
		}
		
		
		return view;
	}

	private boolean pastAlarmOrDueDate(Task task) {
		long today = new Date().getTime();
		long alarm = task.getAlarmDate() == 0L ? Long.MAX_VALUE : task.getAlarmDate();
		long due = task.getDueDate() == 0L ? Long.MAX_VALUE : task.getDueDate();
		
		return today > alarm || today > due;
	}

}
