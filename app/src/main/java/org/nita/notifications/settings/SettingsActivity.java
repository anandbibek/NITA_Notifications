package org.nita.notifications.settings;


import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import org.nita.notifications.R;

import java.util.List;

public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }


    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            // setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            // bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }

        /*@Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View layout = inflater.inflate(R.layout.activity_settings, container, false);
            if (layout != null) {
                AppCompatPreferenceActivity activity = (AppCompatPreferenceActivity) getActivity();
                Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolbar);
                activity.setSupportActionBar(toolbar);

                ActionBar bar = activity.getSupportActionBar();
                bar.setHomeButtonEnabled(true);
                bar.setDisplayHomeAsUpEnabled(true);
                bar.setTitle(getPreferenceScreen().getTitle());
            }
            return layout;
        }*/
    }
}
