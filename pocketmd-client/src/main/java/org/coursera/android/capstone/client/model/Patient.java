package org.coursera.android.capstone.client.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

public class Patient implements Person {

	private long id;

	private String username;

	private String firstName;
	
	private String lastName;
	
	private Date dateOfBirth;
	
	private long recordNumber;

	private Collection<CheckIn> checkIns;

	private Collection<Prescription> prescriptions;

	private long doctorId;

    public Patient() {
    }

    public Patient(final Parcel in) {
        id = in.readLong();
        username = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        dateOfBirth = new Date(in.readLong());
        recordNumber = in.readLong();
        doctorId = in.readLong();

        final List<CheckIn> checkInList = Lists.newArrayList();
        in.readTypedList(checkInList, CheckIn.CREATOR);
        checkIns = checkInList;

        final List<Prescription> prescriptionList = Lists.newArrayList();
        in.readTypedList(prescriptionList, Prescription.CREATOR);
        prescriptions = prescriptionList;
    }

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
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        final Patient that = (Patient) other;
        return Objects.equals(this.getId(), that.getId()) &&
               Objects.equals(this.getUsername(), that.getUsername()) &&
               Objects.equals(this.getFirstName(), that.getFirstName()) &&
               Objects.equals(this.getLastName(), that.getLastName()) &&
               Objects.equals(this.getDateOfBirth(), that.getDateOfBirth()) &&
               Objects.equals(this.getRecordNumber(), that.getRecordNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                username,
                firstName,
                lastName,
                dateOfBirth,
                recordNumber);
    }

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("id", id)
				.add("name", String.format("%s %s", firstName, lastName))
				.toString();
	}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel out, final int flags) {
        out.writeLong(id);
        out.writeString(username);
        out.writeString(firstName);
        out.writeString(lastName);
        out.writeLong(dateOfBirth.getTime());
        out.writeLong(recordNumber);
        out.writeLong(doctorId);
        out.writeTypedList(Lists.newArrayList(checkIns));
        out.writeTypedList(Lists.newArrayList(prescriptions));
    }

    public static final Parcelable.Creator<Patient> CREATOR = new Parcelable.Creator<Patient>() {
        public Patient createFromParcel(final Parcel in) {
            return new Patient(in);
        }

        public Patient[] newArray(final int size) {
            return new Patient[size];
        }
    };
	
	public static class FullNameComparator implements Comparator<Patient> {
		@Override
		public int compare(final Patient first, final Patient second) {
			return first.getFullName().compareTo(second.getFullName());
		}
	}
}
