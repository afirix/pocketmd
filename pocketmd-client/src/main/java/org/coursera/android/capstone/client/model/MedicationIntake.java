package org.coursera.android.capstone.client.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

public class MedicationIntake implements Parcelable {
	
	private long id;
	
	private String medicationName;
	
	private boolean medicationTaken;
	
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

    public MedicationIntake(final Parcel in) {
        id = in.readLong();
        medicationName = in.readString();
        medicationTaken = (in.readByte() != 0);

        final long intakeTimeLong = in.readLong();
        intakeTime = (intakeTimeLong < 0) ? null : new Date(intakeTimeLong);
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

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        final MedicationIntake that = (MedicationIntake) other;
        return Objects.equals(this.getId(), that.getId()) &&
               Objects.equals(this.getMedicationName(), that.getMedicationName()) &&
               Objects.equals(this.isMedicationTaken(), that.isMedicationTaken()) &&
               Objects.equals(this.getIntakeTime(), that.getIntakeTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                medicationName,
                medicationTaken,
                intakeTime);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("medicationName", medicationName)
                .add("medicationTaken", medicationTaken)
                .add("intakeTime", intakeTime)
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
        out.writeByte((byte) (medicationTaken ? 1 : 0));
        out.writeLong((intakeTime == null) ? -1 : intakeTime.getTime());
    }

    public static final Parcelable.Creator<MedicationIntake> CREATOR = new Parcelable.Creator<MedicationIntake>() {
        public MedicationIntake createFromParcel(final Parcel in) {
            return new MedicationIntake(in);
        }

        public MedicationIntake[] newArray(final int size) {
            return new MedicationIntake[size];
        }
    };

    public static class MedicationIntakeComparator implements Comparator<MedicationIntake> {
        @Override
        public int compare(final MedicationIntake first, final MedicationIntake second) {
            return first.getMedicationName().compareTo(second.getMedicationName());
        }
    }
}
