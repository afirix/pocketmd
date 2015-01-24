package org.coursera.android.capstone.client.ui;

import org.coursera.android.capstone.client.PocketMdClientApplication;
import org.coursera.android.capstone.client.R;
import org.coursera.android.capstone.client.alarm.AlarmScheduler;
import org.coursera.android.capstone.client.infrastructure.PreferencesKeys;
import org.coursera.android.capstone.client.model.Person;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public abstract class UserActivity extends BaseActivity {

    public static final String USER_INTENT_TOKEN = "USER";

    protected SharedPreferences preferences;
    protected SharedPreferences userPreferences;

    protected Person currentUser;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentUser = getIntent().getParcelableExtra(USER_INTENT_TOKEN);

        preferences = getSharedPreferences(PreferencesKeys.APP_KEY, Context.MODE_PRIVATE);
        userPreferences = getSharedPreferences(PreferencesKeys.APP_KEY + currentUser.getUsername(), Context.MODE_PRIVATE);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_doctor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_signout:
                signOut();
                return true;
            case R.id.action_exit:
                promptExit(new Runnable() {
                    @Override
                    public void run() {
                        final Intent exitIntent = new Intent(
                                UserActivity.this, LoginActivity.class);
                        exitIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        exitIntent.putExtra(LoginActivity.EXIT_INTENT_TOKEN, true);
                        startActivity(exitIntent);
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signOut() {
        AlarmScheduler.getInstance().cancelCheckinAlarm(getApplicationContext());

        preferences.edit().remove(PreferencesKeys.USER_ID)
                .remove(PreferencesKeys.USER_USERNAME)
                .remove(PreferencesKeys.USER_PASSWORD)
                .remove(PreferencesKeys.USER_FULLNAME)
                .remove(PreferencesKeys.REGISTRATION_ID)
                .remove(PreferencesKeys.SETTINGS_CHECKIN_TIMES)
                .apply();

        ((PocketMdClientApplication) getApplication())
                .setPocketMdService(null);

        final Intent signoutIntent = new Intent(UserActivity.this,
                LoginActivity.class);
        signoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(signoutIntent);
        finish();
    }

    protected void promptExit(final Runnable actionOnConfirm) {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.prompt_exit))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                actionOnConfirm.run();
                            }
                        })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    protected void showInformationDialog(final String message) {
        showInformationDialog(message, null);
    }

    protected void showInformationDialog(final String message, final Runnable callback) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setCancelable(false)
                .setNeutralButton(getString(R.string.ok), new OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        if (callback != null) {
                            callback.run();
                        }
                    }
                })
                .show();
    }
}
