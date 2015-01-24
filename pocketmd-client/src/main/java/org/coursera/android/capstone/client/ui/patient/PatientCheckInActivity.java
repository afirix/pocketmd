package org.coursera.android.capstone.client.ui.patient;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

import org.coursera.android.capstone.client.PocketMdClientApplication;
import org.coursera.android.capstone.client.R;
import org.coursera.android.capstone.client.model.CheckIn;
import org.coursera.android.capstone.client.model.EatingProblems;
import org.coursera.android.capstone.client.model.MedicationIntake;
import org.coursera.android.capstone.client.model.PainSeverity;
import org.coursera.android.capstone.client.model.Prescription;
import org.coursera.android.capstone.client.service.PocketMdServiceApi;
import org.coursera.android.capstone.client.ui.UserActivity;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class PatientCheckInActivity extends UserActivity {

    private static final String TAG = PatientCheckInActivity.class.getSimpleName();

    @InjectView(R.id.checkin_medications_list)
    ListView medicationsList;
    @InjectView(R.id.pain_severity_radio_group)
    RadioGroup painSeverityRadioGroup;
    @InjectView(R.id.eating_problems_radio_group)
    RadioGroup eatingProblemsRadioGroup;

    private MedicationsListAdapter medicationsListAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_check_in);
        ButterKnife.inject(this);

        showProgress(true);
        new RefreshPrescriptionsTask(currentUser.getId()).execute();
    }

    @OnClick(R.id.checkin_button)
    public void checkin() {
        final Map<String, MedicationIntake> medicationIntakesMap = getMedicationIntakes();
        if (!validateMedicationIntakes(medicationIntakesMap)) {
            showInformationDialog(getString(R.string.error_medication_intake_validation_failed));
        } else {
            final CheckIn checkIn = new CheckIn.Builder()
                    .withDateTime(new Date())
                    .withPainSeverity(getSelectedPainSeverity())
                    .withEatingProblems(getSelectedEatingProblems())
                    .withMedications(medicationIntakesMap)
                    .build();

            showProgress(true);
            new CheckInTask(currentUser.getId(), checkIn).execute();
        }
    }

    private PainSeverity getSelectedPainSeverity() {
        switch (painSeverityRadioGroup.getCheckedRadioButtonId()) {
            case R.id.pain_severity_well_controlled_radio:
                return PainSeverity.WELL_CONTROLLED;
            case R.id.pain_severity_moderate_radio:
                return PainSeverity.MODERATE;
            case R.id.pain_severity_severe_radio:
                return PainSeverity.SEVERE;
            default:
                assert false;
                return null;
        }
    }

    private EatingProblems getSelectedEatingProblems() {
        switch (eatingProblemsRadioGroup.getCheckedRadioButtonId()) {
            case R.id.eating_problems_no_radio:
                return EatingProblems.NO;
            case R.id.eating_problems_some_radio:
                return EatingProblems.SOME;
            case R.id.eating_problems_cant_eat_radio:
                return EatingProblems.CANT_EAT;
            default:
                assert false;
                return null;
        }
    }

    private Map<String, MedicationIntake> getMedicationIntakes() {
        final List<MedicationIntake> medicationIntakes = medicationsListAdapter.getItems();
        final Map<String, MedicationIntake> medicationIntakesMap = Maps.uniqueIndex(medicationIntakes, new Function<MedicationIntake, String>() {
            @Override
            public String apply(final MedicationIntake input) {
                return input.getMedicationName();
            }
        });
        return medicationIntakesMap;
    }

    private boolean validateMedicationIntakes(final Map<String, MedicationIntake> medicationIntakesMap) {
        for (final MedicationIntake medicationIntake : medicationIntakesMap.values()) {
            if (medicationIntake.isMedicationTaken() && medicationIntake.getIntakeTime() == null) {
                return false;
            }
        }
        return true;
    }

    static class MedicationsListAdapter extends ArrayAdapter<MedicationIntake> {

        private final List<MedicationIntake> medicationIntakes;

        public MedicationsListAdapter(final Context context, final List<MedicationIntake> values) {
            super(context, R.layout.list_patient_checkin_medications_row, values);
            this.medicationIntakes = values;
        }

        public List<MedicationIntake> getItems() {
            return medicationIntakes;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            final MedicationIntake medicationIntake = getItem(position);

            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.list_patient_checkin_medications_row, parent, false);
                final ViewHolder viewHolder = new ViewHolder();
                viewHolder.promptMedicationIntake = (TextView) view.findViewById(R.id.prompt_medication_intake);
                viewHolder.medicationTakenYesRadio = (RadioButton) view.findViewById(R.id.medication_taken_yes_radio);
                viewHolder.medicationTakenYesRadio.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        final boolean isChecked = ((RadioButton) v).isChecked();
                        viewHolder.medicationIntakeDateTimeButton.setEnabled(isChecked);
                        viewHolder.medicationIntakeTimeLabel.setEnabled(isChecked);
                        medicationIntake.setMedicationTaken(isChecked);
                        if (!isChecked) {
                            viewHolder.medicationIntakeTimeLabel.setText("");
                            medicationIntake.setIntakeTime(null);
                        }
                    }
                });
                viewHolder.medicationTakenNoRadio = (RadioButton) view.findViewById(R.id.medication_taken_no_radio);
                viewHolder.medicationTakenNoRadio.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        final boolean isChecked = ((RadioButton) v).isChecked();
                        viewHolder.medicationIntakeDateTimeButton.setEnabled(!isChecked);
                        viewHolder.medicationIntakeTimeLabel.setEnabled(!isChecked);
                        medicationIntake.setMedicationTaken(!isChecked);
                        if (isChecked) {
                            viewHolder.medicationIntakeTimeLabel.setText("");
                            medicationIntake.setIntakeTime(null);
                        }
                    }
                });
                viewHolder.medicationIntakeDateTimeButton = (Button) view.findViewById(R.id.set_medication_intake_time_button);
                viewHolder.medicationIntakeDateTimeButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        getMedicationIntakeTime(
                                new Function<Date, Void>() {
                                    @Override
                                    public Void apply(final Date intakeTime) {
                                        viewHolder.medicationIntakeTimeLabel.setText(formatIntakeDateTime(intakeTime));
                                        medicationIntake.setIntakeTime(intakeTime);
                                        return null;
                                    }
                                }
                        );
                    }
                });
                viewHolder.medicationIntakeTimeLabel = (TextView) view.findViewById(R.id.medication_intake_time_label);
                view.setTag(viewHolder);
            }

            final ViewHolder holder = (ViewHolder) view.getTag();

            final String promptMedicationIntakeText;
            if (getCount() <= 1) {
                promptMedicationIntakeText = getContext().getString(R.string.prompt_medication_intake);
            } else {
                promptMedicationIntakeText =
                        String.format(
                                getContext().getString(R.string.prompt_medication_intake_template),
                                medicationIntake.getMedicationName());
            }
            holder.promptMedicationIntake.setText(promptMedicationIntakeText);

            holder.medicationTakenYesRadio.setChecked(medicationIntake.isMedicationTaken());

            final Date intakeTime = medicationIntake.getIntakeTime();
            final String intakeTimeString = (intakeTime == null) ? "" : formatIntakeDateTime(intakeTime);
            holder.medicationIntakeTimeLabel.setText(intakeTimeString);

            return view;
        }

        private String formatIntakeDateTime(final Date intakeTime) {
            return String.format("%s %s",
                    ISODateTimeFormat.date().print(intakeTime.getTime()),
                    ISODateTimeFormat.hourMinute().print(intakeTime.getTime()));
        }

        public void getMedicationIntakeTime(final Function<Date, Void> callback) {
            final Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(
                getContext(),
                new OnDateSetListener() {
                    @Override
                    public void onDateSet(
                            final DatePicker view,
                            final int year,
                            final int monthOfYear,
                            final int dayOfMonth) {
                        if (!view.isShown()) {
                            return;
                        }

                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        new TimePickerDialog(
                            getContext(),
                            new OnTimeSetListener() {
                                @Override
                                public void onTimeSet(
                                        final TimePicker view,
                                        final int hourOfDay,
                                        final int minute) {
                                    if (!view.isShown()) {
                                        return;
                                    }

                                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    calendar.set(Calendar.MINUTE, minute);

                                    callback.apply(calendar.getTime());
                                }
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE), true)
                        .show();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
            .show();
        }

        static class ViewHolder {
            protected TextView promptMedicationIntake;
            protected RadioButton medicationTakenYesRadio;
            protected RadioButton medicationTakenNoRadio;
            protected Button medicationIntakeDateTimeButton;
            protected TextView medicationIntakeTimeLabel;
        }
    }

    private class CheckInTask extends AsyncTask<Void, Void, Boolean> {

        private final long patientId;
        private final CheckIn checkIn;

        public CheckInTask(final long patientId, final CheckIn checkIn) {
            this.patientId = patientId;
            this.checkIn = checkIn;
        }

        @Override
        protected Boolean doInBackground(final Void... params) {

            final PocketMdServiceApi service = ((PocketMdClientApplication) getApplication()).getPocketMdService();

            try {
                return service.checkin(patientId, checkIn);
            } catch (final Exception e) {
                Log.e(TAG, "Cannot establish connection to the server", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            if (success) {
                showInformationDialog(getString(R.string.checkin_succeeded), new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            } else {
                showInformationDialog(getString(R.string.error_checkin_failed));
            }
        }
    }

    private class RefreshPrescriptionsTask extends AsyncTask<Void, Void, Boolean> {

        private long patientId;

        public RefreshPrescriptionsTask(final long patientId) {
            this.patientId = patientId;
        }

        @Override
        protected Boolean doInBackground(final Void... params) {

            final PocketMdServiceApi service = ((PocketMdClientApplication) getApplication()).getPocketMdService();

            try {
                final Collection<Prescription> prescriptions = service.getPrescriptions(patientId);
                final Collection<MedicationIntake> medicationIntakes = Lists.newArrayListWithExpectedSize(prescriptions.size());
                for (final Prescription prescription : prescriptions) {
                    final boolean medicationTaken = false;
                    final MedicationIntake medicationIntake = new MedicationIntake(medicationTaken);
                    medicationIntake.setMedicationName(prescription.getMedicationName());
                    medicationIntakes.add(medicationIntake);
                }
                final List<MedicationIntake> medicationIntakesList = Ordering.from(new MedicationIntake.MedicationIntakeComparator()).sortedCopy(medicationIntakes);
                medicationsListAdapter = new MedicationsListAdapter(PatientCheckInActivity.this, ImmutableList.copyOf(medicationIntakesList));
                medicationsList.setAdapter(medicationsListAdapter);

                return true;
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
