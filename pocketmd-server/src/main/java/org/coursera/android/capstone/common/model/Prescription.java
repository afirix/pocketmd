package org.coursera.android.capstone.common.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.common.base.MoreObjects;

@Entity
@Table(name = "prescriptions")
public class Prescription {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "prescription_id", nullable = false)
	private long id;
	
	@Column(name = "medication_name", nullable = false, length = 50)
	private String medicationName;
	
	@Column(name = "patient_id", nullable = false, insertable = false, updatable = false)
	private long patientId;

	public long getId() {
		return id;
	}

	public String getMedicationName() {
		return medicationName;
	}

	public void setMedicationName(final String medicationName) {
		this.medicationName = medicationName;
	}

	public long getPatientId() {
		return patientId;
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("id", id)
				.add("medication", medicationName)
				.add("patientId", patientId)
				.toString();
	}
}
