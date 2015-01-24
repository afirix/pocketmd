package org.coursera.android.capstone.client.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;

public class CheckIn implements Parcelable {
	
	private long id;
	
	private PainSeverity painSeverity;
	
	private EatingProblems eatingProblems;

	private Date dateTime;
	
	private Map<String, MedicationIntake> medications;
	
	private String fileS3Url;
	
	private long patientId;

    public CheckIn() {
    }

    public CheckIn(final Parcel in) {
        id = in.readLong();
        painSeverity = PainSeverity.valueOf(in.readString());
        eatingProblems = EatingProblems.valueOf(in.readString());
        dateTime = new Date(in.readLong());
        fileS3Url = (String) in.readValue(String.class.getClassLoader());
        patientId = in.readLong();

        final int medicationsSize = in.readInt();
        medications = Maps.newHashMapWithExpectedSize(medicationsSize);
        for (int i = 0; i < medicationsSize; i++) {
            medications.put(in.readString(), in.<MedicationIntake>readParcelable(MedicationIntake.class.getClassLoader()));
        }
    }

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
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        final CheckIn that = (CheckIn) other;
        return Objects.equals(this.getId(), that.getId()) &&
               Objects.equals(this.getPainSeverity(), that.getPainSeverity()) &&
               Objects.equals(this.getEatingProblems(), that.getEatingProblems()) &&
               Objects.equals(this.getDateTime(), that.getDateTime()) &&
               Objects.equals(this.getPatientId(), that.getPatientId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                painSeverity,
                eatingProblems,
                dateTime,
                patientId);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel out, final int flags) {
        out.writeLong(id);
        out.writeString(painSeverity.name());
        out.writeString(eatingProblems.name());
        out.writeLong(dateTime.getTime());
        out.writeValue(fileS3Url);
        out.writeLong(patientId);

        out.writeInt(medications.size());
        for (final Map.Entry<String, MedicationIntake> entry : medications.entrySet()) {
            out.writeString(entry.getKey());
            out.writeParcelable(entry.getValue(), flags);
        }
    }

    public static final Parcelable.Creator<CheckIn> CREATOR = new Parcelable.Creator<CheckIn>() {
        public CheckIn createFromParcel(final Parcel in) {
            return new CheckIn(in);
        }

        public CheckIn[] newArray(final int size) {
            return new CheckIn[size];
        }
    };

	public static class Builder {
        private Date dateTime;
		private PainSeverity painSeverity;
		private EatingProblems eatingProblems;
		private Map<String, MedicationIntake> medications;

        public Builder withDateTime(final Date dateTime) {
            this.dateTime = dateTime;
            return this;
        }

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
            checkIn.setDateTime(dateTime);
			checkIn.setPainSeverity(painSeverity);
			checkIn.setEatingProblems(eatingProblems);
			checkIn.setMedications(medications);
			return checkIn;
		}
	}

    public static class TimeComparator implements Comparator<CheckIn> {
        @Override
        public int compare(final CheckIn first, final CheckIn second) {
            return first.getDateTime().compareTo(second.getDateTime());
        }
    }
}
