package org.odk.task.android.activity;

import org.odk.task.android.Constants;
import org.odk.task.android.R;
import org.odk.task.android.Utils;
import org.odk.task.android.model.DbAdapter;
import org.odk.task.android.model.Task;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class EditTaskActivity extends Activity {

	private Long mRowId;
	private DbAdapter mDbAdapter;
	private EditText mNotes;
	private CheckBox mDoneCheck;
	private Task mTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.edit_task);

		mRowId = savedInstanceState == null ? null : savedInstanceState.getLong(DbAdapter.KEY_TASKS_ID);

		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras == null ? null : extras.getLong(DbAdapter.KEY_TASKS_ID);
		}

		loadEditView();
		loadSaveListener();
		loadCancelListenr();
	}

	private void loadEditView() {

		mDbAdapter = new DbAdapter(this, Constants.DB_NAME);
		mDbAdapter.open();

		mTask = mDbAdapter.getTask(Long.toString(mRowId));

		if (mTask != null) {
			TextView description = (TextView) findViewById(R.id.EditDescription);
			description.setText(mTask.getDescription());

			mNotes = (EditText) findViewById(R.id.EditNotes);
			mNotes.setText(mTask.getUserNote());

			TextView alarmDate = (TextView) findViewById(R.id.AlertDate);
			
			alarmDate.setText(Utils.getShortDateForTime(mTask.getDueDate()));

			TextView dueDate = (TextView) findViewById(R.id.DueDate);
			dueDate.setText(Utils.getShortDateForTime(mTask.getDueDate()));

			mDoneCheck = (CheckBox) findViewById(R.id.TaskDone);
			mDoneCheck.setChecked(mTask.isDone());
		}

		mDbAdapter.close();
	}

	private void loadSaveListener() {
		Button saveButton = (Button) findViewById(R.id.task_done_button);

		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				persistChanges();
				finish();
			}

		});
	}

	private void loadCancelListenr() {
		Button cancelButton = (Button)findViewById(R.id.task_cancel_button);
		
		cancelButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				finish();
			}			
		});
	}
	
	private void persistChanges() {
		boolean isModified = isTaskModified();

		if (isModified) {
			mTask.setUserNote(mNotes.getEditableText().toString());
			mTask.setDone(mDoneCheck.isChecked());
			//N.B. once a task is marked modifed it stays modified, 
			//that is why it is never set to false.
			mTask.setModified(true);

			mDbAdapter.open();

			mDbAdapter.updateRecord(mTask);

			mDbAdapter.close();
		}
	}

	protected boolean isTaskModified() {
		return !mNotes.getEditableText().toString().equals(mTask.getUserNote())
				|| mTask.isDone() != mDoneCheck.isChecked();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mDbAdapter != null) {
			mDbAdapter.close();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putLong(DbAdapter.KEY_TASKS_ID, mRowId);

		super.onSaveInstanceState(savedInstanceState);
	}
}
