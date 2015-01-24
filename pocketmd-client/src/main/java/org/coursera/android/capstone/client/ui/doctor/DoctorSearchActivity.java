package org.coursera.android.capstone.client.ui.doctor;

import org.coursera.android.capstone.client.PocketMdClientApplication;
import org.coursera.android.capstone.client.R;
import org.coursera.android.capstone.client.model.Patient;
import org.coursera.android.capstone.client.service.PocketMdServiceApi;
import org.coursera.android.capstone.client.ui.UserActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class DoctorSearchActivity extends UserActivity {

	private static final String TAG = DoctorSearchActivity.class.getName();
	
	@InjectView(R.id.search_field)
	EditText searchField;
	
	@InjectView(R.id.search_button)
	Button searchButton;
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_doctor_search);
		ButterKnife.inject(this);
	}
	
	@OnClick(R.id.search_button)
	public void submitSearch() {
		final String patientName = searchField.getText().toString();
		
		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(patientName)) {
			searchField.setError(getString(R.string.error_field_required));
			focusView = searchField;
			cancel = true;
		}
		
		if (cancel) {
			focusView.requestFocus();
		} else {
			showProgress(true);
			new SearchTask(patientName).execute();
		}
	}
	
	private class SearchTask extends AsyncTask<Void, Void, Boolean> {

		private final String patientName;
		
		private String errorMessage;

		public SearchTask(final String patientName) {
			this.patientName = patientName;
		}

		@Override
		protected Boolean doInBackground(final Void... params) {

			final PocketMdServiceApi service = ((PocketMdClientApplication) getApplication()).getPocketMdService();
			
			try {
				final Patient patient = service.findPatientByName(patientName);
				if (patient != null) {
                    final Intent patientViewIntent = new Intent(DoctorSearchActivity.this, DoctorPatientDetailsActivity.class);
                    patientViewIntent.putExtra(DoctorPatientDetailsActivity.PATIENT_INTENT_TOKEN, patient);
                    patientViewIntent.putExtra(USER_INTENT_TOKEN, currentUser);
                    startActivity(patientViewIntent);
                    return true;
				} else {
					errorMessage = getString(R.string.error_patient_is_not_found);
					return false;
				}
			} catch (final Exception e) {
				Log.e(TAG, "Cannot establish connection to the server", e);
				errorMessage = getString(R.string.error_patient_search_failed);
				return false;
			}
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			showProgress(false);

			if (success) {
				errorMessage = null;
				finish();
			} else {
				searchField
						.setError(errorMessage);
				searchField.requestFocus();
			}
		}
	}
}
