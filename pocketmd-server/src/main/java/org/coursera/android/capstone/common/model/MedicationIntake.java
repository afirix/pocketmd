package org.coursera.android.capstone.common.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.google.common.base.Preconditions;

@Entity
@Table(name = "checkin_medications")
public class MedicationIntake {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "checkin_medication_id", nullable = false)
	private long id;
	
	@Column(name = "medication_name", nullable = false)
	private String medicationName;
	
	@Column(name = "medication_taken", nullable = false)
	private boolean medicationTaken;
	
	@Column(name = "intake_time", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date intakeTime;
	
	public MedicationIntake() {
	}
	
	public MedicationIntake(final boolean medicationTaken) {
		this.medicationTaken = medicationTaken;
	}

	public MedicationIntake(final boolean medicationTaken, final Date intakeTime) {
		Preconditions.checkArgument(medicationTaken && intakeTime != null, "Intake time should be set");
		this.medicationTaken = medicationTaken;
		this.intakeTime = intakeTime;
	}

	public long getId() {
		return id;
	}

	public String getMedicationName() {
		return medicationName;
	}

	public void setMedicationName(final String medicationName) {
		this.medicationName = medicationName;
	}

	public boolean isMedicationTaken() {
		return medicationTaken;
	}

	public void setMedicationTaken(final boolean medicationTaken) {
		this.medicationTaken = medicationTaken;
	}

	public Date getIntakeTime() {
		return intakeTime;
	}

	public void setIntakeTime(final Date intakeTime) {
		this.intakeTime = intakeTime;
	}
}
