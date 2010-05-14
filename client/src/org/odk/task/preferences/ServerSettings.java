package org.odk.task.preferences;


import org.odk.task.android.R;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.os.Bundle;




public class ServerSettings extends PreferenceActivity  implements OnSharedPreferenceChangeListener{

	

    public static String KEY_SERVER = "server";
    public static String KEY_USERNAME = "username";
    public static String KEY_LOCATION = "location";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.server_settings);
        setTitle(getString(R.string.app_name) + " > " + getString(R.string.server_settings));
        updateServer();
        updateUsername();
        updateLocation();
    }


    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(
                this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }


    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_SERVER)) {
            updateServer();
        } else if (key.equals(KEY_USERNAME)) {
            updateUsername();
        } else if (key.equals(KEY_LOCATION)) {
            updateLocation();
        }
    }


    private void updateServer() {
        EditTextPreference etp =
                (EditTextPreference) this.getPreferenceScreen().findPreference(KEY_SERVER);
        String s = etp.getText();
        if (s.endsWith("/")) {
            s = s.substring(0, s.lastIndexOf("/"));
        }
        etp.setSummary(s);
    }


    private void updateUsername() {
        EditTextPreference etp =
                (EditTextPreference) this.getPreferenceScreen().findPreference(KEY_USERNAME);
        etp.setSummary(etp.getText());
    }


    private void updateLocation() {
        EditTextPreference etp =
                (EditTextPreference) this.getPreferenceScreen().findPreference(KEY_LOCATION);
        etp.setSummary(etp.getText());
    }

}
