package org.coursera.android.capstone.client.ui.doctor;

import org.coursera.android.capstone.client.R;
import org.coursera.android.capstone.client.model.Patient;
import org.coursera.android.capstone.client.ui.UserActivity;
import org.joda.time.format.ISODateTimeFormat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class DoctorPatientDetailsActivity extends UserActivity {

    public static final String PATIENT_INTENT_TOKEN = "PATIENT";

    @InjectView(R.id.full_name_content)
    TextView fullNameContent;
    @InjectView(R.id.medical_number_content)
    TextView medicalNumberContent;
    @InjectView(R.id.dob_content)
    TextView dobContent;

    private Patient patient;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_patient_details);
        ButterKnife.inject(this);

        patient = getIntent().getParcelableExtra(PATIENT_INTENT_TOKEN);

        fullNameContent.setText(patient.getFullName());
        medicalNumberContent.setText(String.valueOf(patient.getRecordNumber()));
        dobContent.setText(ISODateTimeFormat.date().print(patient.getDateOfBirth().getTime()));
    }

    @OnClick(R.id.checkins_button)
    public void openPatientCheckins() {
        final Intent patientCheckinsIntent = new Intent(this, DoctorPatientCheckinsListActivity.class);
        patientCheckinsIntent.putExtra(DoctorPatientCheckinsListActivity.PATIENT_INTENT_TOKEN, patient);
        patientCheckinsIntent.putExtra(USER_INTENT_TOKEN, currentUser);
        startActivity(patientCheckinsIntent);
    }

    @OnClick(R.id.prescriptions_button)
    public void openPatientPrescriptions() {
        final Intent patientPrescriptionsIntent = new Intent(this, DoctorPatientPrescriptionsActivity.class);
        patientPrescriptionsIntent.putExtra(DoctorPatientPrescriptionsActivity.PATIENT_INTENT_TOKEN, patient);
        patientPrescriptionsIntent.putExtra(USER_INTENT_TOKEN, currentUser);
        startActivity(patientPrescriptionsIntent);
    }
}
