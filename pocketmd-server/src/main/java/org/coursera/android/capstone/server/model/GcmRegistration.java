package org.coursera.android.capstone.server.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.google.common.base.Charsets;

@Entity
@Table(name = "gcm_registrations")
public class GcmRegistration {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "record_id", nullable = false)
	private long recordId;
	
	@Column(name = "user_id", nullable = false)
	private long userId;
	
	@Column(name = "registration_id", nullable = false)
	@Lob
	private byte[] registrationId;
	
	@Column(name = "registration_id_hash", nullable = false)
	@Lob
	private byte[] registrationIdHash;

	public GcmRegistration() {
	}
	
	public GcmRegistration(
			final long userId,
			final byte[] registrationId,
			final byte[] registrationIdHash) {
		this.userId = userId;
		this.registrationId = registrationId;
		this.registrationIdHash = registrationIdHash;
	}

	public long getRecordId() {
		return recordId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(final long userId) {
		this.userId = userId;
	}

	public byte[] getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(final byte[] registrationId) {
		this.registrationId = registrationId;
	}
	
	public String getRegistrationIdAsString() {
		return new String(registrationId, Charsets.UTF_8);
	}

	public byte[] getRegistrationIdHash() {
		return registrationIdHash;
	}

	public void setRegistrationIdHash(final byte[] registrationIdHash) {
		this.registrationIdHash = registrationIdHash;
	}
}
