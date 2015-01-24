package org.coursera.android.capstone.client.ui;

import org.apache.commons.lang3.StringUtils;
import org.coursera.android.capstone.client.LoginTask;
import org.coursera.android.capstone.client.PocketMdClientApplication;
import org.coursera.android.capstone.client.R;
import org.coursera.android.capstone.client.infrastructure.PreferencesKeys;
import org.coursera.android.capstone.client.infrastructure.PropertiesManager;
import org.coursera.android.capstone.client.model.Patient;
import org.coursera.android.capstone.client.model.Person;
import org.coursera.android.capstone.client.ui.doctor.DoctorMainActivity;
import org.coursera.android.capstone.client.ui.patient.PatientMainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

	private static final String TAG = LoginActivity.class.getSimpleName();

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final String EXIT_INTENT_TOKEN = "EXIT";

	@InjectView(R.id.username_field)
	EditText usernameField;
	@InjectView(R.id.password_field)
	EditText passwordField;

    private String gcmRegistrationId;

    private String server;
    private String projectNumber;

	private SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ButterKnife.inject(this);

        if (getIntent().getBooleanExtra(EXIT_INTENT_TOKEN, false)) {
            finish();
            return;
        }

        server = PropertiesManager.getProperty(
					this,
					PropertiesManager.APP_SERVER);
        projectNumber = getString(R.string.project_number);

		String username = null;
		String password = null;

		preferences = getSharedPreferences(PreferencesKeys.APP_KEY, Context.MODE_PRIVATE);

		if (preferences.contains(PreferencesKeys.USER_USERNAME)) {
			username = preferences.getString(PreferencesKeys.USER_USERNAME, null);
			password = preferences.getString(PreferencesKeys.USER_PASSWORD, null);
		}

        if (checkPlayServices()) {
            gcmRegistrationId = getRegistrationId();
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }

		if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
			usernameField.setText(username);
			passwordField.setText(password);
			showProgress(true);

			new UserLoginTask(this, server, username, password, projectNumber, gcmRegistrationId).execute();
		} else {
            usernameField.setText("");
            passwordField.setText("");
        }
	}
	
	@Override
	public void onBackPressed() {
		finish();
	}

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
//                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private String getRegistrationId() {
        String registrationId = preferences.getString(PreferencesKeys.REGISTRATION_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = preferences.getInt(PreferencesKeys.APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion();
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private int getAppVersion() {
        try {
            final PackageInfo packageInfo = getApplicationContext().getPackageManager()
                    .getPackageInfo(getApplicationContext().getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (final PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

	@OnClick(R.id.sign_in_button)
	public void attemptLogin() {
		usernameField.setError(null);
		passwordField.setError(null);

		final String username = usernameField.getText().toString();
		final String password = passwordField.getText().toString();

		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(username)) {
			usernameField.setError(getString(R.string.error_field_required));
			focusView = usernameField;
			cancel = true;
		}
		if (TextUtils.isEmpty(password)) {
			passwordField.setError(getString(R.string.error_field_required));
			focusView = passwordField;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
		} else {
			showProgress(true);
			new UserLoginTask(this, server, username, password, projectNumber, gcmRegistrationId).execute();
		}
	}

    private class UserLoginTask extends LoginTask {

        public UserLoginTask(
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
            if (result) {
                ((PocketMdClientApplication) getApplication()).setPocketMdService(service);
                storeCurrentUser(currentUser, password);
                if (StringUtils.isNotBlank(gcmRegistrationId)) {
                    storeRegistrationId(gcmRegistrationId);
                }
                startMainActivity(currentUser);
            }
            return result;
        }

        private void storeCurrentUser(final Person currentUser, final String password) {
            preferences.edit()
                    .putString(PreferencesKeys.USER_USERNAME, currentUser.getUsername())
                    .putString(PreferencesKeys.USER_FULLNAME, currentUser.getFullName())
                    .putString(PreferencesKeys.USER_PASSWORD, password)
                    .putLong(PreferencesKeys.USER_ID, currentUser.getId())
                    .apply();
        }

        private void storeRegistrationId(final String gcmRegistrationId) {
            preferences.edit()
                    .putString(PreferencesKeys.REGISTRATION_ID, gcmRegistrationId)
                    .putInt(PreferencesKeys.APP_VERSION, getAppVersion())
                    .apply();
        }

        private void startMainActivity(final Person currentUser) {
            final Class<? extends Activity> mainActivityClass =
                    (currentUser instanceof Patient)
                            ? PatientMainActivity.class
                            : DoctorMainActivity.class;
            final Intent mainActivityIntent = new Intent(LoginActivity.this, mainActivityClass);
            mainActivityIntent.putExtra(UserActivity.USER_INTENT_TOKEN, currentUser);
            startActivity(mainActivityIntent);
        }

		@Override
		protected void onPostExecute(final Boolean success) {
			showProgress(false);

			if (success) {
				finish();
			} else {
				passwordField
						.setError(getString(R.string.error_login_failed));
				passwordField.requestFocus();
			}
		}
	}
}
