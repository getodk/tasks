package org.odk.task.activities;

import org.odk.task.preferences.ServerSettings;

import org.odk.task.android.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TaskClient extends Activity {
	
	  // menu options
	private static final int MENU_PREFERENCES = Menu.FIRST;
	private static final int MENU_REGISTER = MENU_PREFERENCES + 1;
	
	private static Button mViewButton;
	private static Button mSyncButton;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        registerButtons();
        setButtonListeners();
    }
    
    private void registerButtons() {
		mViewButton = (Button)findViewById(R.id.view_tasks);
		mSyncButton = (Button)findViewById(R.id.synchronize_tasks);
	}

	private void setButtonListeners() {
		mViewButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				 Intent i = new Intent(getApplicationContext(), ListTasks.class);
	                startActivity(i);
			
			}
			
		});
		
		mSyncButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
                Intent i = new Intent(getApplicationContext(), SyncTasks.class);
                startActivity(i);
				
			}
			
		});
	}

	private void createPreferencesMenu() {
        Intent i = new Intent(this, ServerSettings.class);
        startActivity(i);
    }

    private void createRegisterMenu() {
        Intent i = new Intent(this, RegisterPhone.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_PREFERENCES, 0, getString(R.string.server_settings)).setIcon(
                android.R.drawable.ic_menu_preferences);
        menu.add(0, MENU_REGISTER, 0, getString(R.string.register_phone)).setIcon(
                android.R.drawable.ic_popup_sync);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_PREFERENCES:
                createPreferencesMenu();
                return true;
            case MENU_REGISTER:
                createRegisterMenu();
                return true;    
        }
        return super.onOptionsItemSelected(item);
    }
}