package org.coursera.android.capstone.client.ui.doctor;

import java.util.Date;
import java.util.List;

import org.coursera.android.capstone.client.R;
import org.coursera.android.capstone.client.model.CheckIn;
import org.coursera.android.capstone.client.model.MedicationIntake;
import org.coursera.android.capstone.client.ui.UserActivity;
import org.joda.time.format.ISODateTimeFormat;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.google.common.collect.Ordering;

public class DoctorPatientCheckinDetailsActivity extends UserActivity {

    public static final String CHECKIN_INTENT_TOKEN = "CHECKIN";

    @InjectView(R.id.checkin_date_content)
    TextView checkinDateContent;
    @InjectView(R.id.checkin_time_content)
    TextView checkinTimeContent;
    @InjectView(R.id.checkin_pain_severity_content)
    TextView checkinPainSeverityContent;
    @InjectView(R.id.checkin_eating_problems_content)
    TextView checkinEatingProblemsContent;
    @InjectView(R.id.checkin_details_medications_list)
    ListView checkinMedicationsList;

    private CheckIn checkIn;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_patient_checkin_details);
        ButterKnife.inject(this);

        checkIn = getIntent().getParcelableExtra(CHECKIN_INTENT_TOKEN);

        checkinDateContent.setText(ISODateTimeFormat.date().print(checkIn.getDateTime().getTime()));
        checkinTimeContent.setText(ISODateTimeFormat.hourMinute().print(checkIn.getDateTime().getTime()));
        checkinPainSeverityContent.setText(checkIn.getPainSeverity().getLabel(this));
        checkinEatingProblemsContent.setText(checkIn.getEatingProblems().getLabel(this));

        final List<MedicationIntake> medicationIntakesSorted = Ordering.from(new MedicationIntake.MedicationIntakeComparator()).sortedCopy(checkIn.getMedications().values());
        checkinMedicationsList.setAdapter(new MedicationsListAdapter(this, medicationIntakesSorted));
    }

    static class MedicationsListAdapter extends ArrayAdapter<MedicationIntake> {

        public MedicationsListAdapter(final Context context, final List<MedicationIntake> values) {
            super(context, R.layout.list_doctor_patient_checkin_details_medication_row, values);
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            final MedicationIntake medicationIntake = getItem(position);
            final Date intakeTime = medicationIntake.getIntakeTime();

            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.list_doctor_patient_checkin_details_medication_row, parent, false);
                final ViewHolder viewHolder = new ViewHolder();
                viewHolder.medicationNameLabel = (TextView) view.findViewById(R.id.medication_list_row_name_label);
                viewHolder.medicationTakenCheckBox = (CheckBox) view.findViewById(R.id.medication_list_row_taken_checkbox);
                viewHolder.medicationIntakeDateTimeLabel = (TextView) view.findViewById(R.id.medication_list_row_intake_time_label);
                view.setTag(viewHolder);
            }

            final ViewHolder holder = (ViewHolder) view.getTag();
            holder.medicationNameLabel.setText(medicationIntake.getMedicationName());
            holder.medicationTakenCheckBox.setChecked(medicationIntake.isMedicationTaken());

            final String intakeTimeString;
            if (intakeTime == null) {
                intakeTimeString = "";
            } else {
                intakeTimeString = String.format("%s %s",
                                        ISODateTimeFormat.date().print(intakeTime.getTime()),
                                        ISODateTimeFormat.hourMinute().print(intakeTime.getTime()));
            }
            holder.medicationIntakeDateTimeLabel.setText(intakeTimeString);

            return view;
        }

        static class ViewHolder {
            protected TextView medicationNameLabel;
            protected CheckBox medicationTakenCheckBox;
            protected TextView medicationIntakeDateTimeLabel;
        }
    }
}
