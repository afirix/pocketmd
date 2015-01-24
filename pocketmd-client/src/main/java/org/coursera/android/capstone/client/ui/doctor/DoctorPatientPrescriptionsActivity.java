package org.coursera.android.capstone.client.ui.doctor;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.coursera.android.capstone.client.PocketMdClientApplication;
import org.coursera.android.capstone.client.R;
import org.coursera.android.capstone.client.model.Patient;
import org.coursera.android.capstone.client.model.Prescription;
import org.coursera.android.capstone.client.service.PocketMdServiceApi;
import org.coursera.android.capstone.client.ui.UserActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;

public class DoctorPatientPrescriptionsActivity extends UserActivity {

    private static final String TAG = DoctorPatientPrescriptionsActivity.class.getSimpleName();

    public static final String PATIENT_INTENT_TOKEN = "PATIENT";

    @InjectView(R.id.prescriptions_list)
    ListView prescriptionsList;

    private PrescriptionsListAdapter listAdapter;

    private Patient patient;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_patient_prescriptions);
        ButterKnife.inject(this);

        patient = getIntent().getParcelableExtra(PATIENT_INTENT_TOKEN);

        setTitle(String.format(getString(R.string.title_activity_patient_prescriptions), patient.getFullName()));

        final Ordering prescriptionOrdering = Ordering.from(new Prescription.MedicationNameComparator());
        final List<Prescription> prescriptionsSorted = prescriptionOrdering.sortedCopy(patient.getPrescriptions());
        listAdapter = new PrescriptionsListAdapter(this, prescriptionsSorted, prescriptionOrdering);
        prescriptionsList.setAdapter(listAdapter);
    }

    @OnClick(R.id.assign_medication_button)
    public void assignNewMedication() {
        final EditText input = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle(R.string.assign_new_medication_prompt_title)
                .setMessage(R.string.assign_new_medication_prompt_message)
                .setView(input)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int whichButton) {
                        final String newMedicationName = input.getText().toString();
                        showProgress(true);
                        new AssignMedicationTask(patient.getId(), newMedicationName).execute();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @OnClick(R.id.unassign_medication_button)
    public void unassignMedication() {
        final Collection<Prescription> selectedPrescriptions = listAdapter.getSelectedPrescriptions();
        if (selectedPrescriptions.isEmpty()) {
        	showInformationDialog(getString(R.string.error_no_medications_to_unassign));
        } else if (selectedPrescriptions.size() == listAdapter.getCount()) {
        	showInformationDialog(getString(R.string.error_cannot_unassign_all_medications));
        } else {
            showProgress(true);
        	new UnassignMedicationsTask(patient.getId(), selectedPrescriptions).execute();
        }
    }

    class PrescriptionsListAdapter extends ArrayAdapter<Prescription> {

        private final Multimap<Boolean, Prescription> prescriptionBySelectedFlagMap = HashMultimap.create();
        private final Map<Prescription, Boolean> selectedFlagByPrescriptionMap = Maps.newHashMap();

        private final Comparator<Prescription> prescriptionComparator;

        public PrescriptionsListAdapter(
                final Context context,
                final List<Prescription> prescriptions,
                final Comparator<Prescription> prescriptionComparator) {
            super(context, R.layout.list_doctor_patients_row, prescriptions);
            this.prescriptionComparator = prescriptionComparator;
            for (final Prescription prescription : prescriptions) {
                prescriptionBySelectedFlagMap.put(Boolean.FALSE, prescription);
                selectedFlagByPrescriptionMap.put(prescription, Boolean.FALSE);
            }
        }
        
        @Override
        public void add(final Prescription newPrescription) {
        	super.add(newPrescription);
            sort(prescriptionComparator);
        	prescriptionBySelectedFlagMap.put(Boolean.FALSE, newPrescription);
        	selectedFlagByPrescriptionMap.put(newPrescription, Boolean.FALSE);
        	notifyDataSetChanged();
        }
        
        public void remove(final Collection<Prescription> prescriptionsToDelete) {
        	for (final Prescription prescription : prescriptionsToDelete) {
        		super.remove(prescription);
        		prescriptionBySelectedFlagMap.remove(Boolean.TRUE, prescription);
        		selectedFlagByPrescriptionMap.remove(prescription);
        	}
        	notifyDataSetChanged();
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            final Prescription prescription = getItem(position);

            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.list_doctor_patient_prescriptions_row, parent, false);
                final ViewHolder viewHolder = new ViewHolder();
                viewHolder.medicationNameLabel = (TextView) view.findViewById(R.id.prescriptions_list_row_label);
                viewHolder.medicationSelectedCheckBox = (CheckBox) view.findViewById(R.id.prescriptions_list_row_checkbox);
                viewHolder.medicationSelectedCheckBox
                        .setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(final View v) {
                                final Prescription prescription = getItem(prescriptionsList.getPositionForView(v));
                                final boolean isChecked = ((CheckBox) v).isChecked();
                                selectedFlagByPrescriptionMap.put(prescription, isChecked);
                                prescriptionBySelectedFlagMap.put(isChecked, prescription);
                                prescriptionBySelectedFlagMap.remove(!isChecked, prescription);
                            }
                        });
                view.setTag(viewHolder);
            }

            final ViewHolder holder = (ViewHolder) view.getTag();
            holder.medicationNameLabel.setText(prescription.getMedicationName());
            holder.medicationSelectedCheckBox.setChecked(selectedFlagByPrescriptionMap.get(prescription));

            return view;
        }

        public Collection<Prescription> getSelectedPrescriptions() {
            return ImmutableList.copyOf(prescriptionBySelectedFlagMap.get(Boolean.TRUE));
        }

        class ViewHolder {
            protected TextView medicationNameLabel;
            protected CheckBox medicationSelectedCheckBox;
        }
    }

    private class AssignMedicationTask extends AsyncTask<Void, Void, Boolean> {

        private final long patientId;
        private final String medicationName;

        public AssignMedicationTask(final long patientId, final String medicationName) {
            this.patientId = patientId;
            this.medicationName = medicationName;
        }

        @Override
        protected Boolean doInBackground(final Void... params) {

            final PocketMdServiceApi service = ((PocketMdClientApplication) getApplication()).getPocketMdService();

            try {
                final Prescription newPrescription = service.assignMedication(patientId, medicationName);
                if (newPrescription == null) {
                	return false;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        patient.getPrescriptions().add(newPrescription);
                        listAdapter.add(newPrescription);
                    }
                });
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
                showInformationDialog(getString(R.string.error_assign_medication_failed));
            }
        }
    }

    private class UnassignMedicationsTask extends AsyncTask<Void, Void, Boolean> {

        private final long patientId;
        private final Collection<Prescription> prescriptionsToDelete;

        public UnassignMedicationsTask(final long patientId, final Collection<Prescription> prescriptionsToDelete) {
            this.patientId = patientId;
            this.prescriptionsToDelete = prescriptionsToDelete;
        }

        @Override
        protected Boolean doInBackground(final Void... params) {

            final PocketMdServiceApi service = ((PocketMdClientApplication) getApplication()).getPocketMdService();

            final Long[] prescriptionIds =
                    FluentIterable.from(prescriptionsToDelete)
                        .transform(new Function<Prescription, Long>() {
                            @Override
                            public Long apply(final Prescription input) {
                                return input.getId();
                            }
                        })
                    .toArray(Long.class);
            
            try {
                final boolean success = service.unassignMedications(patientId, prescriptionIds);
                if (success) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            patient.getPrescriptions().removeAll(prescriptionsToDelete);
                            listAdapter.remove(prescriptionsToDelete);
                        }
                    });
                }
                return success;
            } catch (final Exception e) {
                Log.e(TAG, "Cannot establish connection to the server", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            if (!success) {
                showInformationDialog(getString(R.string.error_unassign_medications_failed));
            }
        }
    }
}
