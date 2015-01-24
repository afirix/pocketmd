package org.coursera.android.capstone.client.ui.doctor;

import java.util.Collection;
import java.util.List;

import org.coursera.android.capstone.client.PocketMdClientApplication;
import org.coursera.android.capstone.client.R;
import org.coursera.android.capstone.client.infrastructure.PreferencesKeys;
import org.coursera.android.capstone.client.model.Patient;
import org.coursera.android.capstone.client.service.PocketMdServiceApi;
import org.coursera.android.capstone.client.ui.UserActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

import com.google.common.collect.Ordering;

public class DoctorListActivity extends UserActivity {
	
	private static final String TAG = DoctorListActivity.class.getSimpleName();

    @InjectView(R.id.patients_list)
    ListView patientsList;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_doctor_list);
		ButterKnife.inject(this);
		
		final long doctorId = preferences.getLong(PreferencesKeys.USER_ID, -1L);
        showProgress(true);
		new ListTask(doctorId).execute();
	}

    @OnItemClick(R.id.patients_list)
    public void onPatientSelect(final int position) {
        final Patient selectedPatient = (Patient) patientsList.getItemAtPosition(position);

        final Intent patientViewIntent = new Intent(this, DoctorPatientDetailsActivity.class);
        patientViewIntent.putExtra(DoctorPatientDetailsActivity.PATIENT_INTENT_TOKEN, selectedPatient);
        patientViewIntent.putExtra(USER_INTENT_TOKEN, currentUser);
        startActivity(patientViewIntent);
    }
	
	private class ListTask extends AsyncTask<Void, Void, Boolean> {

		private final long doctorId;
		
		private String errorMessage;

		public ListTask(final long doctorId) {
			this.doctorId = doctorId;
		}

		@Override
		protected Boolean doInBackground(final Void... params) {

			final PocketMdServiceApi service = ((PocketMdClientApplication) getApplication()).getPocketMdService();
			
			try {
				final Collection<Patient> patients = service.getPatients(doctorId);
				if (patients.isEmpty()) {
					errorMessage = getString(R.string.error_no_patients_found);
					return false;
				} else {
                    final List<Patient> patientsSorted = Ordering.from(new Patient.FullNameComparator()).sortedCopy(patients);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							patientsList.setAdapter(new PatientsListAdapter(DoctorListActivity.this, patientsSorted));
						}
					});
					return true;
				}
			} catch (final Exception e) {
				Log.e(TAG, "Cannot establish connection to the server", e);
				errorMessage = getString(R.string.error_patients_list_failed);
				return false;
			}
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			showProgress(false);

			if (!success) {
                showInformationDialog(errorMessage);
			}
		}
	}

    static class PatientsListAdapter extends ArrayAdapter<Patient> {

        public PatientsListAdapter(final Context context, final List<Patient> values) {
            super(context, R.layout.list_doctor_patients_row, values);
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            final Patient patient = getItem(position);

            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.list_doctor_patients_row, parent, false);
                final ViewHolder viewHolder = new ViewHolder();
                viewHolder.patientNameLabel = (TextView) view.findViewById(R.id.patients_list_row_label);
                view.setTag(viewHolder);
            }

            final ViewHolder holder = (ViewHolder) view.getTag();
            holder.patientNameLabel.setText(patient.getFullName());

            return view;
        }

        static class ViewHolder {
            protected TextView patientNameLabel;
        }
    }
}
