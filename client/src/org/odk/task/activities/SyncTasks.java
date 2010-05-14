package org.odk.task.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import org.odk.task.android.R;

public class SyncTasks extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getString(R.string.app_name) + " > " + getString(R.string.sync_tasks));

        // get instances to upload
        Intent i = getIntent();
   /*     ArrayList<String> instances = i.getStringArrayListExtra(GlobalConstants.KEY_INSTANCES);
        if (instances == null) {
            // nothing to upload
            return;
        }

        // get the task if we've changed orientations.  If it's null it's a new upload.
        mInstanceUploaderTask = (InstanceUploaderTask) getLastNonConfigurationInstance();
        if (mInstanceUploaderTask == null) {
            // setup dialog and upload task
            showDialog(PROGRESS_DIALOG);
            mInstanceUploaderTask = new InstanceUploaderTask();

            SharedPreferences settings =
                    PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String url =
                    settings.getString(ServerPreferences.KEY_SERVER,
                            getString(R.string.default_server))
                            + "/submission";
            mInstanceUploaderTask.setUploadServer(url);
            totalCount = instances.size();

            // convert array list to an array
            String[] sa = instances.toArray(new String[totalCount]);
            mInstanceUploaderTask.execute(sa);
            */
        }
    }

