package org.coursera.android.capstone.client.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.MoreObjects;

import java.util.Comparator;
import java.util.Objects;

public class Prescription implements Parcelable {

	private long id;
	
	private String medicationName;
	
	private long patientId;

    public Prescription() {
    }

    public Prescription(final Parcel in) {
        id = in.readLong();
        medicationName = in.readString();
        patientId = in.readLong();
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

	public long getPatientId() {
		return patientId;
	}

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        final Prescription that = (Prescription) other;
        return Objects.equals(this.getId(), that.getId()) &&
               Objects.equals(this.getMedicationName(), that.getMedicationName()) &&
               Objects.equals(this.getPatientId(), that.getMedicationName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                medicationName,
                patientId);
    }

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("id", id)
				.add("medication", medicationName)
				.add("patientId", patientId)
				.toString();
	}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel out, final int flags) {
        out.writeLong(id);
        out.writeString(medicationName);
        out.writeLong(patientId);
    }

    public static final Parcelable.Creator<Prescription> CREATOR = new Parcelable.Creator<Prescription>() {
        public Prescription createFromParcel(final Parcel in) {
            return new Prescription(in);
        }

        public Prescription[] newArray(final int size) {
            return new Prescription[size];
        }
    };

    public static class MedicationNameComparator implements Comparator<Prescription> {
        @Override
        public int compare(final Prescription first, final Prescription second) {
            return first.getMedicationName().compareTo(second.getMedicationName());
        }
    }
}
