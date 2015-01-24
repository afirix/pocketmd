package org.coursera.android.capstone.server.impl.alert;

import java.util.Date;

public class DoctorAlert {

	public enum Type {
		PROLONGED_SEVERE_PAIN,
		PROLONGED_PAIN,
		PROLONGED_EATING_PROBLEMS;
	}

	private Type type;
	
	private Date time;
	
	private String message;

	public Type getType() {
		return type;
	}

	public void setType(final Type type) {
		this.type = type;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(final Date time) {
		this.time = time;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(final String message) {
		this.message = message;
	}

	public static class Builder {
		private Type type;
		private Date time;
		private String message;
		
		public Builder withType(final Type type) {
			this.type = type;
			return this;
		}
		
		public Builder withTime(final Date time) {
			this.time = time;
			return this;
		}
		
		public Builder withMessage(final String message) {
			this.message = message;
			return this;
		}
		
		public DoctorAlert build() {
			final DoctorAlert alert = new DoctorAlert();
			alert.type = type;
			alert.time = time;
			alert.message = message;
			return alert;
		}
	}
}
