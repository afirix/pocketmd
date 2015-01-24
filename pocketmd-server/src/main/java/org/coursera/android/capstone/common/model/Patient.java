package org.coursera.android.capstone.common.model;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.google.common.base.MoreObjects;

@Entity
@Table(name = "patients")
public class Patient implements Person {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "patient_id", nullable = false)
	private long id;

	@Column(name = "username", nullable = false, length = 50)
	private String username;

	@Column(name = "first_name", nullable = false, length = 50)
	private String firstName;
	
	@Column(name = "last_name", nullable = false, length = 50)
	private String lastName;
	
	@Column(name = "date_of_birth", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date dateOfBirth;
	
	@Column(name = "record_number", nullable = false)
	private long recordNumber;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "patient_id", nullable = false)
	private Collection<CheckIn> checkIns;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "patient_id", nullable = false)
	private Collection<Prescription> prescriptions;

	@Column(name = "doctor_id", nullable = false, insertable = false, updatable = false)
	private long doctorId;

	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	@Override
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	@Override
	public String getLastName() {
		return lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}
	
	@Override
	public String getFullName() {
		return String.format("%s %s", getFirstName(), getLastName());
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(final Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public long getRecordNumber() {
		return recordNumber;
	}

	public void setRecordNumber(final long recordNumber) {
		this.recordNumber = recordNumber;
	}

	public Collection<CheckIn> getCheckIns() {
		return checkIns;
	}

	public void setCheckIns(final Collection<CheckIn> checkIns) {
		this.checkIns = checkIns;
	}

	public Collection<Prescription> getPrescriptions() {
		return prescriptions;
	}

	public void setPrescriptions(final Collection<Prescription> prescriptions) {
		this.prescriptions = prescriptions;
	}
	
	public long getDoctorId() {
		return doctorId;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("id", id)
				.add("name", String.format("%s %s", firstName, lastName))
				.toString();
	}
}
