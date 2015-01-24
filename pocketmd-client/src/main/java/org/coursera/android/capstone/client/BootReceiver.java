package org.coursera.android.capstone.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import org.coursera.android.capstone.client.alarm.AlarmScheduler;
import org.coursera.android.capstone.client.infrastructure.PreferencesKeys;
import org.coursera.android.capstone.client.infrastructure.PropertiesManager;
import org.coursera.android.capstone.client.model.Patient;

import java.util.Collections;
import java.util.Set;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = BootReceiver.class.getSimpleName();

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (!"android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            return;
        }

        Log.i(TAG, "BOOT_COMPLETED received");

        final SharedPreferences preferences = context.getSharedPreferences(
                PreferencesKeys.APP_KEY,
                Context.MODE_PRIVATE);

        final String server = PropertiesManager.getProperty(
                context,
                PropertiesManager.APP_SERVER);
        final String username = preferences.getString(PreferencesKeys.USER_USERNAME, null);
        final String password = preferences.getString(PreferencesKeys.USER_PASSWORD, null);
        final String gcmRegistrationId = preferences.getString(PreferencesKeys.REGISTRATION_ID, null);
        final String projectNumber = context.getString(R.string.project_number);

        if (username == null || password == null) {
            return;
        }

        final SharedPreferences userPreferences = context.getSharedPreferences(
                PreferencesKeys.APP_KEY + username,
                Context.MODE_PRIVATE);

        final Set<String> checkinTimes = userPreferences.getStringSet(
                PreferencesKeys.SETTINGS_CHECKIN_TIMES,
                Collections.<String>emptySet());
        if (!checkinTimes.isEmpty()) {
            new AlarmScheduleTask(
                    context,
                    server,
                    username,
                    password,
                    projectNumber,
                    gcmRegistrationId)
            .execute();
        }
    }

    private class AlarmScheduleTask extends LoginTask {

        public AlarmScheduleTask(
                final Context context,
                final String server,
                final String username,
                final String password,
                final String projectNumber,
                final String gcmRegistrationId) {
            super(context, server, username, password, projectNumber, gcmRegistrationId);
        }

        @Override
        protected Boolean doInBackground(final Void... params) {
            final Boolean result = super.doInBackground(params);

            if (result && currentUser instanceof Patient) {
//                AlarmScheduler.getInstance().scheduleCheckinAlarms(context, currentUser);
            }

            return result;
        }
    }
}