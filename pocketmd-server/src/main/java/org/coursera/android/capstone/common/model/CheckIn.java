package org.coursera.android.capstone.common.model;

import java.util.Date;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.google.common.base.MoreObjects;

@Entity
@Table(name = "checkins")
public class CheckIn {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "checkin_id", nullable = false)
	private long id;
	
	@Column(name = "pain_severity", nullable = false)
	@Enumerated(EnumType.STRING)
	private PainSeverity painSeverity;
	
	@Column(name = "eating_problems", nullable = false)
	@Enumerated(EnumType.STRING)
	private EatingProblems eatingProblems;
	
	@Column(name = "time", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateTime;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@JoinColumn(name = "checkin_id", referencedColumnName = "checkin_id", nullable = false)
	@MapKey(name = "medicationName")
	private Map<String, MedicationIntake> medications;
	
	@Column(name = "file_s3_url", nullable = true, length = 100)
	private String fileS3Url;
	
	@Column(name = "patient_id", nullable = false, insertable = false, updatable = false)
	private long patientId;

	public long getId() {
		return id;
	}
	
	public PainSeverity getPainSeverity() {
		return painSeverity;
	}

	public void setPainSeverity(final PainSeverity painSeverity) {
		this.painSeverity = painSeverity;
	}

	public EatingProblems getEatingProblems() {
		return eatingProblems;
	}

	public void setEatingProblems(final EatingProblems eatingProblems) {
		this.eatingProblems = eatingProblems;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(final Date dateTime) {
		this.dateTime = dateTime;
	}

	public Map<String, MedicationIntake> getMedications() {
		return medications;
	}

	public void setMedications(final Map<String, MedicationIntake> medications) {
		this.medications = medications;
	}
	
	public String getFileS3Url() {
		return fileS3Url;
	}

	public void setFileS3Url(final String fileS3Url) {
		this.fileS3Url = fileS3Url;
	}

	public long getPatientId() {
		return patientId;
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("id", id)
				.add("painSeverity", painSeverity)
				.add("eatingProblems", eatingProblems)
				.add("patientId", patientId)
				.toString();
	}

	public static class Builder {
		private PainSeverity painSeverity;
		private EatingProblems eatingProblems;
		private Map<String, MedicationIntake> medications;
		
		public Builder withPainSeverity(final PainSeverity painSeverity) {
			this.painSeverity = painSeverity;
			return this;
		}
		
		public Builder withEatingProblems(final EatingProblems eatingProblems) {
			this.eatingProblems = eatingProblems;
			return this;
		}
		
		public Builder withMedications(final Map<String, MedicationIntake> medications) {
			this.medications = medications;
			return this;
		}
		
		public CheckIn build() {
			final CheckIn checkIn = new CheckIn();
			checkIn.setPainSeverity(painSeverity);
			checkIn.setEatingProblems(eatingProblems);
			checkIn.setMedications(medications);
			return checkIn;
		}
	}
}
