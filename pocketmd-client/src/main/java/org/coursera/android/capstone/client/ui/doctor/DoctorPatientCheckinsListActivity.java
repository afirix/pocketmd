package org.coursera.android.capstone.client.ui.doctor;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.coursera.android.capstone.client.PocketMdClientApplication;
import org.coursera.android.capstone.client.R;
import org.coursera.android.capstone.client.model.CheckIn;
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

public class DoctorPatientCheckinsListActivity extends UserActivity {

    private static final String TAG = DoctorPatientCheckinsListActivity.class.getSimpleName();

    public static final String PATIENT_INTENT_TOKEN = "PATIENT";

    private Patient patient;

    @InjectView(R.id.checkins_list)
    ListView checkinsList;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_patient_checkins_list);
        ButterKnife.inject(this);

        patient = getIntent().getParcelableExtra(PATIENT_INTENT_TOKEN);

        setTitle(String.format(getString(R.string.title_activity_patient_check_ins), patient.getFullName()));

        showProgress(true);
        new RefreshPatientTask(patient.getId()).execute();
    }

    @OnItemClick(R.id.checkins_list)
    public void onCheckInSelect(final int position) {
        final CheckIn selectedCheckIn = (CheckIn) checkinsList.getItemAtPosition(position);

        final Intent checkinViewIntent = new Intent(this, DoctorPatientCheckinDetailsActivity.class);
        checkinViewIntent.putExtra(DoctorPatientCheckinDetailsActivity.CHECKIN_INTENT_TOKEN, selectedCheckIn);
        checkinViewIntent.putExtra(USER_INTENT_TOKEN, currentUser);
        startActivity(checkinViewIntent);
    }

    static class CheckInsListAdapter extends ArrayAdapter<CheckIn> {

        public CheckInsListAdapter(final Context context, final List<CheckIn> values) {
            super(context, R.layout.list_doctor_patients_row, values);
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            final CheckIn checkIn = getItem(position);
            final Date checkInTime = checkIn.getDateTime();

            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.list_doctor_patient_checkins_row, parent, false);
                final ViewHolder viewHolder = new ViewHolder();
                viewHolder.checkInDateTimeLabel = (TextView) view.findViewById(R.id.checkins_list_row_label);
                view.setTag(viewHolder);
            }

            final ViewHolder holder = (ViewHolder) view.getTag();
            holder.checkInDateTimeLabel.setText(String.format("%tF %tR", checkInTime, checkInTime));

            return view;
        }

        static class ViewHolder {
            protected TextView checkInDateTimeLabel;
        }
    }

    private class RefreshPatientTask extends AsyncTask<Void, Void, Boolean> {

        private long patientId;

        public RefreshPatientTask(final long patientId) {
            this.patientId = patientId;
        }

        @Override
        protected Boolean doInBackground(final Void... params) {

            final PocketMdServiceApi service = ((PocketMdClientApplication) getApplication()).getPocketMdService();

            try {
                final Collection<CheckIn> checkIns = service.getCheckInsForPatient(patientId);
                if (checkIns != null) {
                    final List<CheckIn> checkInsSorted = Ordering.from(new CheckIn.TimeComparator()).sortedCopy(checkIns);
                    checkinsList.setAdapter(new CheckInsListAdapter(DoctorPatientCheckinsListActivity.this, checkInsSorted));
                    return true;
                }

                return false;
            } catch (final Exception e) {
                Log.e(TAG, "Cannot establish connection to the server", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            if (!success) {
                showInformationDialog(getString(R.string.error_cannot_find_patient_data));
            }
        }
    }
}
