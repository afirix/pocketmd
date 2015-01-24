package org.coursera.android.capstone.common.model;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.common.base.MoreObjects;

@Entity
@Table(name = "doctors")
public class Doctor implements Person {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "doctor_id", nullable = false)
	private long id;
	
	@Column(name = "username", nullable = false, length = 50)
	private String username;
	
	@Column(name = "first_name", nullable = false, length = 50)
	private String firstName;
	
	@Column(name = "last_name", nullable = false, length = 50)
	private String lastName;
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "doctor_id", nullable = false)
	private Collection<Patient> patients;

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

	public Collection<Patient> getPatients() {
		return patients;
	}

	public void setPatients(final Collection<Patient> patients) {
		this.patients = patients;
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("id", id)
				.add("name", String.format("%s %s", firstName, lastName))
				.toString();
	}
}
