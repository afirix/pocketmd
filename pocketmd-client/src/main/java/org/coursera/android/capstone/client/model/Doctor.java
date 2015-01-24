package org.coursera.android.capstone.client.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

public class Doctor implements Person {

	private long id;
	
	private String username;
	
	private String firstName;
	
	private String lastName;
	
	private Collection<Patient> patients;

    public Doctor() {
    }

    public Doctor(final Parcel in) {
        id = in.readLong();
        username = in.readString();
        firstName = in.readString();
        lastName = in.readString();

        final List<Patient> patientList = Lists.newArrayList();
        in.readTypedList(patientList, Patient.CREATOR);
        patients = patientList;
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

	public Collection<Patient> getPatients() {
		return patients;
	}

	public void setPatients(final Collection<Patient> patients) {
		this.patients = patients;
	}

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        final Doctor that = (Doctor) other;
        return Objects.equals(this.getId(), that.getId()) &&
               Objects.equals(this.getUsername(), that.getUsername()) &&
               Objects.equals(this.getFirstName(), that.getFirstName()) &&
               Objects.equals(this.getLastName(), that.getLastName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                username,
                firstName,
                lastName);
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
        out.writeTypedList(Lists.newArrayList(patients));
    }

    public static final Parcelable.Creator<Doctor> CREATOR = new Parcelable.Creator<Doctor>() {
        public Doctor createFromParcel(final Parcel in) {
            return new Doctor(in);
        }

        public Doctor[] newArray(final int size) {
            return new Doctor[size];
        }
    };
}
